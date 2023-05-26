package com.iai.traffic.services

import com.iai.traffic.repository.traffic.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficSignalDeviceService {
    @Inject SignalPatternRepository mSignalPatternRepository
    @Inject SignalCycleSplitOffsetRepository mSignalCycleSplitOffsetRepository
    @Inject TrafficSignalDeviceRepository mTrafficSignalDeviceRepository
    @Inject TrafficZoneDataService mTrafficZoneDataService

    def mTrafficSignalDeviceList = []

    @PostConstruct
    def initialize() {
        log.info("TrafficSignalDeviceService: loading traffic signal devices ...")
        getTrafficSignalDevices()
    }

    def getTrafficSignalDevices() {
        if (mTrafficSignalDeviceList.isEmpty()) {
            mTrafficSignalDeviceRepository.findAll().each {
                if (it.permit != null && it.groupId != null) {
                    def item = [
                            id       : it.id,
                            uid      : it.permit,
                            name     : it.permit + ": " + it.name,
                            groupId  : it.groupId,
                            groupName: it.groupName,
                            position : [lat: it.lat, lng: it.lon],
                            enabledTr: it.enabledTr,
                            region   : it.region
                    ]
                    mTrafficSignalDeviceList << item
                }
            }
        }
        return mTrafficSignalDeviceList
    }

    def getSignalCycleSplit(String permit) {
        def data = mSignalCycleSplitOffsetRepository.findByPermit(permit)

        def patternGroups = [:]
        data.each {
            def pattern = it.pattern
            def item = patternGroups.get(pattern)
            if (item == null) {
                item = [pattern: it.pattern, cycle: it.cycleLength,offset: it.offsetTime]
                for (int i = 1; i <= 8; i++) {
                    item << [('P' + i): '/']
                }
                patternGroups << [(pattern) : item]
            }

            item << [('P' + it.phase): it.phaseTime]
            item << [('M' + it.phase): it.phaseMode]
        }

        def device  = getSignalDeviceInfo(permit)
        def pattern = getTimingPattern(device.groupId)
        def current = mTrafficZoneDataService.getCurrentSignalPattern(device.groupId)
        def recommended = mTrafficZoneDataService.getCurrentRecommendedTimingPatternList(device.groupId)

        return [device: device, current: current, pattern: pattern, signal: patternGroups.values(), recommended: recommended]
    }

    def getSignalDeviceInfo(String permit) {
        def signalOpt = mTrafficSignalDeviceRepository.findByPermit(permit)
        return signalOpt.isPresent() ? signalOpt.get() : [:]
    }

    def getTimingPattern(String groupId) {
        if (groupId == null || groupId.length() == 0) {
            return []
        }

        Date dayTime = new Date()
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return mTrafficZoneDataService.getTimingPattern(groupId, startTime, endTime)
    }
}
