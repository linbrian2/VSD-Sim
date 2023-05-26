package com.iai.traffic.services

import com.iai.traffic.domain.traffic.TrafficFlow
import com.iai.traffic.repository.traffic.TrafficFlowRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficVisionService {
    @Inject CameraVisionService mCameraVisionService
    @Inject JetsonVisionService mJetsonVisionService
    @Inject TrafficFlowRepository mTrafficFlowRepository

    def getAllDevices() {
        def devices1 = mCameraVisionService.getAllDevices()
        def devices2 = mJetsonVisionService.getAllDevices()
        def devices = devices1 + devices2
        return devices
    }

    def getDeviceInfo(int deviceId, String type) {
        if (type.equals(Constants.VISION_CAMERA)) {
            return mCameraVisionService.getDeviceInfo(deviceId)
        } else {
            return mJetsonVisionService.getDeviceInfo(deviceId)
        }
    }

    def getTrafficVision5Min(int deviceId, String type, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        if (type.equals(Constants.VISION_CAMERA)) {
            def data = mCameraVisionService.getTrafficVision(deviceId, startTime, endTime)
            return regulateDataTo5Minute(data, startTime)
        } else {
            def data = mJetsonVisionService.getTrafficVision(deviceId, startTime, endTime)
            return regulateDataTo5Minute(data, startTime)
        }
    }

    def getTrafficVisionResults(int deviceId, String type, Date dayTime) {
        if (type.equals(Constants.VISION_CAMERA)) {
            return mCameraVisionService.getTrafficVisionResults(deviceId, dayTime)
        } else {
            return mJetsonVisionService.getTrafficVisionResults(deviceId, dayTime)
        }
    }

    def getTrafficVisionVideos(int deviceId, String type, Date dayTime) {
        if (type.equals(Constants.VISION_CAMERA)) {
            return mCameraVisionService.getTrafficVisionVideos(deviceId, dayTime)
        } else {
            return mJetsonVisionService.getTrafficVisionVideos(deviceId, dayTime)
        }
    }

    def getTrafficCounts(int deviceId, String type, Date dayTime, int intervalInMs, boolean skipIfAllZero) {
        if (type.equals(Constants.VISION_CAMERA)) {
            return mCameraVisionService.getTrafficCounts(deviceId, dayTime, intervalInMs, skipIfAllZero)
        } else {
            return mJetsonVisionService.getTrafficCounts(deviceId, dayTime, intervalInMs, skipIfAllZero)
        }
    }

    def getTrafficFlow(String detectorId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        def dd = detectorId.split('-')
        def direction = dd[1]
        def deviceId  = Integer.parseInt(dd[0].trim())

        List<TrafficFlow> data = mTrafficFlowRepository.findByDeviceIdAndDirectionAndCreatedTimeBetween(deviceId, direction, startTime, endTime)

        def spdList = []
        def volList = []
        def occList = []
        data.each {
            def time = it.createdTime.getTime()
            if (it.speed != null && it.speed > 0) {
                spdList << [time, it.speed]
            }
            if (it.volume != null && it.volume > 0) {
                volList << [time, it.volume]
            }
            if (it.occupancy != null && it.occupancy > 0) {
                occList << [time, it.occupancy]
            }
        }

        return [
                speed: spdList,
                volume: volList,
                occupancy: occList
        ]
    }


    def regulateDataTo5Minute(def data, Date startTime) {
        int SPEED_IDX = 0
        int VOLUME_IDX = 1
        int OCC_IDX = 2

        // Get 5-minute interval
        int binSize  = 12 * 24
        int interval = 300000  // 5 minutes

        int[][] bin = new int[binSize][3]
        int[][] ccc = new int[binSize][3]
        for(int i = 0; i < binSize; i++) {
            for (int j = 0; j < 3; j++) {
                bin[i][j] = ccc[i][j] = 0
            }
        }

        // Project it to 288-length bin
        data.each { d->
            Date t = d.createdTime
            int  idx = (int)((t.getTime() - startTime.getTime())/interval)
            if(idx >= 0 && idx < binSize) {
                if (d.speed != null && d.speed > 0) {
                    bin[idx][SPEED_IDX] += d.speed
                    ccc[idx][SPEED_IDX] ++
                }
                if (d.volume != null && d.volume > 0) {
                    bin[idx][VOLUME_IDX] += d.volume
                    ccc[idx][VOLUME_IDX] ++
                }
                if (d.occupancy != null && d.occupancy > 0) {
                    bin[idx][OCC_IDX] += d.occupancy
                    ccc[idx][OCC_IDX] ++
                }
            }
        }

        // Average speed and occupancy
        for(int i = 0; i < binSize; i++) {
            if (ccc[i][SPEED_IDX] > 0) {
                bin[i][SPEED_IDX] /= ccc[i][SPEED_IDX]
            }
            if (ccc[i][OCC_IDX] > 0) {
                bin[i][OCC_IDX] /= ccc[i][OCC_IDX]
            }
        }

        def results = [[],[],[]]
        for (int idx = 0; idx < 3; idx++) {
            // Remove the first zero appearances
            int startIdx = 0
            for (int i = 0; i < binSize; i++) {
                if (bin[i][idx] > 0) {
                    startIdx = i
                    break
                }
            }

            // Remove the last zero appearances
            int endIdx = binSize - 1
            for (int i = binSize - 1; i > startIdx; i--) {
                if (bin[i][idx] > 0) {
                    endIdx = i
                    break
                }
            }

            if (endIdx - startIdx > 0) {
                for (int i = startIdx; i < endIdx; i++) {
                    def t = startTime.getTime() + (long)(i * interval)
                    def v = bin[i][idx]
                    if (v > 0) {
                        results[idx] << [t, v]
                    }
                }
            }
        }

        return [
                speed: results[SPEED_IDX],
                volume: results[VOLUME_IDX],
                occupancy: results[OCC_IDX]
        ]
    }
}
