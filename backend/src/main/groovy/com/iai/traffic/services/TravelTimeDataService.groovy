package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.anomaly.BluetoothAnomalyDevice
import com.iai.traffic.domain.anomaly.BluetoothAnomalyResult
import com.iai.traffic.domain.traffic.TrafficFlow
import com.iai.traffic.domain.traffic.TravelSegmentNew
import com.iai.traffic.domain.traffic.TravelTimeNew
import com.iai.traffic.repository.anomaly.BluetoothAnomalyDeviceRepository
import com.iai.traffic.repository.anomaly.BluetoothAnomalyResultRepository
import com.iai.traffic.repository.traffic.TravelSegmentNewRepository
import com.iai.traffic.repository.traffic.TravelTimeBaselineWoIncidentRepository
import com.iai.traffic.repository.traffic.TravelTimeNewRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Interpolation
import groovy.util.logging.Slf4j
import io.micronaut.data.annotation.DateCreated

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton
import java.time.Instant

@Singleton
@Slf4j
class TravelTimeDataService {

    @Inject BluetoothAnomalyDeviceRepository mBluetoothAnomalyDeviceRepository
    @Inject BluetoothAnomalyResultRepository mBluetoothAnomalyResultRepository
    @Inject TravelSegmentNewRepository mTravelSegmentNewRepository
    @Inject TravelTimeNewRepository mTravelTimeNewRepository
    @Inject TravelTimeBaselineWoIncidentRepository mTravelTimeBaselineWoIncidentRepository
    @Inject TravelSegmentService mTravelSegmentService

    def getBluetoothSegments() {
        Date endTime = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, 15 * 60)
        List<TravelTimeNew> ttList = mTravelTimeNewRepository.findByDateCreatedBetweenOrderByCalculationTimestampDesc(startTime, endTime)

        def segmentLevels = [:]
        ttList.each {
            def level = getLevel(it)
            def value = it.calculationTimestamp
            def text  = it.calculationTimestamp.toString()
            segmentLevels <<[(it.linkId): [level: level, value: value, text: text, instance: it]]
        }

        return mTravelSegmentService.getInfos().collect { linkId, info->
            def path = mTravelSegmentService.getPathByLinkId(linkId)
            def segment = segmentLevels.get(linkId)
            def travelTime = [
                    calculationTimestamp: [
                            value: (segment ? segment.value : 'N/A'),
                            text: (segment ? segment.text : 'N/A')
                    ],
                    level: (segment? segment.level : -3),
                    data: (segment? segment.instance : null)
            ]

            return [
                    id: linkId,
                    uid: info.shortName,
                    short: info.shortName,
                    name: info.shortName + ': ' + info.description,
                    desc: info.description,
                    route: info.route,
                    seq:   info.seq,
                    startPoint:  info.startPoint,
                    endPoint  :  info.endPoint,
                    distance  :  info.distance,
                    position: info.midPoint,
                    path: path,
                    cameras: info.cameras,
                    travelTime: travelTime
            ]
        }
    }

    def getSelectedBluetoothSegments(int aboveLevel) {
        Date endTime = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, 15 * 60)
        List<TravelTimeNew> ttList = mTravelTimeNewRepository.findByDateCreatedBetweenOrderByCalculationTimestampDesc(startTime, endTime)

        def segments = [:]
        ttList.each {
            def level = getLevel(it)
            if (level >= aboveLevel) {
                def linkId = it.linkId

                // Get baseline
                def timeIndex = getMinIdx(it.calculationTimestamp)
                def baseline = mTravelTimeBaselineWoIncidentRepository.find(linkId, timeIndex)
                def base = [:]
                if (!baseline.isEmpty()) {
                    def item = baseline.last()
                    base << [avgLinkSpeed: item.avgLinkSpeed]
                    base << [meanTravelTime: item.meanTravelTime]
                }

                def travelTime = [
                        calculationTimestamp: [
                                value: it.calculationTimestamp,
                                text : (it.calculationTimestamp ? it.calculationTimestamp.toString() : 'N/A')
                        ],
                        level   : level,
                        baseline: base,
                        data    : it
                ]
                segments << [(linkId): travelTime]
            }
        }

        def result = []
        def segmentInfos = mTravelSegmentService.getInfos()
        segments.each {linkId, travelTime->
            def info = segmentInfos.get(linkId)
            if (info != null) {
                def path = mTravelSegmentService.getPathByLinkId(linkId)
                result << [
                        id: linkId,
                        uid: info.shortName,
                        short: info.shortName,
                        name: info.shortName + ': ' + info.description,
                        desc: info.description,
                        route: info.route,
                        seq:   info.seq,
                        startPoint:  info.startPoint,
                        endPoint  :  info.endPoint,
                        distance  :  info.distance,
                        position: info.midPoint,
                        path: path,
                        cameras: info.cameras,
                        travelTime: travelTime
                ]
            }
        }

        return result
    }

    def getBluetoothAnomalyDevices() {
        ObjectMapper objectMapper = new ObjectMapper()
        List<BluetoothAnomalyDevice> devices = mBluetoothAnomalyDeviceRepository.findAll()
        if (devices.size() == 0) {
            return []
        }

        def travelTimes = getLatestTravelTimeData(120)

        return devices.collect {
            // Transform coordinates to polyline path
            def coordinates = objectMapper.readValue(it.coordinates, new TypeReference<List<List<Double>>>(){ })
            def path = coordinates.collect { coord->
                return [lat: coord[1], lng: coord[0]]
            }
            def cameras = []
            if (it.camera?.trim())  {
                try {
                    cameras = objectMapper.readValue(it.camera, new TypeReference<List<String>>(){ })
                } catch (Exception e) {
                    log.error(e.getMessage())
                }
            }
            def position = path.get((int)Math.round(path.size() / 2))
            def item = [
                    id: it.linkId,
                    name:  it.description,
                    distance: it.distance,
                    startPoint: it.startPoint,
                    endPoint: it.endPoint,
                    path: path,
                    position: position,
                    cameras: cameras
            ]

            def travelTime = travelTimes.get(it.linkId)
            if (travelTime != null) {
                item << [travelTime: travelTime.value]
            }
            return item
        }
    }

    def getLatestTravelTimeData(int secondsAgo) {
        // Get all link ids
        List<BluetoothAnomalyDevice> devices = mBluetoothAnomalyDeviceRepository.findAll()
        def linkIds = devices.collect { return it.linkId }

        // Get all data within time range
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<TravelTimeNew> data = mTravelTimeNewRepository.findByLinkIdInListAndDateCreatedBetween(linkIds, startTime, endTime)

        // Get latest
        def result = [:]
        data.each {
            def key = it.linkId
            def item = result.get(key)
            if ( item == null || it.calculationTimestamp.after(item.time)) {
                result << [(key): [time: it.calculationTimestamp, value: it.meanTravelTimeSecs]]
            }
        }

        return result
    }

    def getLatestBluetoothAnomalyData(int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<BluetoothAnomalyResult> data = mBluetoothAnomalyResultRepository.findByDateCreatedBetweenOrderByDateCreatedDesc(startTime, endTime)
        if (data.isEmpty()) {
            return []
        }
        return data.findAll {it.status > 0 }
    }

    def getTravelTimeData(String linkId, int interval, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getTravelTimeData(linkId, interval, startTime, endTime)
    }

    def getTravelTimeData(String linkId, int interval, Date startTime, Date endTime ) {
        def data = mTravelTimeNewRepository.findByLinkIdAndDateCreatedBetween(linkId, startTime, endTime)
        def baseline = mTravelTimeBaselineWoIncidentRepository.findByLinkId(linkId)

        if (data.isEmpty()) {
            return []
        }

        // Sort by time
        data.sort { it.calculationTimestamp }

        // Normalize timestamp to be 1-minute
        data.each {
            it.calculationTimestamp = AppUtils.roundDown1Min(it.calculationTimestamp)
        }

        // Remove duplicates based on timestamp
        data = data.unique { it.calculationTimestamp }

        // Accumulate data based on interval
        int defaultInterval = 60

        def travelTimeList = []
        def speedList = []
        data.each { item->
            if (item.meanTravelTimeSecs > 0) {
                travelTimeList << [item.calculationTimestamp.getTime(), item.meanTravelTimeSecs]
            }
            if (item.speedMph > 0) {
                speedList << [item.calculationTimestamp.getTime(), Math.round(item.speedMph)]
            }
        }

        def baselineTravelTimeList = []
        def baselineSpeedList = []
        baseline.each { item->
            Date d = composeDateTime(startTime, item.timeOfDay)
            baselineTravelTimeList << [d.getTime(), item.meanTravelTime]
            baselineSpeedList << [d.getTime(), item.avgLinkSpeed]
        }

        if (interval != defaultInterval) {
            def interp = new Interpolation()
            travelTimeList = interp.averageArrayList(travelTimeList, interval, 1, startTime.getTime(), endTime.getTime())
            speedList = interp.averageArrayList(speedList, interval, 1, startTime.getTime(), endTime.getTime())
            baselineTravelTimeList = interp.averageArrayList(baselineTravelTimeList, interval, 1, startTime.getTime(), endTime.getTime())
            baselineSpeedList = interp.averageArrayList(baselineSpeedList, interval, 1, startTime.getTime(), endTime.getTime())
        }

        return [
                travelTime: travelTimeList,
                speed: speedList,
                baselineTravelTime: baselineTravelTimeList,
                baselineSpeed: baselineSpeedList
        ]
    }

    def getLatestTravelTimeInfo(String linkId, int secondsAgo) {
        def result = [:]

        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        int timeIndex = getMinIdx(endTime)
        def data = mTravelTimeNewRepository.findByLinkIdAndDateCreatedBetween(linkId, startTime, endTime)
        if (!data.isEmpty()) {
            // Get the last one row
            def item = data.last()
            def current = [:]
            current << [time: item.calculationTimestamp]
            current << [min: item.minTravelTimeSecs]
            current << [max: item.maxTravelTimeSecs]
            current << [mean: item.meanTravelTimeSecs]
            current << [waitTime: item.actualWaitSecs]
            current << [pctl25: item.pctl25thSecs]
            current << [pctl75: item.pctl75thSecs]
            current << [speed: Math.round(item.speedMph)]
            result << [current: current]

            timeIndex = getMinIdx(item.calculationTimestamp)
        }

        def baseline = mTravelTimeBaselineWoIncidentRepository.find(linkId, timeIndex)
        if (!baseline.isEmpty()) {
            def item = baseline.last()
            def base = [:]
            base << [avgLinkSpeed: item.avgLinkSpeed]
            base << [stdLinkSpeed: item.stdLinkSpeed]
            base << [baseTravelTime: item.baseTravelTime]
            base << [meanTravelTime: item.meanTravelTime]
            base << [stdTravelTime: item.stdTravelTime]
            base << [maxTravelTime: item.maxTravelTime]
            base << [minTravelTime : item.minTravelTime]
            base << [pc25TravelTime: item.pc25TravelTime]
            base << [pc75TravelTime: item.pc75TravelTime]
            result << [baseline: base]
        }

        def anomaly = getLatestBluetoothAnomaly(linkId, secondsAgo)
        if (!anomaly.isEmpty()) {
            def item = [:]
            item << [status: anomaly.status]
            item << [updated: anomaly.time]
            result << [anomaly: item]
        }

        return result
    }

    def getLatestBluetoothAnomaly(String linkId, int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        List<BluetoothAnomalyResult> data = mBluetoothAnomalyResultRepository.findByLinkIdAndDateCreatedBetween(linkId, startTime, endTime)
        if (data.isEmpty()) {
            return [:]
        }
        def result = data.last()
        return [
            time: result.dateCreated,
            status: result.status
        ]
    }

    def getSegmentLinkIds(String route, String direction) {
        List<TravelSegmentNew> segments = direction?.trim() ?
                mTravelSegmentNewRepository.findByRouteAndDirection(route, direction) :
                mTravelSegmentNewRepository.findByRoute(route)

        // Filter out the routeOrder >=500
        def filteredSegments = segments.findAll { it.routeOrder < 500 }

        // Order it by route order
        filteredSegments.sort { it.routeOrder }

        def linkIds = filteredSegments.collect { it.linkId }

        return linkIds
    }

    def getSegments(String route, String direction) {
        def linkIds = getSegmentLinkIds(route, direction)
        return getSegments(linkIds)
    }

    def getSegments(List<String> linkIds) {
        def result = []
        ObjectMapper objectMapper = new ObjectMapper()
        linkIds.each { linkId->
            def segment = mTravelSegmentService.getSegmentByLinkId(linkId)
            if (segment != null) {
                // Transform coordinates to polyline path
                def coordinates = objectMapper.readValue(segment.coordinates, new TypeReference<List<List<Double>>>() { })
                def path = coordinates.collect { coord ->
                    return [lat: coord[1], lng: coord[0]]
                }

                def item = [:]
                item << [id: segment.linkId]
                item << [name: segment.shortName]
                item << [dir: segment.direction]
                item << [desc: segment.description]
                item << [path: path]
                item << [devices: mTravelSegmentService.getDevicesByLinkId(linkId)]

                result << item
            }
        }

        return result
    }

    def getTravelTimeHeatMapData(String route, String direction, Date dayTime, Integer interval) {
        def linkIds = getSegmentLinkIds(route, direction)
        return getTravelTimeHeatMapData(linkIds, dayTime, interval)
    }

    def getTravelTimeHeatMapData(List<String> linkIds, Date dayTime, Integer interval) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        def intervalInSecs = interval ?: 300

        long t0 = System.currentTimeMillis()

        // Get data
        List<TravelTimeNew> data = mTravelTimeNewRepository.findByLinkIdInListAndDateCreatedBetween(linkIds, startTime, endTime)
        if (data.size() == 0) {
            return [:]
        }

        long t1 = System.currentTimeMillis()
        def ycategories = []
        def descriptions = []
        def linkIdId2Idx = [:]
        linkIds.eachWithIndex { linkId, i ->
            def segment = mTravelSegmentService.getSegmentByLinkId(linkId)
            def desc = segment != null ? "${segment.description} (${segment.distance} miles)" : ''
            linkIdId2Idx << [(linkId): i]
            ycategories << (segment != null ? segment.shortName : linkId)
            descriptions << desc.toString()
        }

        def rows = linkIds.size()
        def columns = 24 * 3600 / intervalInSecs

        def xcategories = []
        for (int i = 0; i < columns; i++) {
            xcategories << getTimeByIdx(intervalInSecs, startTime, i).getTime()
        }

        // Arrange data
        def matrixData = [:]
        data.each {
            def segment = mTravelSegmentService.getSegmentByLinkId(it.linkId)
            if (segment != null) {
                if (it.meanTravelTimeSecs != null && segment.freeFlowSpeed != null && segment.freeFlowTime != null) {
                    def value = it.meanTravelTimeSecs
                    def i = linkIdId2Idx.get(it.linkId)
                    def j = getTimeIdx(startTime, it.calculationTimestamp, intervalInSecs)
                    def key = i + '-' + j
                    def item = matrixData.get(key)
                    if (item != null) {
                        item.values << value
                    } else {
                        item = [baseSpeed: segment.freeFlowSpeed, freeFlowTime: segment.freeFlowTime, values: [value]]
                        matrixData << [(key): item]
                    }
                }
            }
        }
        long t2 = System.currentTimeMillis()
        // Format to HighCharts heatmap data format
        def result = []
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                def key = y + '-' + x
                def item = matrixData.get(key)
                if (item != null) {
                    def travelTime = item.values.sum() / item.values.size()
                    def status = computeTravelTimeStatusLevel(item.baseSpeed, item.freeFlowTime, travelTime)
                    result << [x, y, status]
                } else {
                    result << [x, y, -1]
                }
            }
        }
        long t3 = System.currentTimeMillis()

        log.info('t0=' + (t1 - t0) + ',t1=' + (t2 - t1) + ',t2=' + (t3 - t2))

        return [
                xcategories: xcategories,
                ycategories: ycategories,
                descriptions: descriptions,
                series: result
        ]
    }

    def computeTravelTimeStatusLevel(Float baseSpeed, Integer freeFlowTime, Double travelTime) {
        def reliability = travelTime / (float)freeFlowTime

        int status = 0
        if (baseSpeed > 45) {
            if (reliability > 0.0 && reliability < 1.18) {
                status = 1 //Dark Green
            } else if (reliability >= 1.18 && reliability < 1.44) {
                status = 2 //Light Green
            } else if (reliability >= 1.44 && reliability < 1.86) {
                status = 3 //Yellow
            } else if (reliability >= 1.86 && reliability < 2.60) {
                status = 4 //Light Orange
            } else if (reliability >= 2.60 && reliability < 3.25) {
                status = 5 //Orange
            } else if (reliability >= 3.25 && reliability < 4.33) {
                status = 6 //Light Red
            } else if (reliability >= 4.33 && reliability < 13.00) {
                status = 7 //Red
            } else if (reliability >= 13.00) {
                status = 8 //Dark Red
            }
        } else if (baseSpeed > 30) {
            if (reliability > 0.0 && reliability < 1.29) {
                status = 1 //Dark Green
            } else if (reliability >= 1.29 && reliability < 1.80) {
                status = 2 //Light Green
            } else if (reliability >= 1.80 && reliability < 2.25) {
                status = 3 //Yellow
            } else if (reliability >= 2.25 && reliability < 3.00) {
                status = 4 //Light Orange
            } else if (reliability >= 3.00 && reliability < 4.50) {
                status = 5 //Orange
            } else if (reliability >= 4.50 && reliability < 9.00) {
                status = 6 //Light Red
            } else if (reliability >= 9.00) {
                status = 8 //Dark Red
            }
        } else {
            if (reliability > 0.0 && reliability < 1.20) {
                status = 1 //Dark Green
            } else if (reliability >= 1.20 && reliability < 1.50) {
                status = 3 //Yellow
            } else if (reliability >= 1.50 && reliability < 2.00) {
                status = 4 //Light Orange
            } else if (reliability >= 2.00 && reliability < 3.00) {
                status = 5 //Orange
            } else if (reliability >= 3.00 && reliability < 6.00) {
                status = 6 //Light Red
            } else if (reliability >= 6.00) {
                status = 8 //Dark Red
            }
        }
        return status
    }

    Date composeDateTime(Date startTime, String timeOfDay) {
        def items = timeOfDay.split(':')
        def hour = Integer.parseInt(items[0])
        def minute = Integer.parseInt(items[1])
        def seconds = (hour * 60 + minute) * 60
        return AppUtils.addSeconds(startTime, seconds)
    }

    int getMinIdx(Date time) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime()) / 60000.0f)
    }

    int getHourIdx(Date time) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime()) / 3600000.0f)
    }

    int get5MinIdx(Date time) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime()) / 300000.0f)
    }

    int getTimeIdx(Date time, int intervalInSecs) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime()) / (intervalInSecs * 1000.0f))
    }

    int getTimeIdx(Date startTime, Date time, int intervalInSecs) {
        return (int)Math.floor((time.getTime() - startTime.getTime()) / (intervalInSecs * 1000.0f))
    }

    int get5MinIdx(Date startTime, Date time) {
        return (int)Math.floor((time.getTime() - startTime.getTime()) / 300000.0f)
    }

    Date getTimeByHourIdx(Date startTime, int hourIdx) {
        def t = (long)Math.floor(startTime.getTime()  + hourIdx * 3600 * 1000.0f)
        return new Date(t)
    }

    Date getTimeBy5MinIdx(Date startTime, int minIdx) {
        def t = (long)Math.floor(startTime.getTime()  + minIdx * 300000.0f)
        return new Date(t)
    }

    Date getTimeByIdx(int intervalInSecs, Date startTime, int idx) {
        def t = (long)Math.floor(startTime.getTime()  + idx * intervalInSecs * 1000.0f)
        return new Date(t)
    }

    def getLevel(tt) {
        def tti = tt.meanTravelTimeSecs / tt.freeFlowRunTimeSecs
        int segmentLevel = -3
        if (tt.score < 50) {
            segmentLevel = 0
        }
        else {
            if (tti <= 1.25) {
                segmentLevel = 1
            }
            else if (tti <= 1.5)
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

}
