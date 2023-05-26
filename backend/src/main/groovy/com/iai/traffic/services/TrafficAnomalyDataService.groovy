package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.anomaly.AnomalyDevice
import com.iai.traffic.domain.anomaly.AnomalyResult
import com.iai.traffic.domain.traffic.TrafficDevice
import com.iai.traffic.domain.traffic.TrafficDeviceAttrs
import com.iai.traffic.domain.traffic.TrafficFlow
import com.iai.traffic.repository.*
import com.iai.traffic.repository.anomaly.AnomalyDeviceRepository
import com.iai.traffic.repository.anomaly.AnomalyResultRepository
import com.iai.traffic.repository.traffic.TrafficBreakdownParamRepository
import com.iai.traffic.repository.traffic.TrafficDeviceAttrsRepository
import com.iai.traffic.repository.traffic.TrafficDeviceRepository
import com.iai.traffic.repository.traffic.TrafficFlowRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficAnomalyDataService {
    @Inject AnomalyResultRepository mAnomalyResultRepository
    @Inject AnomalyDeviceRepository mAnomalyDeviceRepository
    @Inject TrafficDeviceRepository mTrafficDeviceRepository
    @Inject TrafficFlowRepository mTrafficFlowRepository
    @Inject AnomalyThresholdService mAnomalyThresholdService
    @Inject TrafficFlowAlgorithmService mTrafficFlowAlgorithmService
    @Inject TrafficBreakdownParamRepository mTrafficBreakdownParamRepository
    @Inject TrafficDeviceAttrsRepository mTrafficDeviceAttrsRepository
    @Inject RedisCacheService mRedisCacheService

    def getTrafficDevices(int flag) {
        def result = []
        mTrafficDeviceRepository.findAll().each {
            if (it.flags == flag) {
                def item = [
                        id        : it.deviceId,
                        uid       : it.uid,
                        title     : it.title,
                        name      : it.deviceId + " - " + it.uid + " - " + it.title,
                        position  : [lat: it.lat, lng: it.lon],
                        flags     : it.flags,
                        zone      : it.flags,
                        directions: mTrafficFlowAlgorithmService.getDirections(it.directions)
                ]
                result << item
            }
        }

        result.sort { it.id }
        return result
    }

    def getAllAnomalyDevices() {
        return mAnomalyDeviceRepository.findAll()
    }

    def getAnomalyDevices() {
        List<AnomalyDevice> devices = getAllAnomalyDevices()
        def uniqueAnomalyDevices = [:]
        devices.each { device->
            uniqueAnomalyDevices << [(device.deviceId): device]
        }
        // Add CAV system detectors
        def cavSystemDetectors = getTrafficDevices(4)
        cavSystemDetectors.each {
            uniqueAnomalyDevices <<[(it.id.intValue()): it]
        }

        // Add beach area detectors
        def beachAreaDetectors = getTrafficDevices(7)
        beachAreaDetectors.each {
            uniqueAnomalyDevices <<[(it.id.intValue()): it]
        }

        def deviceIds = []
        uniqueAnomalyDevices.each { deviceId, device->
            deviceIds << (Long) deviceId
        }

        def trafficDevices = [:]
        mTrafficDeviceRepository.findByDeviceIdInList(deviceIds).each {device->
            trafficDevices << [(device.deviceId + ''): device]
        }

        def result = []
        uniqueAnomalyDevices.each { deviceId, device->
            def thisDevice = trafficDevices.get(deviceId + '')
            def item = [
                    id:     deviceId,
                    seq:    device.seq,
                    uid:    thisDevice.uid,
                    route:  device.route,
                    dir:    device.direction,
                    zone:   device.zone,
                    region: thisDevice.region,
                    type:   thisDevice.deviceType,
                    name:  deviceId + " - " + thisDevice.uid + " - " + thisDevice?.title,
                    position: [lat: thisDevice?.lat, lng: thisDevice?.lon],
                    directions: ['NB', 'SB'],
                    params: mTrafficFlowAlgorithmService.getTrafficBreakdownParam(deviceId, device.direction)
            ]
            result << item
        }
        result.sort { it.id }

        return result
    }

    def getAnomalyDevices(String route, String direction) {
        def trafficDevices = [:]
        mTrafficDeviceRepository.findAll().each { device->
            trafficDevices << [(device.deviceId.intValue()): device]
        }

        List<AnomalyDevice> devices = mAnomalyDeviceRepository.findByRouteAndDirection(route, direction)
        def result = devices.findAll {it.enabled }.collect {
            def device = trafficDevices.get(it.deviceId)
            return [
                    id:    it.deviceId,
                    permit: device?.uid,
                    seq:   it.seq,
                    route: it.route,
                    dir:   it.direction,
                    name:  it.deviceId + " - " + device?.title,
                    position: [lat: device?.lat, lng: device?.lon]
            ]
        }
        result.sort { it.seq }

        return result
    }

    def getAnomalyResultsHour(String route, String direction, Date dayTime) {
        def devices = getAnomalyDevices(route, direction).collect { it.id }
        return getAnomalyResultsHour(devices, direction, dayTime)
    }

    def getAnomalyResultsHour(List<Integer> devices, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getAnomalyResultsHour(devices, direction, startTime, endTime)
    }

    def getAnomalyResults5Min(String route, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        def devices = getAnomalyDevices(route, direction)
        return getAnomalyResults5Min(devices, direction, startTime, endTime)
    }

    def getAnomalyData(List<Integer> devices, String direction, Date startTime, Date endTime) {
        return mAnomalyResultRepository.findByDeviceIdInListAndDirectionAndTimeBetween(devices, direction, startTime, endTime)
    }

    def getAnomalyTimeData(Integer deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<AnomalyResult> data =  mAnomalyResultRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)
        return data.findAll {it.status > 0 }.collect {it.time }
    }

    def getAnomalyTimeDataList(List<String> devices, Date dayTime) {
        def result = [:]
        devices.each {device->
            def dd = device.split('-')
            def deviceId  = Integer.parseInt(dd[0].trim())
            def direction = dd[1]
            def data = getAnomalyTimeData(deviceId, direction, dayTime)
            if (!data.isEmpty()) {
                result << [(device): data]
            }
        }
        return result
    }

    def getAnomalyVolumeSpeedData(Integer deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<AnomalyResult> data =  mAnomalyResultRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)
        return data.findAll {it.status > 0 }.collect {[x: it.volume, y: it.speed, time: it.time, status: it.status] }
    }

    def getLatestAnomalyData(int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<AnomalyResult> data = mAnomalyResultRepository.findByDateCreatedBetweenOrderByDateCreatedDesc(startTime, endTime)
        if (data.isEmpty()) {
            return []
        }

        return data.findAll {it.status > 0 }
    }

    def getLatestAnomalyData(Integer deviceId, String direction, int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<AnomalyResult> data = mAnomalyResultRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)
        if (data.isEmpty()) {
            return null
        }

        // Get the last one row
        def result = data.last()
        return [
                time : result.time,
                mdist: result.mdist,
                severity: result.severity,
                status: result.status
        ]
    }

    def getAnomalyResultsHour(List<Integer> devices, String direction, Date startTime, Date endTime) {
        // Get data
        def data = getAnomalyData(devices, direction, startTime, endTime)
        if (data.size() == 0) {
            return [:]
        }

        def ycategories = []
        def deviceIdToIdx = [:]
        devices.eachWithIndex{ deviceId, i ->
            deviceIdToIdx << [(deviceId): i]
            ycategories << deviceId
        }

        def deviceCount = devices.size()
        def colCount = 24

        def xcategories = []
        for (int i = 0; i < colCount; i++) {
            xcategories << getTimeByHourIdx(startTime, i).getTime()
        }

        // Arrange data
        int[][] matrix = new int[deviceCount][colCount]
        for (int i = 0; i < deviceCount; i++) {
            for (int j = 0; j < colCount; j++) {
                matrix[i][j] = -1
            }
        }

        data.each {
            def i = deviceIdToIdx.get(it.deviceId)
            def j = getHourIdx(it.time)
            def v = it.status > 0 ? 1 : 0
            if (matrix[i][j] < 0) {
                matrix[i][j] = v
            } else {
                matrix[i][j] += v
            }
        }

        // Format to HighCharts heatmap data format
        def result = []
        for (int x = 0; x < colCount; x++) {
            for (int y = 0; y < deviceCount; y++) {
                result << [x, y, matrix[y][x]]
            }
        }

        return [
                xcategories: xcategories,
                ycategories: ycategories,
                series: result
        ]
    }

    def getAnomalyResults5Min(def devices, String direction, Date startTime, Date endTime) {
        def deviceIds = devices.collect { it.id }
        def permitNumbers = devices.collect { it.permit }

        // Get data
        def data = getAnomalyData(deviceIds, direction, startTime, endTime)
        if (data.size() == 0) {
            return [:]
        }

        def ycategories = []
        def deviceIdToIdx = [:]
        deviceIds.eachWithIndex{ deviceId, i ->
            deviceIdToIdx << [(deviceId): i]
            ycategories << deviceId
        }

        def xcategories = []
        for (int i = 0; i < 288; i++) {
            xcategories << getTimeBy5MinIdx(startTime, i).getTime()
        }

        // Arrange data
        def deviceCount = deviceIds.size()
        int[][] matrix = new int[deviceCount][288]
        for (int i = 0; i < deviceCount; i++) {
            for (int j = 0; j < 288; j++) {
                matrix[i][j] = -1
            }
        }

        data.each {
            def i = deviceIdToIdx.get(it.deviceId)
            def j = get5MinIdx(it.time)
            matrix[i][j] = it.status
        }

        // Format to HighCharts heatmap data format
        def result = []
        for (int x = 0; x < 288; x++) {
            for (int y = 0; y < deviceCount; y++) {
                result << [x, y, matrix[y][x]]
            }
        }

        return [
                permitNumbers: permitNumbers,
                xcategories: xcategories,
                ycategories: ycategories,
                series: result
        ]
    }

    def getAnomalyResults(Integer deviceId, String direction, int hourIdx, Date dayTime) {
        Date startTime = AppUtils.getStartOfHour(dayTime, hourIdx)
        Date endTime   = AppUtils.getEndOfHour(dayTime, hourIdx)

        List<AnomalyResult> data = mAnomalyResultRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)

        def avgCategories = ['AvgSpd', 'AvgVol', 'AvgOcc']
        def anomalyCategories = ['speed', 'volume', 'occupancy', 'severity', 'mdist', 'status']
        int rows = avgCategories.size() + anomalyCategories.size()
        int cols = 12

        def xcategories = []
        for (int i = 0; i < cols; i++) {
            xcategories << getTimeBy5MinIdx(startTime, i)
        }

        // Arrange data
        float[][] matrix = new float[rows][cols]
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = -100
            }
        }

        data.each { d->
            def j = get5MinIdx(startTime, d.time)

            def threshold = mAnomalyThresholdService.getAnomalyThreshold(deviceId, direction, d.time)
            if (threshold != null) {
                matrix[0][j] = Math.round(threshold.spd)
                matrix[1][j] = Math.round(threshold.vol)
                matrix[2][j] = Math.round(threshold.occ)
            }

            for (int i = 0; i < anomalyCategories.size(); i++) {
                def type = anomalyCategories[i]
                def value = d[type]
                if (type.equalsIgnoreCase("mdist")) {
                    value = round2(value)
                } else if (type.equalsIgnoreCase("status")) {
                    if (d.code != null) {
                        value = d.code
                    }
                }
                matrix[avgCategories.size() + i][j] = value
            }
        }


//        // Format to HighCharts heatmap data format
//        def series = []
//        for (int x = 0; x < cols; x++) {
//            for (int y = 0; y < rows; y++) {
//                series << [x, y, matrix[y][x]]
//            }
//        }

        def ycategories = avgCategories + anomalyCategories
        ycategories = ycategories.collect { AppUtils.capitalize(it) }

        return [
                xcategories: xcategories,
                ycategories: ycategories,
                matrix: matrix
        ]
    }

    def getLatestTrafficFlowData(Integer deviceId, String direction, int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, direction, startTime, endTime)

        if (data.size() == 0) {
            return null
        }

        // Get the last one row
        def flow = data.last()
        return [
                time : flow.createdTime,
                speed:flow.speed,
                volume: flow.volume,
                occupancy: flow.occupancy
        ]
    }

    def getNearbyTwoTrafficCams(Integer deviceId, String direction) {
        def result = null
        List<AnomalyDevice> devices = mAnomalyDeviceRepository.findByDeviceIdAndDirection(deviceId, direction)
        if (devices.size() > 0) {
            def device = devices.first()
            result = [upCam: device.upstream, downCam: device.downstream]
        }
        return result
    }

    def getLatestDeviceInfo(Integer deviceId) {
        Optional<TrafficDevice> deviceOpt = mTrafficDeviceRepository.findByDeviceId((long)deviceId)
        if (!deviceOpt.isPresent()) {
            return [:]
        }
        def device = deviceOpt.get()

        Date time = AppUtils.roundDown5Min(new Date())

        def lanes = mTrafficFlowAlgorithmService.getNumbers(device.lanes)
        def sampleSize = mTrafficFlowAlgorithmService.getNumbers(device.sampleSize)
        def maxVolumeFiveMin = mTrafficFlowAlgorithmService.getNumbers(device.maxVolumeFiveMin)
        def maxVolumeOneHour = mTrafficFlowAlgorithmService.getNumbers(device.maxVolumeOneHour)

        def sensorMap = [:]
        def directions = mTrafficFlowAlgorithmService.getDirections(device.directions)
        directions.eachWithIndex { direction, idx->
            def r = [:]

            def flow = getLatestTrafficFlowData(deviceId, direction, 900)
            if (flow != null) {
                r << [flowTime: flow.time]
                r << [speed: flow.speed]
                r << [volume: flow.volume]
                r << [occupancy: flow.occupancy]

                // Flow level and status
                def level = mTrafficFlowAlgorithmService.getTrafficStatusLevelByOccupancy(flow.occupancy)
                r << [flowLevel: level]
                r << [flowStatus: mTrafficFlowAlgorithmService.getTrafficStatusByOccupancy(flow.occupancy) ]
                r << [flowColor: mTrafficFlowAlgorithmService.getTrafficStatusColorByLevel(level)]

                time = flow.time
                r << [volume1hrProjected: flow.volume * 12]

                if (maxVolumeFiveMin != null && idx < maxVolumeFiveMin.size()) {
                    r << [vo: Math.round(flow.volume * 100/maxVolumeFiveMin[idx] + flow.occupancy)]
                }

                def params = mTrafficFlowAlgorithmService.getTrafficBreakdownParam(deviceId, direction)
                if (params.size() == 3) {
                    r << [breakdown: mTrafficFlowAlgorithmService.genlogisticCdfDfvol(flow.volume, params)]
                }
            }

            def threshold = mAnomalyThresholdService.getAnomalyThreshold(deviceId, direction, time)
            if (threshold != null) {
                r << [avgSpd: Math.round(threshold.spd)]
                r << [avgVol: Math.round(threshold.vol)]
                r << [avgOcc: Math.round(threshold.occ)]
            }

            def anomaly = getLatestAnomalyData(deviceId, direction, 900)
            if (anomaly != null) {
                r << [anomalyTime : anomaly.time]
                r << [mdist: round2(anomaly.mdist)]
                r << [severity: Math.round(anomaly.severity)]
                r << [anomalyStatus: anomaly.status]
            }

            if (lanes != null && idx < lanes.size()) {
                r << [lanes: lanes[idx]]
            }
            if (sampleSize != null && idx < sampleSize.size()) {
                r << [sampleSize: sampleSize[idx]]
            }
            if (maxVolumeFiveMin != null && idx < maxVolumeFiveMin.size()) {
                r << [volume5MinMax: maxVolumeFiveMin[idx]]
            }
            if (maxVolumeOneHour != null && idx < maxVolumeOneHour.size()) {
                r << [volume1hrMax: maxVolumeOneHour[idx]]
            }

            Optional<TrafficDeviceAttrs>  attrsOpt = mTrafficDeviceAttrsRepository.findByDeviceIdAndDirection(deviceId, direction)
            if (attrsOpt.isPresent()) {
                TrafficDeviceAttrs attrs = attrsOpt.get()
                if (attrs.capacityFiveMin) {
                    r << [capacityFiveMin: attrs.capacityFiveMin]
                    r << [capacityHour: attrs.capacityHour]
                    r << [capacityHourPerLane: attrs.capacityHourPerLane]
                    if (flow != null) {
                        r << [capacityPercent: Math.round(flow.volume * 100 / attrs.capacityFiveMin)]
                    }
                }
            }

            r << [deviceId: deviceId]
            r << [direction: direction]
            r << [uid: device.uid]
            r << [type: device.type]
            r << [title: device.title]
            r << [stations: device.stationCount]

            def camera = getNearbyTwoTrafficCams(deviceId, direction)
            if (camera != null) {
                if (camera.upCam != null) {
                    r << [upCamUrl: camera.upCam.toString()]
                }
                if (camera.downCam != null) {
                    r << [downCamUrl: camera.downCam.toString()]
                }
            }

            sensorMap << [(direction): r]
        }

        // Get the prediction results
        def predictions = getPredictionResult(device, time)
        if (!predictions.isEmpty()) {
            // Merge with the sensorMap info
            predictions.each { direction, prediction ->
                def info = sensorMap.get(direction)
                if (info != null) {
                    info << [prediction: prediction]
                }
            }
        }

        return sensorMap
    }

    def getPredictionResult(TrafficDevice device, Date time) {
        def key = ''
        if (device.flags == Constants.REGION_URBAN_FREEWAY) {
            key = 'freeway_' + AppUtils.formatDateToString(time)
        } else if (device.flags == Constants.REGION_CAV_SYSTEM) {
            key = 'arterial_' + AppUtils.formatDateToString(time)
        }

        def result = [:]
        if (!key.isEmpty()) {
            def data = mRedisCacheService.getData(key)
            if (data != null) {
                ObjectMapper objectMapper = new ObjectMapper()
                def json = objectMapper.readValue(data, HashMap.class)

                // Extract data for the detector
                def directions = mTrafficFlowAlgorithmService.getDirections(device.directions)
                directions.each {direction->
                    def entryKey = device.deviceId + '_' + direction
                    def entry = json.get('result').get(entryKey)
                    if (entry != null) {
                        result << [(direction): entry]
                    }
                }
            }
        }

        return result
    }

    def round2(def value) {
        return Math.round(value * 100) / 100
    }

    int getHourIdx(Date time) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime())/3600000.0f)
    }

    int get5MinIdx(Date time) {
        def startTime = AppUtils.getStartOfDay(time)
        return (int)Math.floor((time.getTime() - startTime.getTime())/300000.0f)
    }

    int get5MinIdx(Date startTime, Date time) {
        return (int)Math.floor((time.getTime() - startTime.getTime())/300000.0f)
    }

    Date getTimeByHourIdx(Date startTime, int hourIdx) {
        def t = (long)Math.floor(startTime.getTime()  + hourIdx * 3600 * 1000.0f)
        return new Date(t)
    }

    Date getTimeBy5MinIdx(Date startTime, int minIdx) {
        def t = (long) Math.floor(startTime.getTime() + minIdx * 300000.0f)
        return new Date(t)
    }
}
