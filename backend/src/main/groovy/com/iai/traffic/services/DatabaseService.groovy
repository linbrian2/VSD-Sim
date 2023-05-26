package com.iai.traffic.services


import com.iai.traffic.domain.traffic.TrafficFlow
import com.iai.traffic.domain.traffic.TrafficVision
import com.iai.traffic.domain.traffic.VisionCamera
import com.iai.traffic.repository.traffic.ReleaseNotesRepository
import com.iai.traffic.repository.traffic.TrafficBreakdownParamRepository
import com.iai.traffic.repository.traffic.TrafficFlowPredictionRepository
import com.iai.traffic.repository.traffic.TrafficFlowRepository
import com.iai.traffic.repository.traffic.TrafficVisionRepository
import com.iai.traffic.repository.traffic.VisionCameraRepository
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton
import org.apache.commons.codec.binary.Base64;

@Singleton
@Slf4j
class DatabaseService {
    @Inject ReleaseNotesRepository mReleaseNotesRepository
    @Inject TrafficFlowRepository mTrafficFlowRepository
    @Inject TrafficFlowPredictionRepository mTrafficFlowPredictionRepository

    def getReleaseNotes(String version) {
        if (version == null || version.equals('')) {
            return ''
        }
        def opt = mReleaseNotesRepository.findByVersionNumber(version)
        return opt.isPresent() ? opt.get().notes : ''
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

    def getTrafficFlowPrediction(int deviceId, String direction, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        def data = mTrafficFlowPredictionRepository.findByDeviceIdAndDirectionAndTimeBetween(deviceId, direction, startTime, endTime)

        def predictions = [:]
        data.each {
            def method = it.method
            def time = it.time.getTime()

            def container = predictions.get(method)
            if (container == null) {
                container = [:]
                predictions << [(method): container]
            }
            addPredictionValue(container, time, it)
        }

        return predictions
    }

    def getTrafficFlowPrediction(int deviceId, String direction, int method, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        def data = mTrafficFlowPredictionRepository.findByDeviceIdAndDirectionAndMethodAndTimeBetween(deviceId, direction, method, startTime, endTime)

        def predictions = [:]
        data.each {
            def time = it.time.getTime()
            addPredictionValue(predictions, time, it)
        }

        return predictions
    }

    def addPredictionValue(def predictions, long time, def value) {
        // Pred-5
        def pred5 = predictions.get('pred5')
        if (pred5 == null) {
            pred5 = [:]
            predictions << ['pred5': pred5]
        }
        def speed5 = pred5.get('speed')
        if (speed5 == null) {
            speed5 = []
            pred5 << ['speed': speed5]
        }
        speed5 << [time, value.speed5]

        def volume5 = pred5.get('volume')
        if (volume5 == null) {
            volume5 = []
            pred5 << ['volume': volume5]
        }
        volume5 << [time, value.volume5]

        def occupancy5 = pred5.get('occupancy')
        if (occupancy5 == null) {
            occupancy5 = []
            pred5 << ['occupancy': occupancy5]
        }
        occupancy5 << [time, value.occupancy5]

        // Pred-10
        def pred10 = predictions.get('pred10')
        if (pred10 == null) {
            pred10 = [:]
            predictions << ['pred10': pred10]
        }
        def speed10 = pred10.get('speed')
        if (speed10 == null) {
            speed10 = []
            pred10 << ['speed': speed10]
        }
        speed10 << [time, value.speed10]

        def volume10 = pred10.get('volume')
        if (volume10 == null) {
            volume10 = []
            pred10 << ['volume': volume10]
        }
        volume10 << [time, value.volume10]

        def occupancy10 = pred10.get('occupancy')
        if (occupancy10 == null) {
            occupancy10 = []
            pred10 << ['occupancy': occupancy10]
        }
        occupancy10 << [time, value.occupancy10]

        // Pred-15
        def pred15 = predictions.get('pred15')
        if (pred15 == null) {
            pred15 = [:]
            predictions << ['pred15': pred15]
        }
        def speed15 = pred15.get('speed')
        if (speed15 == null) {
            speed15 = []
            pred15 << ['speed': speed15]
        }
        speed15 << [time, value.speed15]

        def volume15 = pred15.get('volume')
        if (volume15 == null) {
            volume15 = []
            pred15 << ['volume': volume15]
        }
        volume15 << [time, value.volume15]

        def occupancy15 = pred15.get('occupancy')
        if (occupancy15 == null) {
            occupancy15 = []
            pred15 << ['occupancy': occupancy15]
        }
        occupancy15 << [time, value.occupancy15]
    }
}
