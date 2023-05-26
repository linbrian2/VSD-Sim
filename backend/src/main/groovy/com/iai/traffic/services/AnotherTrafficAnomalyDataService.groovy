package com.iai.traffic.services


import com.iai.traffic.repository.anomaly.TrafficAnomalyResultRepository
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class AnotherTrafficAnomalyDataService {
    @Inject TrafficAnomalyResultRepository mTrafficAnomalyResultRepository

    def getAnomalyResultsHour(Integer deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getAnomalyResultsHour(deviceId, direction, startTime, endTime)
    }

    def getAnomalyResultsHour(Integer deviceId, String direction, Date startTime, Date endTime) {

        // Get data
        def data = mTrafficAnomalyResultRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)
        if (data.size() == 0) {
            return [:]
        }

        //println("$deviceId, $direction, $startTime, $endTime, count=${data.size()}")

        def xcategories = []
        def ycategories = ['Original', 'LSTM-05', 'LSTM-10', 'LSTM-15', 'XGBM-05', 'XGBM-10', 'XGBM-15']

        def rowCount = ycategories.size()
        def colCount = 288
        for (int i = 0; i < colCount; i++) {
            xcategories << getTimeBy5MinIdx(startTime, i).getTime()
        }

        // Arrange data
        int[][] matrix = new int[rowCount][colCount]
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                matrix[i][j] = -1
            }
        }

        data.each {
            def j = get5MinIdx(it.time)
            if (it.status != null) {
                matrix[0][j] = it.status
            }
            if (it.statusL5 != null) {
                matrix[1][j] = it.statusL5
            }
            if (it.statusL10 != null) {
                matrix[2][j] = it.statusL10
            }
            if (it.statusL15 != null) {
                matrix[3][j] = it.statusL15
            }
            if (it.statusX5 != null) {
                matrix[4][j] = it.statusX5
            }
            if (it.statusX10 != null) {
                matrix[5][j] = it.statusX10
            }
            if (it.statusX15 != null) {
                matrix[6][j] = it.statusX15
            }
        }

        // Format to HighCharts heatmap data format
        def result = []
        for (int x = 0; x < colCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                result << [x, y, matrix[y][x]]
            }
        }

        return [
                xcategories: xcategories,
                ycategories: ycategories,
                series: result
        ]
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
