package com.iai.traffic.services


import com.iai.traffic.domain.traffic.TrafficFlowPerLane
import com.iai.traffic.domain.traffic.TrafficResponsiveZone
import com.iai.traffic.repository.solution.SignalRecommendationRepository
import com.iai.traffic.repository.traffic.SignalPatternRepository
import com.iai.traffic.repository.traffic.TrafficDeviceRepository
import com.iai.traffic.repository.traffic.TrafficFlowPerLaneRepository
import com.iai.traffic.repository.traffic.TrafficResponseCycleLevelRepository

import com.iai.traffic.repository.traffic.TrafficResponsiveZoneRepository
import com.iai.traffic.repository.traffic.TrafficSignalDeviceRepository
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficZoneDataService {
    @Inject TrafficDeviceRepository mTrafficDeviceRepository
    @Inject TrafficSignalDeviceRepository mTrafficSignalDeviceRepository
    @Inject TrafficFlowPerLaneRepository mTrafficFlowPerLaneRepository
    @Inject TrafficResponsiveZoneRepository mTrafficResponsiveZoneRepository
    @Inject TrafficResponseCycleLevelRepository mTrafficResponseCycleLevelRepository
    @Inject SignalPatternRepository mSignalPatternRepository
    @Inject SignalRecommendationRepository mSignalRecommendationRepository

    def mTrafficZoneList = [:]

    @PostConstruct
    def initialize() {
        log.info("TrafficZoneDataService: loading traffic zone ...")
        buildTrafficZoneList();
    }

    def buildTrafficZoneList() {
        // Load all devices
        def allDevices = [:]
        mTrafficDeviceRepository.findAll().collect {
            def d = [
                    id:   it.deviceId,
                    uid:  it.uid,
                    title: it.title,
                    name: it.deviceId + " - " +  it.uid + " - " + it.title,
                    position: [lat: it.lat, lng: it.lon],
            ]
            allDevices << [(it.uid): d]
        }

        // Load all zones
        def uniqueDevices = new HashSet<String>()
        mTrafficResponsiveZoneRepository.findAll().each {
            if (it.channelNumber == 5 || it.channelNumber == 6) {
                def zoneId = it.zoneId
                if (!uniqueDevices.contains(it.permitNumber)) {
                    uniqueDevices.add(it.permitNumber)

                    def device = allDevices.get(it.permitNumber)
                    if (device != null) {
                        device << [zoneId: zoneId]
                        def zone = mTrafficZoneList.get(zoneId)
                        if (zone == null) {
                            zone = [title: it.zoneTitle, devices: [device]]
                            mTrafficZoneList << [(zoneId): zone]
                        } else {
                            zone.devices << device
                        }
                    }
                }
            }
        }
    }

    def getZoneList() {
        if (mTrafficZoneList.isEmpty()) {
            buildTrafficZoneList()
        }
        return mTrafficZoneList
    }

    def getZoneDetectors(String zoneId) {
        if (mTrafficZoneList.isEmpty()) {
            buildTrafficZoneList()
        }
        return mTrafficZoneList.get(zoneId)
    }

    def getZoneDetectorWeights(String zoneId) {
        def result = [:]
        def directions = ['NB': 6, 'SB': 5]
        directions.each {direction, channel ->
            def devices = mTrafficResponsiveZoneRepository.findByZoneIdAndChannelNumber(zoneId, channel)
            def uniqueDevices = new HashSet<String>()
            def items = []
            devices.each {
                if (!uniqueDevices.contains(it.permitNumber)) {
                    uniqueDevices.add(it.permitNumber)
                    def item = [zoneId: it.zoneId, direction: direction, permit: it.permitNumber, deviceId: it.deviceId, weight: it.weight]
                    items << item
                }
            }
            result << [(direction): items]
        }
        return result
    }

    def updateZoneDetectorWeights(def data) {
        def directions = ['NB': 6, 'SB': 5]
        data.each {
            if (it.zoneId != null && it.direction != null && it.permit != null) {
                def zoneId  = it.zoneId
                def channel = directions.get(it.direction)
                def permit  = it.permit
                List<TrafficResponsiveZone> zones = mTrafficResponsiveZoneRepository.findByZoneIdAndChannelNumberAndPermitNumber(zoneId, channel, permit)
                zones.each {trz->
                    trz.weight = it.weight
                    mTrafficResponsiveZoneRepository.update(trz)
                }
            }
        }
    }

    def getZoneVPlusO(String permitNumber, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getZoneVPlusO(permitNumber, direction, startTime, endTime)
    }

    def getZoneVPlusO(String permitNumber, String direction, Date startTime, Date endTime) {
        // 'EB' and 'WB' are not supported yet
        if (direction == 'EB' || direction == 'WB' || permitNumber == null) {
            return []
        }
        def signal = mTrafficSignalDeviceRepository.findByPermit(permitNumber)
        if (!signal.isPresent()) {
            //log.info('This intersection is not part of a coordinated group')
            return []
        }

        // Get devices belong to the same group id
        def zoneId = signal.get().groupId
        if (zoneId == null) {
            return []
        }

        return getZoneVO(zoneId, direction, startTime, endTime, [:])
    }

    def getTrafficResponsiveZoneData(String zoneId, Date dayTime, def customWeights, boolean includeZoneDetectors) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        def NBVO = getZoneVO(zoneId, 'NB', startTime, endTime, customWeights.get('NB'))
        def SBVO = getZoneVO(zoneId, 'SB', startTime, endTime, customWeights.get('SB'))
        def levels = getThresholdLevels(zoneId)
        def patterns = getTimingPattern(zoneId, startTime, endTime)
        def recomms = getRecommendedTimingPattern(zoneId, startTime, endTime)

        def result = [NBVO: NBVO, SBVO:SBVO, levels: levels, patterns: patterns, recomms: recomms]

        if (includeZoneDetectors) {
            def zoneDetectors = getZoneDetectors(zoneId)
            result << [detectors: zoneDetectors]
        }

        return result
    }

    def getZoneVO(String zoneId, String direction, Date startTime, Date endTime, def customWeights) {
        def channel = direction == 'NB' ? 6 : 5
        def devices = mTrafficResponsiveZoneRepository.findByZoneIdAndChannelNumber(zoneId, channel)

        // Query traffic flow per lane data based for all selected devices
        def items = [:]

        def deviceData = [:]
        devices.each {device ->
            def permit = device.permitNumber
            List<TrafficFlowPerLane> data = deviceData.get(permit)
            if (data == null) {
                data = mTrafficFlowPerLaneRepository.findByPermitNumberAndCreatedTimeBetween(permit, startTime, endTime)
                deviceData << [(permit): data]
            }

            def weight = device.weight
            data.each {flow->
                if (flow.zoneId == device.position) {
                    Date time = AppUtils.round5Min(flow.createdTime)
                    def timestamp = time.getTime()
                    if (flow.volume) {
                        def occupancy = flow.occupancy ?: 0
                        def vo = Math.round(occupancy * 100 + flow.volume * 100.0/150.0)
                        def list = items.get(timestamp)
                        if (list != null) {
                            list << [permit: permit, vo: vo]
                        } else {
                            items << [(timestamp): [[permit: permit, vo: vo]]]
                        }
                    }
                }
            }
        }

        // Compose weights (custom weights have priority over the device weights)
        def weights = [:]
        devices.each { device ->
            def permit = device.permitNumber
            def weight = device.weight
            if (customWeights != null && customWeights.containsKey(permit)) {
                weight = customWeights.get(permit)
            }
            weights << [(permit) : weight]
        }

        // Average the calculated VO
        def result = []
        items.each { time, values->
            // Calculate the mean
            def total = 0
            def denominator = 0
            values.each { value->
                def weight = weights.getOrDefault(value.permit, 1.0)
                total += value.vo * weight
                denominator += weight
            }

            def mean = Math.floor(denominator != 0 ? total/denominator : 0)

            // Add to result
            result << [time, mean ]
        }

        // Sort based on time
        result.sort { it[0] }

        return result
    }

    def getThresholdLevels(String zoneId) {
        def data = mTrafficResponseCycleLevelRepository.findByZoneIdAndCalculatorType(zoneId, 4)

        def result = []
        data.each {
            if (it.exitValue > 0) {
                result << [it.exitValue, it.entryValue]
            }
        }
        return result
    }

    def getTimingPattern(String zoneId, Date startTime, Date endTime) {
        def result = []

        // Move start time a bit to make sure we get data before the start time
        def startTime0 = AppUtils.addSeconds(startTime, -12 * 3600)
        def data = mSignalPatternRepository.findByGroupIdAndTimestampBetweenOrderByTimestampAsc(zoneId, startTime0, endTime)

        def prevPattern = null

        // Obtain the last one record before start time otherwise there will be missing point
        def oldRecords = data.findAll {it.timestamp.before(startTime) }
        if (!oldRecords.isEmpty()) {
            def signalPattern = oldRecords.last()
            def pattern = extractPattern(signalPattern.pattern)
            if (!pattern.isEmpty()) {
                result << [startTime.getTime(), pattern.cycleLength, pattern.tag, pattern.id]
                prevPattern = pattern
            }
        }

        // Continue for the current day's data
        data.each {
            if (it.timestamp.after(startTime)) {
                def pattern = extractPattern(it.pattern)
                if (!pattern.isEmpty()) {
                    def time = it.timestamp.getTime()
                    if (prevPattern != null) {
                        result << [time, prevPattern.cycleLength, prevPattern.tag, prevPattern.id]
                    }
                    result << [time, pattern.cycleLength, pattern.tag, pattern.id]
                    prevPattern = pattern
                }
            }
        }

        // Filling the missing entry till the end of the day
        if (result.size() > 0) {
            def last = result.last()
            if (AppUtils.isToday(endTime)) {
                endTime = new Date()
            }
            result << [endTime.getTime(), last[1], last[2], last[3]]
        }

        return result
    }

    def getRecommendedTimingPattern(String zoneId, Date startTime, Date endTime) {
        // Move start time a bit to make sure we get data before the start time
        def startTime0 = AppUtils.addSeconds(startTime, -12 * 3600)

        // Fetch all data based on zoneId and time
        def data = mSignalRecommendationRepository.findByGroupIdAndTimestampBetween(zoneId, startTime0, endTime)
        if (data.isEmpty()) {
            return []
        }

        // Group data by permit
        def signalByPermit = [:]
        data.each {
            def permit = it.permit
            def signals = signalByPermit.get(permit)
            if (signals != null) {
                signals << it
            } else {
                signalByPermit << [(permit): [it]]
            }
        }

        // Pick one signals for a permit since every signal shares the same pattern within the groupId
        def signals = signalByPermit.values().first()

        // Sort the signals by timestamp
        def sortedSignals = signals.sort { it.timestamp }

        def result = []
        def prevPattern = null
        def prevPatternString = null

        // Obtain the last one record before start time otherwise there will be a missing point
        def oldRecords = sortedSignals.findAll {it.timestamp.before(startTime) }
        if (!oldRecords.isEmpty()) {
            def signalPattern = oldRecords.last()
            def pattern = extractPattern(signalPattern.pattern)
            if (!pattern.isEmpty()) {
                result << [startTime.getTime(), pattern.cycleLength, pattern.tag, pattern.id]
                prevPattern = pattern
                prevPatternString = signalPattern.pattern
            }
        }

        // Filter out the same pattern
        def selectedData = []
        sortedSignals.each {
            if (it.timestamp.after(startTime) ) {
                if (it.pattern != prevPatternString) {
                    selectedData << it
                    prevPatternString = it.pattern
                }
            }
        }

        // Compose the result
        selectedData.each {
            def pattern = extractPattern(it.pattern)
            if (!pattern.isEmpty()) {
                def time = it.timestamp.getTime()
                if (prevPattern != null) {
                    result << [time, prevPattern.cycleLength, prevPattern.tag, prevPattern.id]
                }
                result << [time, pattern.cycleLength, pattern.tag, pattern.id]
                prevPattern = pattern
            }
        }

        // Filling the missing entry till the end of the day
        if (result.size() > 0) {
            def last = result.last()
            if (AppUtils.isToday(endTime)) {
                endTime = new Date()
            }
            result << [endTime.getTime(), last[1], last[2], last[3]]
        }

        return result
    }

    def getCurrentRecommendedTimingPattern(String permit) {
        // Get data within last 25 minutes
        Date endTime   = new Date()
        Date startTime = new Date(endTime.getTime() - 25 * 60 * 1000L)
        def data = mSignalRecommendationRepository.findByPermitAndTimestampBetweenOrderByTimestampDesc(permit, startTime, endTime)
        if (data.isEmpty()) {
            return [:]
        }
        return data.first()
    }


    def getCurrentRecommendedTimingPatternList(String zoneId) {
        // Get data within last 25 minutes
        Date endTime   = new Date()
        Date startTime = new Date(endTime.getTime() - 25 * 60 * 1000L)

        // Fetch all data based on zoneId and time
        def data = mSignalRecommendationRepository.findByGroupIdAndTimestampBetween(zoneId, startTime, endTime)
        if (data.isEmpty()) {
            return []
        }

        // Group data by permit
        def signalByPermit = [:]
        data.each {
            def permit = it.permit
            def signals = signalByPermit.get(permit)
            if (signals != null) {
                signals << it
            } else {
                signalByPermit << [(permit): [it]]
            }
        }

        // Output the latest timing pattern
        def result = []
        signalByPermit.each { permit, signals->
            def latest = signals.max { it.timestamp }

            // Create POJO item
            def item = [
               id:   latest.id,
               permit: latest.permit,
               cms: latest.cms,
               saturationDegree: latest.saturationDegree,
               levelOfService: latest.levelOfService,
               progressionRatio: latest.progressionRatio,
               pattern: latest.pattern,
               cycleLength: latest.cycleLength,
               patternId: latest.patternId
            ]
            // Add IP address to it
            def signalOpt = mTrafficSignalDeviceRepository.findByPermit(permit)
            if (signalOpt.isPresent()) {
                def ip = signalOpt.get().ip
                item << [ip: ip]
            }

            result << item
        }

        return result
    }

    def getCurrentSignalPattern(String groupId) {
        if (groupId != null && groupId.length() > 0) {
            Date dayTime = new Date()
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime = AppUtils.getEndOfDay(dayTime)

            // Move start time a bit to make sure we get enough data
            Date startTime0 = AppUtils.addSeconds(startTime, -6 * 3600)
            def signalPatterns = mSignalPatternRepository.findByGroupIdAndTimestampBetweenOrderByTimestampDesc(groupId, startTime0, endTime)
            if (!signalPatterns.isEmpty()) {
                def signalPattern = signalPatterns.first()
                def p = extractPattern(signalPattern.pattern)
                if (!p.isEmpty()) {
                    return [pattern: p.id, timestamp: signalPattern.timestamp]
                }
            }
        }
        return [:]
    }

    def extractPattern(String pattern) {
        def result = [:]

        if (pattern.isEmpty()) {
            return result
        }

        String[] items = pattern.split('-')
        if (items.length == 2) {
            // 141 - 100 BAL
            String[] parts = items[1].trim().split("\\s+")
            if (parts.length > 0) {
                def id = Integer.valueOf(items[0].trim())
                def cycleLength = Integer.valueOf(parts[0].trim())
                def tag = ""
                if (parts.length > 1) {
                    StringBuilder sb = new StringBuilder()
                    for (int i = 1; i < parts.length; i++) {
                        sb.append(parts[i].trim())
                        if (i < parts.length - 1) {
                            sb.append(' ')
                        }
                    }
                    tag = sb.toString()
                }

                result << [id: id, cycleLength: cycleLength, tag: tag]
            }
        }

        return result
    }
}
