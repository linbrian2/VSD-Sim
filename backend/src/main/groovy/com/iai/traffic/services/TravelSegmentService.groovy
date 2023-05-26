package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.LatLonDist
import com.iai.traffic.domain.traffic.TrafficDevice
import com.iai.traffic.domain.traffic.TrafficDeviceAttrs
import com.iai.traffic.repository.traffic.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TravelSegmentService {
    @Inject TrafficDeviceRepository mTrafficDeviceRepository
    @Inject TravelSegmentNewRepository mTravelSegmentNewRepository
    @Inject TrafficCameraRepository mTrafficCameraRepository
    @Inject WeatherStationRepository mWeatherStationRepository
    @Inject TrafficDeviceAttrsRepository mTrafficDeviceAttrsRepository
    @Inject TrafficAnomalyDataService mTrafficAnomalyDataService

    // LinkId to segment data mapping
    def mSegments = [:]

    // LinkId to segment path mapping
    def mPaths = [:]

    // LinkId to segment info mapping
    def mInfos = [:]

    // LinkId to device id mapping
    def mDevices = [:]

    // CameraId to linkId mapping
    def mCameraToLinkIds = [:]

    def mAnomalyDeviceToSegments = [:]

    // CameraId to cameras
    def mCameras = [:]

    @PostConstruct
    def initialize() {
        log.info("TravelSegmentService: loading segments ...")
        loadCameras()
        segmentMapping()
        deviceMapping()
        log.info("TravelSegmentService: finish loading segments ...")
    }

    def loadCameras() {
        if(mCameras.isEmpty()) {
            mTrafficCameraRepository.findAll().each {
                mCameras << [(it.cameraId): it]
            }
        }
    }

    def deviceMapping() {
        ObjectMapper objectMapper = new ObjectMapper()
        mTrafficDeviceRepository.findAll().each { device->
            if (device.linkId != null) {
                // Compose device info
                def info = formDeviceInfo(device)

                // Get linkId list for this device
                def linkList = objectMapper.readValue(device.linkId, new TypeReference<List<String>>(){})
                linkList.unique()

                // Add the linkId to map
                linkList.each {linkId->
                    def deviceIds = mDevices.get(linkId)
                    if (deviceIds == null) {
                        mDevices << [(linkId): [info]]
                    } else {
                        deviceIds << info
                    }
                }
            }
        }

        // Get the mapping from anomaly device to travel time segments
        mTrafficAnomalyDataService.getAllAnomalyDevices().each { it->
            def key = String.format("%d-%s", it.deviceId, it.direction)
            mAnomalyDeviceToSegments << [(key): it.segmentId]
        }
    }

    def segmentMapping() {
        ObjectMapper objectMapper = new ObjectMapper()
        mTravelSegmentNewRepository.findAll().each { segment ->
            def linkId = segment.linkId
            mSegments << [(linkId): segment]

            // Transform coordinates to polyline path
            def coordinates = objectMapper.readValue(segment.coordinates, new TypeReference<List<List<Double>>>() {})
            def path = coordinates.collect { coord ->
                return [lat: coord[1], lng: coord[0]]
            }
            mPaths << [(linkId): path]

            def midPoint = path[(int)Math.round(path.size()/2)]

            // Camera list
            def cameras = []
            if (segment.cameras?.trim()) {
                try {
                    def cameraIds = objectMapper.readValue(segment.cameras, new TypeReference<List<String>>() {})
                    cameraIds.eachWithIndex { cameraId, index ->
                        mCameraToLinkIds << [(cameraId): linkId]
                        def camera = mCameras.get(cameraId)
                        if (camera != null) {
                            cameras << [
                                    id      : cameraId,
                                    name    : cameraId + ' - ' + camera.title,
                                    position: [lat: camera.lat, lng: camera.lon],
                                    seq     : (index + 1)
                            ]
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage())
                }
            }

            def info = [
                    shortName:   segment.shortName,
                    description: segment.description,
                    startPoint:  segment.startPoint,
                    endPoint  :  segment.endPoint,
                    midPoint:    midPoint,
                    distance  :  segment.distance,
                    route :      segment.route,
                    seq:         (segment.routeOrder ?: 100),
                    direction:   segment.direction,
                    stationId :  segment.stationId,
                    cameras   :  cameras
            ]

            mInfos << [(linkId): info]
        }
    }

    def getNearbyCameras(Double latitude, Double longitude, Integer counts) {
        def cameras = []
        mCameras.each {cameraId, camera->
            def dist = LatLonDist.getDistance(latitude, longitude, camera.lat, camera.lon)
            if (dist < 500) {
                def info = [
                        id      : cameraId,
                        name    : cameraId + ' - ' + camera.title,
                        position: [lat: camera.lat, lng: camera.lon],
                ]
                cameras << [distance: dist, camera: info]
            }
        }

        if (cameras.isEmpty()) {
            return []
        }

        // Sort by distance
        cameras.sort { it.distance }

        // Select at most specified counts
        if (counts >= cameras.size()) {
            return cameras.collect { it.camera }
        }

        def selectedCameras = cameras.subList(0, counts)
        return selectedCameras.collect { it.camera }
    }

    def getNearbyCameras(def locationList, Integer counts) {
        def result = [:]
        locationList.each {
            def cameras = getNearbyCameras(it.latitude, it.longitude, counts)
            if (!cameras.isEmpty()) {
                result << [(it.id): cameras]
            }
        }
        return result
    }

    def getCameraPositionByCameraId(String cameraId) {
        if(mCameras.isEmpty()) {
            mTrafficCameraRepository.findAll().each {
                mCameras << [(it.cameraId): it]
            }
        }
        def camera = mCameras.get(cameraId)
        if (camera == null) {
            return null
        }
        return [lat: camera.lat, lng: camera.lon]
    }

    def formDeviceInfo(TrafficDevice device) {
        return [
                id:   device.deviceId,
                uid:  device.uid,
                name: device.deviceId + " - " +  device.uid + " - " + device.title,
                title: device.title,
                type: device.type,
                directions: device.directions,
                position: [lat: device.lat, lng: device.lon],
        ]
    }

    def getSegments() {
        return mSegments
    }

    def getPaths() {
        return mPaths;
    }

    def getInfos() {
        return mInfos;
    }

    def getSegmentByLinkId(String linkId) {
        return mSegments.get(linkId)
    }

    def getPathByLinkId(String linkId) {
        return mPaths.get(linkId)
    }

    def getInfoByLinkId(String linkId) {
        return mInfos.get(linkId)
    }

    def getDevicesByLinkId(String linkId) {
        return mDevices.get(linkId)
    }

    def getLinkIByCameraId(String cameraId) {
        return mCameraToLinkIds.get(cameraId)
    }

    def saveTrafficDeviceAttrs() {
        def devices = []
        mTrafficDeviceRepository.findAll().each { device->
            def items = createTrafficDeviceAttrs(device)
            devices.addAll(items)
        }
        mTrafficDeviceAttrsRepository.saveAll(devices)
    }

    def updateTrafficDeviceAttrsWithWeatherStation() {
        def deviceMap = [:]
        mTrafficDeviceAttrsRepository.findAll().each {
            def deviceId = it.deviceId
            def items = deviceMap.get(deviceId)
            if (items != null) {
                items << it
            } else {
                deviceMap << [(deviceId): [it]]
            }
        }

        // Get weather stations
        def weatherStations = mWeatherStationRepository.findAll()

        // Update
        deviceMap.values().each { items->
            def item = items[0]

            def distances = []
            weatherStations.each { station->
                def dist = LatLonDist.getDistance(item.lat, item.lon, station.lat, station.lon)
                distances << [stationId: station.stationId, dist: dist]
            }

            // Sort by dist
            distances.sort { it.dist }

            // Get the weather station with the minimum distance
            def stationId = distances[0].stationId
            def minDist = distances[0].dist
            println("device=${item.deviceId}, stationid=${stationId}, distance=${minDist}m")

            // Update the devices
            items.each { device->
                device.stationId = stationId
                mTrafficDeviceAttrsRepository.update(device)
            }
        }
    }

    def getSegmentIdByAnomalyDevice(Integer deviceId, String direction) {
        def key = String.format("%d-%s", deviceId, direction)
        return mAnomalyDeviceToSegments.get(key)
    }

    def updateTravelSegment(def segment, def path) {
        def updateNeeded = false

        // Free flow speed
        if (segment.distance == null) {
            // Calculate distance
            def distance = getSegmentDistanceInMeters(path) / 1609.34f
            segment.distance = AppUtils.round2((float)distance)
        }

        if (segment.freeFlowSpeed == null && segment.freeFlowTime != null) {
            segment.freeFlowSpeed = AppUtils.round2((float) (segment.distance * 3600 / segment.freeFlowTime))
            println("link ${segment.linkId}, mph=${segment.freeFlowSpeed}")
        }

        // Camera list
        def cameras = []
        if (segment.cameras == null)  {
            def cameraDistList = mapItemsToDistanceList(path, mTrafficCameraRepository.findAll())
            def sortedList = cameraDistList.sort {it.dist }
            for (int i = 0; i < 5; i++) {
                if (sortedList[i].dist < 100) {
                    cameras << sortedList[i].item.cameraId
                }
            }
            if (!cameras.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper()
                segment.cameras = objectMapper.writeValueAsString(cameras)
                updateNeeded = true
            }
        }

        // Weather station
        if (segment.stationId == null) {
            def stationItem = getClosestItem(path, mWeatherStationRepository.findAll())
            segment.stationId = stationItem.stationId
            println("segmentid=${segment.linkId},stationid=${segment.stationId}")
            updateNeeded = true
        }

        if (updateNeeded) {
            mTravelSegmentNewRepository.update(segment)
        }
    }

    def createTrafficDeviceAttrs(TrafficDevice device) {
        def result = []

        ObjectMapper objectMapper = new ObjectMapper()
        def directions = device.directions ? objectMapper.readValue(device.directions, new TypeReference<List<String>>(){}) : []
        def linkIds =  device.linkId ? objectMapper.readValue(device.linkId, new TypeReference<List<String>>(){}) : []
        def lanes =  device.lanes ? objectMapper.readValue(device.lanes, new TypeReference<List<Integer>>(){}) : []
        def maxVolumeFiveMin =  device.maxVolumeFiveMin ? objectMapper.readValue(device.maxVolumeFiveMin, new TypeReference<List<Integer>>(){}) : []
        def maxVolumeOneHour =  device.maxVolumeOneHour ? objectMapper.readValue(device.maxVolumeOneHour, new TypeReference<List<Integer>>(){}) : []
        def sampleSize =  device.sampleSize ? objectMapper.readValue(device.sampleSize, new TypeReference<List<Integer>>(){}) : []
        def vph =  device.vph ? objectMapper.readValue(device.vph, new TypeReference<List<Integer>>(){}) : []

        def n = directions ? directions.size() : 0
        for (int i = 0; i < n ; i++) {
            def attrs = new TrafficDeviceAttrs()
            attrs.deviceId = device.deviceId
            attrs.uid = device.uid
            attrs.title = device.title
            attrs.county = device.county
            attrs.lat = device.lat
            attrs.lon = device.lon
            attrs.type = device.type
            attrs.stationCount = device.stationCount
            attrs.zone = device.flags
            attrs.direction = directions && i < directions.size() ? directions[i] : null
            attrs.numLane = lanes && i < lanes.size() ? lanes[i] : null
            attrs.maxVolumeFiveMin = maxVolumeFiveMin && i < maxVolumeFiveMin.size() ? maxVolumeFiveMin[i] : null
            attrs.maxVolumeOneHour = maxVolumeOneHour && i < maxVolumeOneHour.size() ? maxVolumeOneHour[i] : null
            attrs.sampleSize = sampleSize && i < sampleSize.size() ? sampleSize[i] : null
            attrs.vph = vph && i < vph.size() ? vph[i] : null
            attrs.linkId = linkIds && linkIds.size() ? linkIds[i] : null

            if (attrs.linkId) {
                def path = getPathByLinkId(attrs.linkId)
                if (path) {
                    def dist = findMinDistance(device.lat, device.lon, path)
                    attrs.minDist = Math.round(dist)
                }
            }

            result << attrs
        }

        return result
    }

    def updateTrafficDeviceLinkId(def device) {
        def lat = device.lat
        def lon = device.lon

        def distances = []
        mPaths.each { linkId, path->
            def dist = findMinDistance(lat, lon, path)
            distances << [id: linkId, dist: dist]
        }

        // Find the linkId with minimum distance
        def item = distances.min { it.dist }
        if (item.dist < 10) {
            log.info("${device.deviceId}-${item.id}-${item.dist}")
            device.linkId = item.id
            mTrafficDeviceRepository.update(device)
        }
    }

    def getClosestItem(def segmentCoordinates, def items) {
        def distList = mapItemsToDistanceList(segmentCoordinates, items)
        def minDistItem = distList.min {it.dist }
        return minDistItem.item
    }

    def mapItemsToDistanceList(def segmentCoordinates, def items) {
        return items.collect {
            def dist = findMinDistance(it.lat, it.lon, segmentCoordinates)
            return [item: it, dist: dist]
        }
    }

    def getSegmentDistanceInMeters(def coordinates) {
        def total = 0
        for (int i = 1; i < coordinates.size(); i++) {
            def lat0 = coordinates[i - 1].lat
            def lon0 = coordinates[i - 1].lng
            def lat1 = coordinates[i + 0].lat
            def lon1 = coordinates[i + 0].lng
            total += LatLonDist.getDistance(lat0, lon0, lat1, lon1)
        }
        return total
    }

    def findMinDistance(double latitude, double longitude, def coordinates) {
        def mind = 10000000
        for (int i = 1; i < coordinates.size(); i++) {
            def lat0 = coordinates[i - 1].lat
            def lon0 = coordinates[i - 1].lng
            def lat1 = coordinates[i + 0].lat
            def lon1 = coordinates[i + 0].lng
            def d = LatLonDist.pointToLineDistance(lat0, lon0, lat1, lon1, latitude, longitude)
            if (d < mind) {
                mind = d
            }
        }
        return mind
    }
}