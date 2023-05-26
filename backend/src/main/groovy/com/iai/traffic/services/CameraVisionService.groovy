package com.iai.traffic.services

import com.iai.traffic.domain.traffic.TrafficVision
import com.iai.traffic.domain.traffic.VisionCamera
import com.iai.traffic.repository.traffic.TrafficVisionRepository
import com.iai.traffic.repository.traffic.VisionCameraRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j
import org.apache.commons.codec.binary.Base64

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class CameraVisionService {
    @Inject VisionCameraRepository mVisionCameraRepository
    @Inject TrafficVisionRepository mTrafficVisionRepository

    def getAllDevices() {
        List<VisionCamera> devices = mVisionCameraRepository.findByEnabled(1)
        return devices.collect {
            return composeDeviceInfo(it)
        }
    }

    def getDeviceInfo(int deviceId) {
        VisionCamera device = mVisionCameraRepository.getByDeviceId(deviceId)
        return device != null ? composeDeviceInfo(device) : [:]
    }

    def composeDeviceInfo(VisionCamera device) {
        return [
                id:       device.deviceId,
                type:     Constants.VISION_CAMERA,
                title:    device.deviceName,
                name:     device.deviceName + ": " + device.description,
                desc:     device.description,
                uri:      device.uri,
                server:   device.mediaServer,
                detector: device.detectorId,
                group:    device.groupId,
                position: [lat: device.lat, lng: device.lon],
        ]
    }

    def getTrafficVision(int deviceId, Date startTime, Date endTime) {
        return mTrafficVisionRepository.findByDeviceIdAndFlowStatusAndCreatedTimeBetween(deviceId, 1, startTime, endTime)
    }

    def getTrafficVision(int deviceId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<TrafficVision> data = getTrafficVision(deviceId, startTime, endTime)

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

    def getTrafficVisionResults(int deviceId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<TrafficVision> data = mTrafficVisionRepository.findByDeviceIdAndCreatedTimeBetween(deviceId, startTime, endTime)

        def result = []
        data.eachWithIndex { d, idx->
            def item = [:]
            item << [id: idx]
            item << [time: d.createdTime.getTime()]
            item << [item: d.flowStatus]
            item << [speed: d.speed]
            item << [volume: d.volume]
            item << [occupancy: d.occupancy]
            item << [video: d.video]
            item << [car: d.volCar]
            item << [bus: d.volBus]
            item << [truck: d.volTruck]
            item << [motor: d.volMotor]
            result.add(item)
        }

        return result
    }

    def getTrafficVisionVideos(int deviceId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<TrafficVision> data = mTrafficVisionRepository.findByDeviceIdAndCreatedTimeBetween(deviceId, startTime, endTime)

        def result = []
        data.eachWithIndex { d, idx->
            def item = [:]
            item << [time: d.createdTime.getTime()]
            item << [volume: d.volume]
            item << [video: d.video]
            result.add(item)
        }

        return result
    }

    def getTrafficCounts(int deviceId, Date dayTime, int intervalInMs, boolean skipIfAllZero) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)

        long duration = (endTime.getTime() - startTime.getTime())
        int  binSize  = (int)duration/intervalInMs + 1
        if(binSize < 2) {
            return [:]
        }
        // Initialize bins
        int[][] bin = new int[binSize][4]
        for(int i = 0; i < binSize; i++) {
            for (int j = 0; j < 4; j++)
                bin[i][j] = 0
        }

        // Get vision data
        List<TrafficVision> data = getTrafficVision(deviceId, startTime, endTime)
        data.each {
            int  idx = (int)((it.createdTime.getTime() -  startTime.getTime())/intervalInMs)
            if(idx >= 0 && idx < binSize) {
                bin[idx][0] += it.volCar
                bin[idx][1] += it.volTruck
                bin[idx][2] += it.volBus
                bin[idx][3] += it.volMotor
            }
        }

        // Check all zero cases
        if (skipIfAllZero) {
            def allZero = true
            for (int i = 0; i < binSize; i++) {
                if ((bin[i].sum()) > 0) {
                    allZero = false
                    break;
                }
            }
            if (allZero) {
                return [:]
            }
        }

        // Remove the head zero appearances
        int startIdx = 0
        for(int i = 0; i < binSize; i++) {
            if ((bin[i][0] + bin[i][1] + bin[i][2] + bin[i][3]) > 0) {
                startIdx = i;
                break;
            }
        }
        // Remove the tail zero appearances
        int endIdx = binSize - 1
        for(int i = binSize - 1; i > startIdx ; i--) {
            if ((bin[i][0] + bin[i][1] + bin[i][2] + bin[i][3]) > 0) {
                endIdx = i;
                break;
            }
        }

        // Empty bins
        if(endIdx - startIdx <= 0) {
            return [:]
        }

        // Compose actual data
        def car = []
        def truck = []
        def bus = []
        def motor = []
        def start = startTime.getTime()// + (long)(intervalInMs/2)
        for (int i = startIdx; i <= endIdx; i++) {
            Date t = new Date(start + intervalInMs * i * 1L)
            long time = t.getTime()
            car   << [time, bin[i][0]]
            truck << [time, bin[i][1]]
            bus   << [time, bin[i][2]]
            motor << [time, bin[i][3]]
        }

        return [
                car: car,
                truck: truck,
                bus: bus,
                motor: motor
        ]
    }

    def encodeImageDataUrl(byte[] image) {
        if (image == null || image.length == 0) {
            return ''
        }
        byte[] encodedBytes = Base64.encodeBase64(image);
        String data = new String(encodedBytes);
        String url = 'data:image/jpeg;base64,' + data
        return url
    }
}
