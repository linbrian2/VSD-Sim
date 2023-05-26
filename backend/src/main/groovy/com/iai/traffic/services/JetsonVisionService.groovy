package com.iai.traffic.services

import com.iai.traffic.domain.vision.TrafficVision
import com.iai.traffic.domain.vision.TrafficCamera
import com.iai.traffic.repository.vision.JetsonVisionRepository
import com.iai.traffic.repository.vision.JetsonCameraRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class JetsonVisionService {
    @Inject JetsonCameraRepository mVisionCameraRepository
    @Inject JetsonVisionRepository mTrafficVisionRepository

    def getAllDevices() {
        List<TrafficCamera> devices = mVisionCameraRepository.findAll()
        return devices.collect {
            return composeDeviceInfo(it)
        }
    }

    def getAllLiveDevices() {
        def ids = getLiveDevices()
        def result = []
        if (!ids.isEmpty()) {
            ids.each {
                Optional<TrafficCamera> d = mVisionCameraRepository.findById(Long.valueOf(it))
                if (d.isPresent()) {
                    result << composeDeviceInfo(d.get())
                }
            }
        }
        return result
    }

    def getDeviceInfo(Long id) {
        Optional<TrafficCamera> d = mVisionCameraRepository.findById(id)
        return d.isPresent() ? composeDeviceInfo(d.get()) : [:]
    }

    def composeDeviceInfo(TrafficCamera device) {
        def code = device.machineId.substring(8)
        def host = "https://video.aitoms.net/c${code}"
        def host1 = "http://${device.ipAddr}:${device.port}"

        def uri = "${host}/live${device.idx}/${device.serialNo}.m3u8"

        def server = "${host}/media/"
        def desc = device.description ?: ('SerialNo ' + device.serialNo)
        return [
                id:   device.id,
                type: Constants.JETSON_CAMERA,
                title: device.name,
                name: device.name + ": " + desc,
                desc: desc,
                uri:  uri.toString(),
                server: server.toString(),
                detector: device.detectorId,
                group: device.groupId,
                position: [lat: device.lat, lng: device.lon]
        ]
    }

    def isCameraLive(int deviceId) {
        Date endTime = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, 300)
        List<TrafficVision> data = mTrafficVisionRepository.findByDateCreatedBetween(startTime, endTime)
        if (data.isEmpty()) {
            return false
        }

        def item = data.find {it.deviceId == deviceId }
        return (item != null)
    }

    def getLiveDevices() {
        Date endTime = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, 1800)
        List<TrafficVision> data = mTrafficVisionRepository.findByDateCreatedBetween(startTime, endTime)
        if (data.isEmpty()) {
            return []
        }

        def deviceIds = data.collect {it.deviceId }
        deviceIds.unique()

        return deviceIds
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
        List<TrafficVision> data = getTrafficVision(deviceId, startTime, endTime)

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
            item << [coupe: d.volCoupe]
            item << [sedan: d.volSedan]
            item << [large: d.volLarge]
            item << [suv: d.volSuv]
            item << [truck: d.volTruck]
            item << [van: d.volVan]

            result.add(item)
        }

        return result
    }

    def getTrafficVisionVideos(int deviceId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        List<TrafficVision> data = getTrafficVision(deviceId, startTime, endTime)

        def result = []
        data.eachWithIndex { d, idx->
            def dateDir = AppUtils.formatDateToDay(d.createdTime) + '/'
            def item = [:]
            item << [time: d.createdTime.getTime()]
            item << [volume: d.volume]
            item << [video: dateDir + d.video]
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
        int classes = 6
        int[][] bin = new int[binSize][classes]
        for(int i = 0; i < binSize; i++) {
            for (int j = 0; j < classes; j++)
                bin[i][j] = 0
        }

        // Get vision data
        List<TrafficVision> data = getTrafficVision(deviceId, startTime, endTime)
        data.each {
            int  idx = (int)((it.createdTime.getTime() -  startTime.getTime())/intervalInMs)
            if(idx >= 0 && idx < binSize) {
                bin[idx][0] += it.volCoupe
                bin[idx][1] += it.volSedan
                bin[idx][2] += it.volLarge
                bin[idx][3] += it.volSuv
                bin[idx][4] += it.volTruck
                bin[idx][5] += it.volVan
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
            if ((bin[i].sum()) > 0) {
                startIdx = i
                break;
            }
        }
        // Remove the tail zero appearances
        int endIdx = binSize - 1
        for(int i = binSize - 1; i > startIdx ; i--) {
            if ((bin[i].sum()) > 0) {
                endIdx = i;
                break;
            }
        }

        // Empty bins
        if(endIdx - startIdx <= 0) {
            return [:]
        }

        // Compose actual data
        def coupe = []
        def sedan = []
        def large = []
        def suv = []
        def truck = []
        def van = []

        def start = startTime.getTime()// + (long)(intervalInMs/2)
        for (int i = startIdx; i <= endIdx; i++) {
            Date t = new Date(start + intervalInMs * i * 1L)
            long time = t.getTime()
            coupe << [time, bin[i][0]]
            sedan << [time, bin[i][1]]
            large << [time, bin[i][2]]
            suv   << [time, bin[i][3]]
            truck << [time, bin[i][4]]
            van   << [time, bin[i][5]]
        }

        return [
                coupe: coupe,
                sedan: sedan,
                large: large,
                suv: suv,
                truck: truck,
                van: van
        ]
    }
}
