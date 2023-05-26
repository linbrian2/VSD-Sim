package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.anomaly.TrafficLcmParam
import com.iai.traffic.domain.traffic.*
import com.iai.traffic.repository.anomaly.TrafficLcmParamRepository
import com.iai.traffic.repository.traffic.*
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import com.iai.traffic.utils.Interpolation
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficFlowDataService {
    @Inject TrafficFlowRepository mTrafficFlowRepository
    @Inject TrafficDeviceRepository mTrafficDeviceRepository
    @Inject AnomalyThresholdService mAnomalyThresholdService
    @Inject TrafficFlowAlgorithmService mTrafficFlowAlgorithmService
    @Inject TrafficFlowPerLaneRepository mTrafficFlowPerLaneRepository
    @Inject TrafficFlowPerMinuteRepository mTrafficFlowPerMinuteRepository
    @Inject TrafficDevicePerLaneRepository mTrafficDevicePerLaneRepository
    @Inject TrafficDevicePerMinuteRepository mTrafficDevicePerMinuteRepository
    @Inject TrafficLcmParamRepository mTrafficLcmParamRepository
    @Inject TrafficFlowBaselineRepository mTrafficFlowBaselineRepository
    @Inject TrafficCameraRepository mTrafficCameraRepository
    @Inject TrafficZoneDataService mTrafficZoneDataService
    @Inject RedisCacheService mRedisCacheService

    def mTrafficDevicePermitNumbers = [:]
    def mTrafficDevicePerLanes = [:]
    def mTrafficDevicePerMinutes = [:]

    @PostConstruct
    def initialize() {
        log.info("TrafficFlowDataService initializing ...")
        createTrafficDevicePerLaneMap()
        createTrafficDevicePerMinuteMap()
    }

    def getTrafficDevices(boolean includePerLaneDevices) {
        def redisKey = 'TRAFFIC_DEVICES'
        def data = mRedisCacheService.getData(redisKey)
        if (data != null) {
            ObjectMapper objectMapper = new ObjectMapper()
            def result = objectMapper.readValue(data, List.class)
            return result
        }

        List<TrafficDevice> devices = mTrafficDeviceRepository.findAll()
        def result = devices.collect {
            return [
                    id:   it.deviceId,
                    uid:  it.uid,
                    title: it.title,
                    name: it.deviceId + " - " +  it.uid + " - " + it.title,
                    position: [lat: it.lat, lng: it.lon],
                    flags: it.flags,
                    region: it.region,
                    type: it.deviceType,
                    directions: mTrafficFlowAlgorithmService.getDirections(it.directions)
            ]
        }

        if (includePerLaneDevices) {
            def deviceUids = result.collect {it.uid } as Set
            def perLaneDevices = getTrafficPerLaneDevices()
            def uniquePerLaneDevices = perLaneDevices.findAll {!deviceUids.contains(it.uid) }
            result.addAll(uniquePerLaneDevices)
        }

        result.sort { it.id }

        // Save the result to Redis
        ObjectMapper objectMapper = new ObjectMapper()
        def redisValue = objectMapper.writeValueAsString(result)
        mRedisCacheService.putData(redisKey, redisValue, 24 * 3600L)

        return result
    }

    def getTrafficPerLaneDevices() {
        List<TrafficDevicePerLane> devices = mTrafficDevicePerLaneRepository.findAll()

        // Organize the data
        def deviceMap = [:]
        devices.each {
            def permitNumber = it.permitNumber
            def items = deviceMap.get(permitNumber)
            if (items != null) {
                items << it
            } else {
                deviceMap << [(permitNumber): [it]]
            }
        }

        // Feed the result
        def result = deviceMap.values().collect { items->
            def lanes = items.size()
            def item = items[0]
            return [
                    id:   item.deviceId,
                    uid:  item.permitNumber,
                    title: item.description,
                    name: item.deviceId + " - " +  item.permitNumber + " - " + item.description,
                    position: [lat: item.latitude, lng: item.longitude],
                    lanes: lanes,
                    flags: -1,
                    region: -1,
                    type: -1,
                    directions: getBoundsByDevicePerLane(item.permitNumber)
            ]
        }

        result.sort { it.id }
        return result
    }

    def getTrafficPerMinuteDevices() {
        List<TrafficDevicePerMinute> devices = mTrafficDevicePerMinuteRepository.findAll()

        // Organize the data
        def deviceMap = [:]
        devices.each {
            def deviceId = it.deviceId
            def items = deviceMap.get(deviceId)
            if (items != null) {
                items << it
            } else {
                deviceMap << [(deviceId): [it]]
            }
        }

        // Feed the result
        def result = deviceMap.values().collect { items->
            def lanes = items.size()
            def item = items[0]
            return [
                    id:   item.deviceId,
                    uid:  item.permitNumber,
                    title: item.description,
                    lanes: lanes,
                    name: item.deviceId + " - " +  item.description,
                    position: [lat: item.latitude, lng: item.longitude]
            ]
        }

        result.sort { it.id }

        return result
    }

    def createTrafficDevicePerLaneMap() {
        if (mTrafficDevicePerLanes.isEmpty()) {
            List<TrafficDevicePerLane> devices = mTrafficDevicePerLaneRepository.findAll()

            // Organize the data
            def deviceMap = [:]
            devices.each {
                def key = it.permitNumber
                def items = deviceMap.get(key)
                if (items != null) {
                    items << it
                } else {
                    deviceMap << [(key): [it]]
                }
            }

            deviceMap.each { key, items->
                def lanes = [:]
                items.each { item->
                    lanes << [(item.position): [name: item.name, bound: item.bound]]
                }
                mTrafficDevicePerLanes << [(key): lanes]
            }
        }
    }

    def getTrafficCameras() {
        def result = [:]
        mTrafficCameraRepository.findAll().each {
            if (it.enabled) {
                def key = it.cameraId
                def value = [
                        id      : it.cameraId,
                        name    : it.cameraId + " - " + it.title,
                        position: [lat: it.lat, lng: it.lon],
                ]
                result << [(key): value]
            }
        }
        return result.values()
    }

    def createTrafficDevicePerMinuteMap() {
        if (mTrafficDevicePerMinutes.isEmpty()) {
            List<TrafficDevicePerMinute> devices = mTrafficDevicePerMinuteRepository.findAll()

            // Organize the data
            def deviceMap = [:]
            devices.each {
                def id = it.deviceId
                def items = deviceMap.get(id)
                if (items != null) {
                    items << it
                } else {
                    deviceMap << [(id): [it]]
                }
            }

            deviceMap.each { id, items->
                def lanes = [:]
                items.each { item->
                    lanes << [(item.position): [name: item.zoneName, bound: item.bound]]
                }
                mTrafficDevicePerMinutes << [(id): lanes]
            }
        }
    }

    def getBoundsByDevicePerLane(String permitNumber) {
        def lanes = mTrafficDevicePerLanes.get(permitNumber)
        def directions = lanes.values().unique(false) { it.bound }.collect { it.bound }

        // Sort directions
        def result = []
        ['NB','SB','EB','WB'].each { dir->
            if (directions.contains(dir)) {
                result << dir
            }
        }
        return result
    }

    def getLaneByPositionForDevicePerLane(String permitNumber, def position) {
        def result = [name: position]
        def lanes = mTrafficDevicePerLanes.get(permitNumber)
        if (lanes != null) {
            result = lanes.get(position)
        }
        return result
    }

    def getLaneByPositionForDevicePerMinute(int deviceId, def position) {
        def result = [name: position]
        def lanes = mTrafficDevicePerMinutes.get(deviceId)
        if (lanes != null) {
            result = lanes.get(position)
        }
        return result
    }


    def getTrafficDeviceInfo(long deviceId) {
        Optional<TrafficDevice> deviceOpt = mTrafficDeviceRepository.findByDeviceId(deviceId)
        if (!deviceOpt.isPresent()) {
            return [:]
        }
        def device = deviceOpt.get()
        return [
                id:   device.deviceId,
                name: device.deviceId + " - " + device.title,
                type: device.type,
                directions: device.directions,
                lanes: device.lanes,
                position: [lat: device.lat, lng: device.lon],
        ]
    }

    def getTrafficDirections(int deviceId) {
        Optional<TrafficDevice> deviceOpt = mTrafficDeviceRepository.findByDeviceId((long)deviceId)
        if (!deviceOpt.isPresent()) {
            return []
        }
        def device = deviceOpt.get()

        def result = []
        if (device != null) {
            // Convert to JSON object
            ObjectMapper objectMapper = new ObjectMapper()
            def directions = objectMapper.readValue(device.directions, new TypeReference<List<String>>(){}) as Set

            // Sort directions
            def formattedDirections = ['NB','SB','EB','WB']
            formattedDirections.each { direction->
                if (directions.contains(direction)) {
                    result << direction
                }
            }
        }
        return result
    }

    def getPermitNumberByDeviceId(int deviceId) {
        if (mTrafficDevicePermitNumbers.isEmpty()) {
           mTrafficDeviceRepository.findAll().each {
               mTrafficDevicePermitNumbers << [(it.deviceId.intValue()): it.uid]
           }
        }
        return mTrafficDevicePermitNumbers.get(deviceId)
    }

    def getTrafficFlowData(int deviceId, String direction, int interval, Date startTime, Date endTime, boolean computeZoneVPlusO) {
        // Limit the date range within two weeks
        if (AppUtils.getDays(startTime, endTime) > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Organize the data
        def spdData = [:]
        def volData = [:]
        def occData = [:]
        def voData  = [:]
        def directions = direction == null ? getTrafficDirections(deviceId) : [ direction ]
        directions.each { dir->
            List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, dir, startTime, endTime)
            if (!data.isEmpty()) {
                def maxVolumeFiveMin = mTrafficFlowAlgorithmService.getMaxVolumeFiveMin(deviceId, dir)
                def spd = []
                def vol = []
                def occ = []
                def vo = []
                data.each {
                    def time = it.createdTime.getTime()
                    if (it.speed) {
                        spd << [time, it.speed]
                    }
                    if (it.volume) {
                        vol << [time, it.volume]
                    }
                    if (it.occupancy) {
                        occ << [time, it.occupancy]
                    }

                    if (it.volume && it.occupancy) {
                        def value = mTrafficFlowAlgorithmService.getVolPlusOccValue(maxVolumeFiveMin, it.volume, it.occupancy)
                        vo << [time, value]
                    }
                }
                if (!spd.isEmpty()) {
                    spdData << [(dir): spd]
                }
                if (!vol.isEmpty()) {
                    volData << [(dir): vol]
                }
                if (!occ.isEmpty()) {
                    occData << [(dir): occ]
                }
                if (!vo.isEmpty()) {
                    voData << [(dir): vo]
                }
            }
        }

        if (volData.isEmpty() && occData.isEmpty()) {
            return [:]
        }

        // Interpolation
        int defaultInterval = 300
        if (interval > defaultInterval) {
            def interp = new Interpolation()
            spdData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            volData.each {
                it.value = interp.averageArrayList(it.value, interval, 0, startTime.getTime(), endTime.getTime())
            }
            occData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            voData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
        }

        // Compose the output result
        def colors = ['#058DC7', '#ED561B', '#50B432', '#DDDF00', '#24CBE5', "#2B908F", "#F45B5B", "#91E8E1","#434348"]

        def speeds = []
        spdData.eachWithIndex{ entry, i ->
            speeds << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def volumes = []
        volData.eachWithIndex{ entry, i ->
            volumes << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def occupancies = []
        occData.eachWithIndex{ entry, i ->
            occupancies << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def voes = []
        voData.eachWithIndex{ entry, i ->
            voes << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        // Calculate zone v+o
        if (!voes.isEmpty() && computeZoneVPlusO) {
            def startIdx = voes.size()
            def permitNumber = getPermitNumberByDeviceId(deviceId)
            directions.eachWithIndex { dir, i ->
                def data = mTrafficZoneDataService.getZoneVPlusO(permitNumber, dir, startTime, endTime)
                if (!data.isEmpty()) {
                    voes << [name: dir + ' Zone', bound: dir, color: colors[(startIdx + i) % colors.size()], data: data]
                }
            }
        }

        return  [speed: speeds, volume: volumes, occupancy: occupancies, vo: voes]
    }

    def getTrafficFlowBaselineData(int deviceId, String direction, int interval, Date startTime, Date endTime) {
        // Limit the date range within two weeks
        if (AppUtils.getDays(startTime, endTime) > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Organize the data
        def spdData = [:]
        def volData = [:]
        def occData = [:]
        def voData = [:]
        def directions = direction == null ? getTrafficDirections(deviceId) : [ direction ]
        directions.each { dir->
            def data = mAnomalyThresholdService.getAnomalyThresholdList(deviceId, dir, startTime)
            if (!data.isEmpty()) {
                def maxVolumeFiveMin = mTrafficFlowAlgorithmService.getMaxVolumeFiveMin(deviceId, dir)
                def startOfDay = AppUtils.getStartOfDay(startTime)
                def spd = []
                def vol = []
                def occ = []
                def vo  = []
                data.each {
                    def time = startOfDay.getTime() + it.minute * 60000L
                    if (it.speed) {
                        spd << [time, Math.round(it.speed)]
                    }
                    if (it.volume) {
                        vol << [time, Math.round(it.volume)]
                    }
                    if (it.occupancy) {
                        occ << [time, Math.round(it.occupancy)]
                    }
                    if (it.volume && it.occupancy) {
                        def value = mTrafficFlowAlgorithmService.getVolPlusOccValue(maxVolumeFiveMin, it.volume, it.occupancy)
                        vo << [time, value]
                    }
                }
                if (!spd.isEmpty()) {
                    spdData << [(dir): spd]
                }
                if (!vol.isEmpty()) {
                    volData << [(dir): vol]
                }
                if (!occ.isEmpty()) {
                    occData << [(dir): occ]
                }
                if (!vo.isEmpty()) {
                    voData << [(dir): vo]
                }
            }
        }

        if (volData.isEmpty() && occData.isEmpty()) {
            return [:]
        }

        // Interpolation
        int defaultInterval = 300
        if (interval > defaultInterval) {
            def interp = new Interpolation()
            spdData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            volData.each {
                it.value = interp.averageArrayList(it.value, interval, 0, startTime.getTime(), endTime.getTime())
            }
            occData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            voData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
        }

        // Compose the output result
        def colors = ['#058DC7', '#ED561B', '#50B432', '#DDDF00', '#24CBE5']

        def speeds = []
        spdData.eachWithIndex{ entry, i ->
            speeds << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def volumes = []
        volData.eachWithIndex{ entry, i ->
            volumes << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def occupancies = []
        occData.eachWithIndex{ entry, i ->
            occupancies << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        def voes = []
        voData.eachWithIndex{ entry, i ->
            voes << [name: entry.key, bound: entry.key,  color: colors[i % colors.size()], data: entry.value]
        }

        return  [speed: speeds, volume: volumes, occupancy: occupancies, vo: voes]
    }

    def getTrafficFlowPerLaneData(String permitNumber, String direction, int interval, Date startTime, Date endTime, boolean computeZoneVPlusO) {
        // Limit the date range within two weeks
        if (AppUtils.getDays(startTime, endTime) > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Get traffic flow data
        List<TrafficFlowPerLane> data = mTrafficFlowPerLaneRepository.findByPermitNumberAndCreatedTimeBetween(permitNumber, startTime, endTime)
        if (data.isEmpty()) {
            return [:]
        }

        // Get positions
        def lanes = direction == null ? mTrafficDevicePerLaneRepository.findByPermitNumber(permitNumber) : mTrafficDevicePerLaneRepository.findByPermitNumberAndBound(permitNumber, direction)
        def positions = lanes.collect {it.position } as Set

        // Organize the data
        def spdData = [:]
        def volData = [:]
        def occData = [:]
        data.each {
            def id = it.zoneId
            if (positions.contains(id)) {
                def time = it.createdTime.getTime()

                if (it.speed) {
                    def spd = [time, (int) Math.round(it.speed)]
                    def sm = spdData.get(id)
                    if (sm != null) {
                        sm << spd
                    } else {
                        spdData << [(id): [spd]]
                    }
                }

                if (it.volume) {
                    def vol = [time, (int) Math.round(it.volume)]
                    def vm = volData.get(id)
                    if (vm != null) {
                        vm << vol
                    } else {
                        volData << [(id): [vol]]
                    }
                }

                if (it.occupancy) {
                    def occ = [time, (int) Math.round(it.occupancy * 100)]
                    def om = occData.get(id)
                    if (om != null) {
                        om << occ
                    } else {
                        occData << [(id): [occ]]
                    }
                }
           }
        }

        // Interpolation
        int defaultInterval = 300
        if (interval > defaultInterval) {
            def interp = new Interpolation()
            spdData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            volData.each {
                it.value = interp.averageArrayList(it.value, interval, 0, startTime.getTime(), endTime.getTime())
            }
            occData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
        }

        // Compose the output result
        def colors = ["#7CB5EC", "#90ED7D", "#F7A35C", "#8085E9", "#F15C80", "#E4D354", "#2B908F", "#F45B5B", "#91E8E1","#434348" ]

        def speeds = []
        spdData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerLane(permitNumber, entry.key)
            speeds << [name: lane.name, bound: lane.bound,  color: colors[i % colors.size()], data: entry.value]
        }

        def volumes = []
        volData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerLane(permitNumber, entry.key)
            volumes << [name: lane.name, bound: lane.bound,  color: colors[i % colors.size()], data: entry.value]
        }

        def occupancies = []
        occData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerLane(permitNumber, entry.key)
            occupancies << [name: lane.name, bound: lane.bound, color: colors[i % colors.size()], data: entry.value]
        }

        // Calculate zone v+o
        def voes = []
        if (computeZoneVPlusO) {
            def startIdx = voes.size()
            ['NB', 'SB'].eachWithIndex { dir, i ->
                def vo = mTrafficZoneDataService.getZoneVPlusO(permitNumber, dir, startTime, endTime)
                if (!vo.isEmpty()) {
                    voes << [name: dir + ' Zone', bound: dir, color: colors[(startIdx + i) % colors.size()], data: vo]
                }
            }
        }

        return  [speed: speeds, volume: volumes, occupancy: occupancies, vo: voes]
    }

    def getTrafficFlowPerMinuteData(int deviceId, String direction, int interval, Date startTime, Date endTime) {
        // Limit the date range within two weeks
        if (AppUtils.getDays(startTime, endTime) > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Get traffic flow data
        List<TrafficFlowPerLane> data =  mTrafficFlowPerMinuteRepository.findByDeviceIdAndCreatedTimeBetween(deviceId, startTime, endTime)
        if (data.isEmpty()) {
            return [:]
        }

        // Get positions
        def lanes = direction == null ? mTrafficDevicePerMinuteRepository.findByDeviceId(deviceId) : mTrafficDevicePerMinuteRepository.findByDeviceIdAndBound(deviceId, direction)
        def positions = lanes.collect {it.position } as Set

        // Organize the data
        def spdData = [:]
        def volData = [:]
        def occData = [:]
        data.each {
            def id = it.position
            if (positions.contains(id)) {

                def time = it.createdTime.getTime()

                if (it.speed) {
                    def spd = [time, (int) Math.round(it.speed)]
                    def sm = spdData.get(id)
                    if (sm != null) {
                        sm << spd
                    } else {
                        spdData << [(id): [spd]]
                    }
                }

                if (it.volume) {
                    def vol = [time, (int) Math.round(it.volume)]
                    def vm = volData.get(id)
                    if (vm != null) {
                        vm << vol
                    } else {
                        volData << [(id): [vol]]
                    }
                }

                if (it.occupancy) {
                    def occ = [time, (int) Math.round(it.occupancy)]
                    def om = occData.get(id)
                    if (om != null) {
                        om << occ
                    } else {
                        occData << [(id): [occ]]
                    }
                }
            }
        }

        // Interpolation
        int defaultInterval = 60
        if (interval > defaultInterval) {
            def interp = new Interpolation()
            spdData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
            volData.each {
                it.value = interp.averageArrayList(it.value, interval, 0, startTime.getTime(), endTime.getTime())
            }
            occData.each {
                it.value = interp.averageArrayList(it.value, interval, 1, startTime.getTime(), endTime.getTime())
            }
        }

        // Compose the output result
        def colors = ["#7CB5EC", "#434348", "#90ED7D", "#F7A35C", "#8085E9", "#F15C80", "#E4D354", "#2B908F", "#F45B5B", "#91E8E1"]

        def speeds = []
        spdData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerMinute(deviceId, entry.key)
            speeds << [name: lane.name, bound: lane.bound, color: colors[i % colors.size()], data: entry.value]
        }

        def volumes = []
        volData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerMinute(deviceId, entry.key)
            volumes << [name: lane.name, bound: lane.bound, color: colors[i % colors.size()], data: entry.value]
        }

        def occupancies = []
        occData.eachWithIndex{ entry, i ->
            def lane = getLaneByPositionForDevicePerMinute(deviceId, entry.key)
            occupancies << [name: lane.name, bound: lane.bound, color: colors[i % colors.size()], data: entry.value]
        }

        return  [speed: speeds, volume: volumes, occupancy: occupancies]
    }


    def getTrafficFlowAndBaseline(int deviceId, String direction, int intervalInSecs, Date startTime, Date endTime) {
        // Limit the date range within two weeks
        if (AppUtils.getDays(startTime, endTime) > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Get traffic flow data for all directions
        List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, direction, startTime, endTime)
        def speedList = []
        def volumeList = []
        def occupancyList = []
        data.each {
            speedList << [it.createdTime.getTime(), it.speed]
            volumeList << [it.createdTime.getTime(), it.volume]
            occupancyList << [it.createdTime.getTime(), it.occupancy]
        }

        // Get baseline data
        def baseline = mAnomalyThresholdService.getAnomalyThresholdList(deviceId, direction, startTime)
        def baselineSpeedList = []
        def baselineVolumeList = []
        def baselineOccupancyList = []
        def startOfDay = AppUtils.getStartOfDay(startTime)
        baseline.each {
            def time = startOfDay.getTime() + it.minute * 60000L
            baselineSpeedList << [time, Math.round(it.speed)]
            baselineVolumeList << [time, Math.round(it.volume)]
            baselineOccupancyList << [time, Math.round(it.occupancy)]
        }

        int defaultInterval = 300
        if (intervalInSecs != defaultInterval) {
            def interp = new Interpolation()
            speedList = interp.averageArrayList(speedList, intervalInSecs, 1)
            volumeList = interp.averageArrayList(volumeList, intervalInSecs, 0)
            occupancyList = interp.averageArrayList(occupancyList, intervalInSecs, 1)

            baselineSpeedList = interp.averageArrayList(baselineSpeedList, intervalInSecs, 1)
            baselineVolumeList = interp.averageArrayList(baselineVolumeList, intervalInSecs, 0)
            baselineOccupancyList = interp.averageArrayList(baselineOccupancyList, intervalInSecs, 1)
        }

        return [
            speed: speedList,
            volume: volumeList,
            occupancy: occupancyList,
            baselineSpeed: baselineSpeedList,
            baselineVolume: baselineVolumeList,
            baselineOccupancy: baselineOccupancyList
        ]
    }

    def getTrafficVolumeAndBaseline(int deviceId, String direction, int intervalInSecs, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        // Get traffic flow data
        List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, direction, startTime, endTime)

        // Get one day baseline
        int weekday = getTimeWeekday(dayTime)
        List<TrafficFlowBaseline> baseline = mTrafficFlowBaselineRepository.findByDeviceIdAndDirectionAndWeekday(deviceId, direction, weekday)

        def volumeList = data.collect { [it.createdTime.getTime(), it.volume]}
        def baselineList = []
        baseline.each {
            int hh = (int)Math.floor(it.minute/100)
            int mm = it.minute - hh * 100
            def time = startTime.getTime() + (hh * 60 + mm) * 60000L
            baselineList << [time, it.avgVolume]
        }

        int defaultInterval = 300
        if (intervalInSecs != defaultInterval) {
            def interp = new Interpolation()
            volumeList = interp.averageArrayList(volumeList, intervalInSecs, 0)
            baselineList = interp.averageArrayList(baselineList, intervalInSecs, 0)
        }

        return [
                volume: volumeList,
                baselineVolume: baselineList
        ]
    }

    def getTrafficVolumeAndBaseline(List<String> devices, int intervalInSecs, Date dayTime) {
        def result = [:]
        devices.each {device->
            def dd = device.split('-')
            def deviceId  = Integer.parseInt(dd[0].trim())
            def direction = dd[1]
            def data = getTrafficVolumeAndBaseline(deviceId, direction, intervalInSecs, dayTime)
            result << [(device): data]
        }
        return result
    }

    def getTrafficFlowBaselineVolume(Integer deviceId, String direction, Date createdTime) {
        def timeValues = getTimeValues(createdTime)
        def baselineOpt = mTrafficFlowBaselineRepository.findByDeviceIdAndDirectionAndWeekdayAndMinute(deviceId, direction, timeValues.weekday, timeValues.hhmm)
        return baselineOpt.isPresent() ? baselineOpt.get().avgVolume : null
    }

    def getTimeWeekday(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)

        // Weekday: Holiday = 0, Sunday = 1, Monday = 2, ...  Saturday = 7
        int weekday = calendar.get(Calendar.DAY_OF_WEEK)

        return weekday
    }

    def getTimeValues(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)

        // Weekday: Holiday = 0, Sunday = 1, Monday = 2, ...  Saturday = 7
        int weekday = calendar.get(Calendar.DAY_OF_WEEK)

        int hour = calendar.get(Calendar.HOUR_OF_DAY)
        int minute = calendar.get(Calendar.MINUTE)
        int index = hour * 100 + minute

        return [weekday: weekday, hhmm: index]
    }

    def getTrafficSpeedVolumeData(int deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getTrafficSpeedVolumeData(deviceId, direction, startTime, endTime)
    }

    def getTrafficSpeedVolumeData(int deviceId, String direction, Date currentTime, int secondsBeforeAndAfter) {
        Date startTime = AppUtils.minusSeconds(currentTime, secondsBeforeAndAfter)
        Date endTime   = AppUtils.addSeconds(currentTime, secondsBeforeAndAfter)
        return getTrafficSpeedVolumeData(deviceId, direction, startTime, endTime)
    }

    def getTrafficSpeedVolumeData(int deviceId, String direction, Date startTime, Date endTime) {
        // Organize the data
        List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, direction, startTime, endTime)

        def result = []
        data.each {
            def time = it.createdTime.getTime()
            if (it.speed != null && it.volume != null && it.speed > 0 && it.volume > 0) {
                result << [x: it.volume, y: it.speed, time: time]
            }
        }

        return  result
    }

    def getAllLCMDetectors() {
        return mTrafficLcmParamRepository.findAll().collect {
            return [deviceId: it.deviceId, direction: it.direction]
        }
    }

    def generateLCMCurve(Integer deviceId, String direction, int count) {
        Optional<TrafficLcmParam> opt = mTrafficLcmParamRepository.findByDeviceIdAndDirection(deviceId, direction)
        if (!opt.isPresent()) {
            return []
        }

        def result = []

        TrafficLcmParam param = opt.get()

        def vf = param.vf
        def vc = param.spdC
        def Vc = param.volC
        def va = param.spdA
        def Va = param.volA

        // Curve 3
        def interval = 0.1
        def speed = 0
        while (speed < va) {
            def volume = param.a3 * Math.pow(speed, 2) + param.b3 * speed
            result << [volume, speed]
            speed += interval
        }

        // Curve 2
        speed = va
        while (speed < vc) {
            def volume = param.a2 * Math.pow(speed, 3) + param.b2 * Math.pow(speed, 2) + param.c2 * speed + param.d2
            result << [volume, speed]
            speed += interval
        }

        // LCM
        while (speed < vf - 0.2) {
            def volume = volumeFromParameters(speed, vf, param.r, param.t, param.l)
            result << [volume, speed]
            speed += 0.01
        }

        // Fill the horizontal line
        def n3 = 30
        def last = result.last()
        def lastVolume = last[0]
        def interval3 = lastVolume/n3
        for (int i = 0; i < n3 ; i++) {
            def volume = lastVolume - i * interval3
            result << [volume, vf]
        }

        return [
                curve: result,
                Vc: Vc,
                vc: vc,
                vf: vf,
                Va: Va,
                va: va
        ]
    }

    def volumeListFromParameters(def speedList, def vf, double gamma, double tao, double l ) {
        return speedList.collect { v->
            def a = v/vf
            a = a < 1.0 ? a : 0.999
            return v/(gamma * v * v + tao * v + l)/(1.0 - Math.log(1.0 - a))
        }
    }

    def volumeFromParameters(def speed, def vf, double gamma, double tao, double l ) {
        def v = speed
        def a = v/vf
        a = a < 1.0 ? a : 0.999
        return v/(gamma * v * v + tao * v + l)/(1.0 - Math.log(1.0 - a))
    }
}
