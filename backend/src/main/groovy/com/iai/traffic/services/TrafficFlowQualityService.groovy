package com.iai.traffic.services


import com.iai.traffic.repository.anomaly.TrafficFlowQualityRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficFlowQualityService {
    @Inject TrafficFlowQualityRepository mTrafficFlowQualityRepository
    @Inject TrafficFlowDataService mTrafficFlowDataService

    static int DATA_QUALITY_TOTAL_TYPE_COUNT = 8

    def getErrorDistribution(Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        // Get data
        def data = mTrafficFlowQualityRepository.findByDateCreatedBetween(startTime, endTime)
        if (data.size() == 0) {
            return [:]
        }

        // Total error count by time (Heatmap)
        def totalErrorCounts = new int[288]
        for (int i = 0;i < 288; i++) {
            totalErrorCounts[i] = 0
        }
        data.each {
            def idx = AppUtils.get5MinIndex(it.createdTime)
            totalErrorCounts[idx] ++
        }

        // Error percentage by type (PieChart)
        def errorCountsByType = new int[DATA_QUALITY_TOTAL_TYPE_COUNT]
        for (int i = 0;i < 8; i++) {
            errorCountsByType[i] = 0
        }
        data.each {
            def idx = it.status
            if (idx >= 0 && idx <= 7) {
                errorCountsByType[idx]++
            }
        }

        // Error percentage by sensor (PieChart)
        def sensorErrorCounts = {}
        data.each {
            def key = it.deviceId
            def counts = sensorErrorCounts.get(key)
            if (counts == null) {
                sensorErrorCounts << [(key): 1]
            } else {
                sensorErrorCounts << [(key): counts + 1]
            }
        }

        // Error percentage by hour (PieChart)
        def errorCountsByHour = new int[24]
        for (int i = 0;i < 24; i++) {
            errorCountsByHour[i] = 0
        }
        data.each {
           def idx = AppUtils.getHourIndex(it.createdTime)
            errorCountsByHour[idx] ++;
        }

        // Hourly sensor error count (heatmap)
        def hourlySensorErrorCounts = {}
        data.each {
            def idx = AppUtils.getHourIndex(it.createdTime)
            def key = it.deviceId
            def hourlyErrors = hourlySensorErrorCounts.get(key)
            if (hourlyErrors == null) {
                hourlyErrors = new int[24]
                for (int i = 0; i < 24; i++) {
                    hourlyErrors[i] = 0
                }
                hourlyErrors[idx] ++
                hourlySensorErrorCounts << [(key): hourlyErrors]
            } else {
                hourlyErrors[idx] ++
            }
        }

        return [
                totalErrorCounts: totalErrorCounts,
                errorCountsByType: errorCountsByType,
                sensorErrorCounts: sensorErrorCounts,
                errorCountsByHour: errorCountsByHour,
                hourlySensorErrorCounts: hourlySensorErrorCounts
        ]
    }

    def getSensorErrorCountPercentage(Integer deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        // Get data
        def data = mTrafficFlowQualityRepository.findByDeviceIdAndDateCreatedBetween(deviceId, startTime, endTime)



        // Total error counts
        def errorCountsByType = new int[DATA_QUALITY_TOTAL_TYPE_COUNT]
        data.each {
            if (direction == '' || direction == it.direction) {
                def idx = it.status
                if (idx >= 0 && idx < DATA_QUALITY_TOTAL_TYPE_COUNT) {
                    errorCountsByType[idx]++
                }
            }
        }

        def totalPoints = 288
        if (AppUtils.isToday(dayTime)) {
            Date current = new Date()
            def idx = AppUtils.get5MinIndex(current)
            totalPoints = idx + 1
        }

        def factor = 1
        if (direction == '') {
            def directions = mTrafficFlowDataService.getTrafficDirections(deviceId)
            factor = directions.size()
        }
        totalPoints *= factor

        // Convert to percentage
        def totalCounts = 0
        for (int i = 0; i < DATA_QUALITY_TOTAL_TYPE_COUNT; i++) {
            if (errorCountsByType[i] > 0) {
                totalCounts += errorCountsByType[i]
                errorCountsByType[i] = Math.min(100, Math.round(errorCountsByType[i] * 100 / totalPoints))
            }
        }
        totalCounts = Math.min(100, Math.round(totalCounts * 100 / totalPoints))

        return [errors: errorCountsByType, total: totalCounts]
    }
}
