package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.iai.traffic.domain.traffic.*
import com.iai.traffic.repository.traffic.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j
import io.micronaut.discovery.event.ServiceStartedEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async

import javax.inject.Inject
import javax.inject.Singleton

import com.fasterxml.jackson.databind.ObjectMapper

@Singleton
@Slf4j
class BluetoothDataService {

    @Inject
    TravelTimeNewRepository travelTimeNewRepository
    @Inject
    TravelSegmentNewRepository travelSegmentNewRepository
    @Inject
    WazeAlertRepository wazeAlertRepository
    @Inject
    WazeRoadRepository wazeRoadRepository
    @Inject
    WazeTypeRepository wazeTypeRepository
    @Inject
    TravelTimeBaselineRepository travelTimeBaselineRepository
    @Inject
    TravelTimeBaselineWoIncidentRepository travelTimeBaselineWoIncidentRepository
    @Inject
    TrafficFlowRepository trafficFlowRepository
    @Inject
    TrafficDeviceRepository trafficDeviceRepository
    @Inject
    TrafficBreakdownParamRepository trafficBreakdownParamRepository
    @Inject
    RedisCacheService mRedisCacheService


    // Misc
    def sensorInit = false
    def fetchCount = 0
    def fetching = false
    def lastCallTime = 0
    // Constant
    def deviceJSON = []
    def bpDevices = []
    def sensorsJSON = []
    def segmentsFullDayJSON = []
    def wazeAlertsFullDayJSON = []
    def devicesFullDayJSON = []
    // Travel Time Requests
    def travelTimeBaseline = []
    def travelTimeBaselineWoIncident = []
    def travelTimeCurrFullJSON = []

    def mWazeRoadMap = [:]
    def mWazeTypeMap = [:]

    final MINUTES_IN_A_DAY = 1440

    @EventListener
    @Async
    void init(final ServiceStartedEvent event) {
        //processFullDayData()
    }

    def processFullDayData() {
        // Full Day Data
        long callTime = System.currentTimeMillis()
        if (callTime > (lastCallTime + (5 * 60 * 1000)) && !fetching) {
            log.info('Initializing bluetooth data...')
            fetching = true
            lastCallTime = callTime

            Date dayTime = new Date()
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime = AppUtils.getEndOfDay(dayTime)

           // log.info('Initializing Full Day Data for Waze Alerts, Segments, and Devices')
            wazeAlertsFullDayJSON = fetchWazeAlerts(startTime, endTime)
            segmentsFullDayJSON = fetchSegments(startTime, endTime)
            devicesFullDayJSON = fetchDevices(startTime, endTime)
            log.info('Successful Initialization {}', ++fetchCount)
            fetching = false
        }
        return !fetching
    }

    // Segment Methods
    def fetchSegments(Date startTime, Date endTime) {
        String key = "segments_${AppUtils.formatDateToDay(startTime)}"

        if (!AppUtils.isToday(startTime)) {
            def data = mRedisCacheService.getData(key)
            if (data != null) {
                log.info("Pulled data from Redis: $key")
                ObjectMapper objectMapper = new ObjectMapper()
                def json = objectMapper.readValue(data, List.class)

                // Extract data for the detector
                return json
            }
        }


        def t0 = System.currentTimeMillis()
        List<TravelSegmentNew> travelSegmentList = travelSegmentNewRepository.findAll()
        def t1 = System.currentTimeMillis()

        List<TravelTimeNew> ttList = travelTimeNewRepository.findEvery15Min(startTime, endTime)
        def t2 = System.currentTimeMillis()
        //log.info('Get Segment Data from {}, and ending at {}, received {} entries', startTime, endTime, ttList.size())

        def travelTimeMap = [:]
        ttList.each { tt->
            def linkId = tt.linkId
            int segmentLevel = getLevel(tt)
            def item = [
                    calculationTimestamp: [
                            value: tt.calculationTimestamp,
                            text : tt.calculationTimestamp.toString()
                    ],
                    level               : segmentLevel,
                    speedMph            : tt.speedMph,
                    meanTravelTimeSecs  : tt.meanTravelTimeSecs,
                    freeFlowRunTimeSecs : tt.freeFlowRunTimeSecs
            ]

            def travelTimeList = travelTimeMap.get(linkId)
            if (travelTimeList == null) {
                travelTimeList = []
                travelTimeMap << [(linkId): travelTimeList]
            }
            travelTimeList << item
        }

        def t3 = System.currentTimeMillis()

        ObjectMapper objectMapper = new ObjectMapper()

        def segments = []
        travelSegmentList.eachWithIndex { segment, idx->
            def timelineData = travelTimeMap[segment.linkId]
            if (timelineData != null) {
                def currTT = timelineData.get(0)
                def coordinates = parseCoordinates(objectMapper, segment.coordinates)

                segments.add(
                    [
                        info: [
                            id          : idx,
                            linkId      : segment.linkId,
                            name        : segment.shortName,
                            description : segment.description,
                            lat         : coordinates[0].lat,
                            lng         : coordinates[0].lng,
                            route       : segment.route,
                            startPoint  : segment.startPoint,
                            endPoint    : segment.endPoint,
                            freeFlowTime: segment.freeFlowTime,
                            coordinates : coordinates,
                            distance    : segment.distance
                        ],
                        travelTime: [
                            calculationTimestamp: currTT.calculationTimestamp,
                            level: currTT.level
                        ],
                        timeline  : timelineData
                    ]
                )
            }
        }

        def t4 = System.currentTimeMillis()
        if (!AppUtils.isToday(startTime)) {
            ObjectMapper objectMapper2 = new ObjectMapper()
            String segmentsStr = objectMapper2.writeValueAsString(segments)
            mRedisCacheService.putData(key, segmentsStr,  12 * 3600L)
            log.info("Saved data to Redis: $key")
        }

        //log.info("Done Processing Segment Data: ${segments.size()} entries, time spent:  ${t1-t0}-${t2-t1}-${t3-t2}-${t4-t3}")
        return segments
    }

    def getSegmentsJSON() {
        return segmentsJSON
    }

    def getSegmentsFullJSON() {
        return segmentsFullDayJSON
    }

    def parseCoordinates(ObjectMapper objectMapper, String coords) {
        // Transform coordinates to polyline path
        def coordinates = objectMapper.readValue(coords, new TypeReference<List<List<Double>>>() {})
        return coordinates.collect { coord ->
            return [lat: coord[1], lng: coord[0]]
        }
    }

    def getLevel(tt) {
        def tti = tt.meanTravelTimeSecs / tt.freeFlowRunTimeSecs
        int segmentLevel = -3

        if (tt.score < 50) {
            segmentLevel = 0
        } else {
            if (tti <= 1.25) {
                segmentLevel = 1
            } else if (tti <= 1.5)
                segmentLevel = 2
            else if (tti <= 2)
                segmentLevel = 3
            else if (tti <= 2.5)
                segmentLevel = 4
            else if (tti <= 4)
                segmentLevel = 5
            else
                segmentLevel = 6
        }
        return segmentLevel
    }

    // Waze Methods
    def fetchWazeAlerts(startTime, endTime) {
        String key = "WazeAlerts_${AppUtils.formatDateToDay(startTime)}"

        if (!AppUtils.isToday(startTime)) {
            def data = mRedisCacheService.getData(key)
            if (data != null) {
                log.info("Pulled data from Redis: $key")
                ObjectMapper objectMapper = new ObjectMapper()
                return objectMapper.readValue(data, List.class)
            }
        }


        def t0 = System.currentTimeMillis()
        List<WazeAlert> wazeList = wazeAlertRepository.findByUpdatedAtBetweenOrderByUpdatedAt(startTime, endTime)
        def t1 = System.currentTimeMillis()
       // log.info('Get Waze Alerts from {}, and ending at {}, received {} entries.', startTime, endTime, wazeList.size())

        if (mWazeRoadMap.isEmpty()) {
            List<WazeRoad> roadList = wazeRoadRepository.findAll()
            roadList.each {
                mWazeRoadMap << [(it.roadId): it]
            }
        }
        def t2 = System.currentTimeMillis()

        if (mWazeTypeMap.isEmpty()) {
            List<WazeType> typeList = wazeTypeRepository.findAll()
            typeList.each {
                mWazeTypeMap << [(it.alertId): it]
            }
        }
        def t3 = System.currentTimeMillis()

        ObjectMapper objectMapper = new ObjectMapper()

        // Only focus on critical alert types: [10, 20, 90, 100]
        def importantAlertTypes = [10, 20, 90, 100] as Set

        def wazeAlerts = []
        wazeList.eachWithIndex { alert, idx->
            if (importantAlertTypes.contains(alert.alertType)) {
                def road = mWazeRoadMap.get(alert.roadType)
                def roadName = road != null ? road.name : null

                def type = mWazeTypeMap.get(alert.alertType)
                def alertName = type != null ? type.name : null

                def subtype = mWazeTypeMap.get(alert.subtype)
                def roadSubtype = subtype != null ? subtype.name : null

                def address = objectMapper.readValue(alert.address, HashMap.class)

                wazeAlerts.add(
                        [       id         : idx,
                                markerId   : getWazeMarkerId(alert.subtype),
                                alertTime  : alert.updatedAt.toString(),
                                alertTimeTS: alert.updatedAt,
                                address    : address,
                                description: alert.description,
                                position   : [lat: alert.latitude, lng: alert.longitude],
                                lat        : alert.latitude,
                                lng        : alert.longitude,
                                reportRating: alert.reportRating,
                                confidence : alert.confidence,
                                reliability: alert.reliability,
                                thumbsUp   : alert.thumbsUp,
                                alertType  : [id: alert.alertType, name: alertName],
                                subType    : [id: alert.subtype, name: roadSubtype],
                                roadType   : [id: alert.roadType, name: roadName],
                        ]
                )
            }
        }

        if (!AppUtils.isToday(startTime)) {
            ObjectMapper objectMapper2 = new ObjectMapper()
            String jsonStr = objectMapper2.writeValueAsString(wazeAlerts)
            mRedisCacheService.putData(key, jsonStr, 12 * 3600L)
            log.info("Saved data to Redis: $key")
        }

        def t4 = System.currentTimeMillis()
        //log.info("Done Processing Waze Data: ${wazeAlerts.size()} entries, time spent: ${t1-t0}-${t2-t1}-${t3-t2}-${t4-t3}")

        return wazeAlerts
    }

    def getWazeMarkerId(id) {
        // Accident
        if (id >= 10 && id <= 13)
            return 1
        // Jam
        else if (id >= 20 && id <= 25)
            return 2
        // Construction
        else if (id >= 90 && id <= 91)
            return 3
        // Closure
        else if (id >= 100 && id <= 104)
            return 4
        // Hazard
        else return 0
    }

    def getWazeAlertsJSON() {
        return wazeAlertsJSON
    }

    def getWazeAlertsFullDayJSON() {
        return wazeAlertsFullDayJSON
    }

    // Traffic Flow Methods
    def getSegmentHistoricalWIncident(String linkId) {
        List<TravelTimeBaseline> baselineList = travelTimeBaselineRepository.findByLinkId(linkId)
        travelTimeBaseline = []
        int id = 1
        if (baselineList?.size()) {
            for (baseline in baselineList) {
                travelTimeBaseline.add(
                        [
                                id            : id,
                                dateCreated   : baseline.dateCreated,
                                linkId        : baseline.linkId,
                                timeOfDay     : baseline.timeOfDay,
                                timeIndex     : baseline.timeIndex,
                                avgLinkSpeed  : baseline.avgLinkSpeed,
                                stdLinkSpeed  : baseline.stdLinkSpeed,
                                meanTravelTime: baseline.meanTravelTime,
                                stdTravelTime : baseline.stdTravelTime,
                                maxTravelTime : baseline.maxTravelTime,
                                minTravelTime : baseline.minTravelTime,
                                pc25TravelTime: baseline.pc25TravelTime,
                                pc75TravelTime: baseline.pc75TravelTime,
                        ]
                )
                id++
            }
        }
        return travelTimeBaseline
    }

    def getSegmentHistoricalWoIncident(String linkId) {
        List<TravelTimeBaselineWoIncident> baselineWoIncidentList = travelTimeBaselineWoIncidentRepository.findByLinkId(linkId)
        travelTimeBaselineWoIncident = []
        int id = 1
        if (baselineWoIncidentList?.size()) {
            for (baseline in baselineWoIncidentList) {
                travelTimeBaselineWoIncident.add(
                        [
                                id            : id,
                                linkId        : baseline.linkId,
                                linkName      : baseline.linkName,
                                timeOfDay     : baseline.timeOfDay,
                                baseTravelTime: baseline.baseTravelTime,
                                avgLinkSpeed  : baseline.avgLinkSpeed,
                                stdLinkSpeed  : baseline.stdLinkSpeed,
                                meanTravelTime: baseline.meanTravelTime,
                                stdTravelTime : baseline.stdTravelTime,
                                maxTravelTime : baseline.maxTravelTime,
                                minTravelTime : baseline.minTravelTime,
                                pc25TravelTime: baseline.pc25TravelTime,
                                pc75TravelTime: baseline.pc75TravelTime,
                                sampleSize    : baseline.sampleSize
                        ]
                )
                id++
            }
        }
        return travelTimeBaselineWoIncident
    }

    def getCurrentTT(String linkId, Date startTime, Date endTime) {
        //log.info('Get travel time data for segment id: {}, Starting from {}, and ending at {}', linkId, startTime, endTime)
        List<TravelTimeNew> currTTList = travelTimeNewRepository.findByLinkIdAndDateCreatedBetween(linkId, startTime, endTime)
        //log.info("Entries Found: ${currTTList.size()}")
        def currTT = []
        int id = 1
        if (currTTList?.size()) {
            for (tt in currTTList) {
                String timeOfDay = AppUtils.formatDateHHMM(tt.dateCreated)
                currTT.add(
                        [
                                timeOfDay: timeOfDay,
                                meanTT   : tt.meanTravelTimeSecs,
                                pctl25TT : tt.pctl25thSecs,
                                pctl75TT : tt.pctl75thSecs,
                                minTT    : tt.minTravelTimeSecs,
                                maxTT    : tt.maxTravelTimeSecs
                        ]
                )
                id++
            }
        }
        currTT = currTT.sort { it.dateCreated }
        return currTT
    }

    def fetchFlow(startTime, endTime) {
        String key = "devices_${AppUtils.formatDateToDay(startTime)}"

        if (!AppUtils.isToday(startTime)) {
            def data = mRedisCacheService.getData(key)
            if (data != null) {
                log.info("Pulled data from Redis: $key")
                ObjectMapper objectMapper = new ObjectMapper()
                def json = objectMapper.readValue(data, Map.class)

                // Extract data for the detector
                return json
            }
        }

        def t0 = System.currentTimeMillis()
        List<TrafficFlow> flowList = trafficFlowRepository.findEvery20Min(startTime, endTime)
        def t1 = System.currentTimeMillis()
        //log.info('Get Flow Data from {}, and ending at {}, now processing: {} entries', startTime, endTime, flowList.size())

        def flowData = [:]
        flowList.each { x->
            if (x.flowStatus > 0) {
                int statusLevel = getTrafficStatusLevelByOccupancy(x.occupancy, x.flowStatus)
                String statusColor = getTrafficStatusColorByLevel(statusLevel)

                def item = [
                                createdTime: x.createdTime,
                                deviceId   : x.deviceId,
                                occupancy  : x.occupancy,
                                speed      : x.speed,
                                volume     : x.volume,
                                direction  : x.direction,
                                status     : [level: statusLevel, color: statusColor]
                        ]
                def flowItemList = flowData.get(x.deviceId)
                if (flowItemList == null) {
                    flowItemList = [ item ]
                    flowData << [(x.deviceId): flowItemList]
                } else {
                    flowItemList << item
                }
            }
        }

        def t2 = System.currentTimeMillis()
        if (!AppUtils.isToday(startTime)) {
            ObjectMapper objectMapper2 = new ObjectMapper()
            String flowDataStr = objectMapper2.writeValueAsString(flowData)
            mRedisCacheService.putData(key, flowDataStr, 12 * 3600L)
            log.info("Saved data to Redis: $key")
        }
        def t3 = System.currentTimeMillis()

        log.info("Done Processing Flow Data: ${ flowData.size() } entries, time spent : ${t1-t0}-${t2-t1}-${t3-t2}")

        return flowData
    }

    def getTrafficStatusColorByLevel(int level) {
        if (level == -1) {
            return '#808080'
        } else {
            def colors = ['#339900', '#00ff33', '#ffff00', '#ffc031', '#fa7a28', '#ff0000', '#c00000']
            return colors[level]
        }
    }

    def getTrafficStatusLevelByOccupancy(occupancy, flowStatus) {
        int level = -1
        if (occupancy != null && flowStatus != 0) {
            if (occupancy <= 8) level = 0
            else if (occupancy <= 15) level = 1
            else if (occupancy <= 25) level = 2
            else if (occupancy <= 35) level = 3
            else if (occupancy <= 45) level = 4
            else if (occupancy <= 95) level = 5
            else level = 6
        }
        return level
    }

    // Sensor Methods

    def parseSensors() {
        String csvFile = '/data/sensor_loc.csv'
        BufferedReader br = null
        String line = ''
        sensorsJSON = []
        int i = 1
        int id = 1
        try {
            InputStream is = BluetoothDataService.class.getResourceAsStream(csvFile)
            br = new BufferedReader(new InputStreamReader(is))
            while ((line = br.readLine()) != null) {
                String[] info = line.split(',')
                if (i > 1) {
                    sensorsJSON.add(
                            [
                                    id      : id,
                                    node    : info[0],
                                    position: [
                                            lat: Float.parseFloat(info[1]),
                                            lng: Float.parseFloat(info[2])
                                    ],
                                    lat     : Float.parseFloat(info[1]),
                                    lng     : Float.parseFloat(info[2])
                            ]
                    )
                    id++
                }
                i++
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
    }

    def getSensorsJSON() {
        if (!sensorInit) {
            parseBPDevices()
            parseSensors()
            log.info('Successful initialized Breakdown Probability Devices & Sensors')
            sensorInit = true
        }
        return sensorsJSON
    }

    // Devices Methods
    def fetchDevices(startTime, endTime) {
        def recentFlowData = fetchFlow(startTime, endTime)
        List<TrafficDevice> deviceList = trafficDeviceRepository.findAll()

        def deviceData = []
        for (device in deviceList) {
            def flowData = []
            def status = [level: 0, color: '#339900' ]

            def flow = recentFlowData.get(device.deviceId.intValue())
            if (flow != null) {
                flowData.addAll(flow)
                flowData.sort { it.createdTime }
                def maxStatus = flowData.max {it.status.level }.status
                if (maxStatus.level > 0) {
                    status = maxStatus
                }
            }

            deviceData.add(
                [
                        county      : device.county,
                        deviceId    : device.deviceId,
                        lanes       : device.lanes,
                        lat         : device.lat,
                        lng         : device.lon,
                        position    : [lat: device.lat,lng: device.lon],
                        stationCount: device.stationCount,
                        title       : device.title,
                        type        : device.type,
                        uid         : device.uid,
                        flowData    : flowData,
                        status      : status,
                ]
            )
        }

        return deviceData
    }

    def getDevicesFullJSON() {
        return devicesFullDayJSON
    }

    // Breakdown Probability Methods
    def parseBPDevices() {
        List<TrafficBreakdownParam> bpData = trafficBreakdownParamRepository.findByIdGreaterThan(0)
        bpDevices = []
        for (TrafficBreakdownParam d in bpData) {
            bpDevices.add(
                    [
                            deviceId : d.deviceId,
                            route    : d.route,
                            direction: d.direction,
                            p1       : d.p1,
                            p2       : d.p2,
                            p3       : d.p3,
                    ]
            )
        }
    }

    def getBreakdownProb(Integer deviceId, String dir, Date startTime, Date endTime) {
        log.info('Get Traffic Flow BP Data Starting from {}, and ending at {}', startTime, endTime)
        List<TrafficFlow> trafficFlowData = trafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, dir, startTime, endTime)

        int i
        def breakdownProb = []
        List<Integer> volList = new ArrayList<Integer>()
        int j
        for (j = 0; j < 60; j++) {
            if (j >= trafficFlowData.size()) {
                break
            }
            volList.add(trafficFlowData[j].volume)
        }
        j = 0
        for (TrafficFlow tf in trafficFlowData) {
            if (j >= volList.size()) {
                break
            }
            float bpVal = genlogisticCdfDfvol(volList, j, bpDevices[i].p1, bpDevices[i].p2, bpDevices[i].p3)
            j++
            breakdownProb.add(
                    [
                            id         : tf.id,
                            createdTime: tf.createdTime,
                            deviceId   : tf.deviceId,
                            flowStatus : tf.flowStatus,
                            occupancy  : tf.occupancy,
                            speed      : tf.speed,
                            volume     : tf.volume,
                            direction  : tf.direction,
                            bp         : bpVal,
                    ]
            )
        }
        return breakdownProb
    }

    def getBPDevices() {
        return bpDevices
    }

    def genlogisticCdfDfvol(volDf, volColIlocnum, p1, p2, p3) {
        int x = volDf[volColIlocnum]
        float y = (x - p2) / p3
        float cdf = 1.0f / Math.pow((1.0f + Math.pow(2.718281, 0 - y)), p1)
        return cdf
    }

    // Utility Methods
    def formatXX(int val) {
        if (val == 0) {
            return '00'
        } else if (val < 10) {
            return '0' + val
        } else {
            return String.valueOf(val)
        }
    }

}
