package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.LatLonDist
import com.iai.traffic.configs.MinioConfiguration
import com.iai.traffic.domain.anomaly.TrafficIncidentResult
import com.iai.traffic.domain.solution.MitigationResult
import com.iai.traffic.domain.traffic.TrafficAlert
import com.iai.traffic.repository.anomaly.IncidentStateRepository
import com.iai.traffic.repository.anomaly.IncidentVideoRepository
import com.iai.traffic.repository.anomaly.TrafficIncidentResultRepository
import com.iai.traffic.repository.solution.MitigationResultRepository
import com.iai.traffic.repository.solution.MitigationSolutionRepository
import com.iai.traffic.repository.traffic.MessageSignRepository
import com.iai.traffic.repository.traffic.TrafficAlertRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method

import javax.inject.Inject
import javax.inject.Singleton
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@Singleton
@Slf4j
class TrafficIncidentDataService {
    @Inject MinioConfiguration mMinioConfiguration
    @Inject TravelSegmentService mTravelSegmentService
    @Inject IncidentVideoRepository mIncidentVideoRepository
    @Inject TrafficAlertRepository mTrafficAlertRepository
    @Inject TrafficIncidentResultRepository mTrafficIncidentResultRepository
    @Inject IncidentStateRepository mIncidentStateRepository
    @Inject MitigationResultRepository mMitigationResultRepository
    @Inject MitigationSolutionRepository mMitigationSolutionRepository
    @Inject MessageSignRepository mMessageSignRepository

    MinioClient  mMinioClient

    def getLatestIncidentData(int secondsAgo) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        // Get all data within time range
        return getIncidentData(startTime, endTime, 0, 0, false, true)
    }

    def getLatestIncidentData(int secondsAgo, Integer severity, Integer duration) {
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        // Get all data within time range
        return getIncidentData(startTime, endTime, severity, duration, false, true)
    }

    def getIncidentData(Date dayTime, boolean includesVideo, Integer severity, Integer duration) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getIncidentData(startTime, endTime, severity, duration, includesVideo, false)
    }

    def getIncidentData(Date startTime, Date endTime, Integer severity, Integer duration, boolean includesVideo,  boolean includePath) {
        def maxCount = 200

        List<TrafficIncidentResult> data = mTrafficIncidentResultRepository.findByDateUpdatedBetweenOrderByDateUpdatedDesc(startTime, endTime)
        if (data.isEmpty()) {
            return []
        }

        if (severity == 0 && duration == 0) {
            return transformData(data, includesVideo, includePath, maxCount)
        } else {
            // Apply filter by severity and duration
            def filteredData = data.findAll { it.severity >= severity && it.duration >= duration }

            // Transform the result
            return transformData(filteredData, includesVideo, includePath, maxCount)
        }
    }

    def searchIncidentData(Date startDate, Date endDate, String route, Integer region,
                           Integer minSeverity, Integer maxSeverity, Integer minDuration, Integer maxDuration,
                           Integer blockage, Integer detourNeeded, Integer mitigation,
                           boolean includesVideo, int maxCount) {

        Date startTime = AppUtils.getStartOfDay(startDate)
        Date endTime   = AppUtils.getEndOfDay(endDate)

        List<TrafficIncidentResult> data = null
        if (route.isEmpty() && region < 0) {
            data = mTrafficIncidentResultRepository.findByDateUpdatedBetweenOrderByDateUpdatedDesc(startTime, endTime)
        } else if (!route.isEmpty() && region < 0) {
            data = mTrafficIncidentResultRepository.findByDateUpdatedBetweenAndRouteOrderByDateUpdatedDesc(startTime, endTime, route)
        } else if (route.isEmpty() && region > 0) {
            data = mTrafficIncidentResultRepository.findByDateUpdatedBetweenAndRegionOrderByDateUpdatedDesc(startTime, endTime, region)
        } else {
            data = mTrafficIncidentResultRepository.findByDateUpdatedBetweenAndRouteAndRegionOrderByDateUpdatedDesc(startTime, endTime, route, region)
        }

        if (data.isEmpty()) {
            return []
        }

        if (minSeverity < 0 && maxSeverity < 0 && minDuration < 0 && maxDuration < 0 && blockage < 0 && detourNeeded < 0 && mitigation < 0) {
            return transformData(data, includesVideo, false, maxCount)
        } else {
            // Apply filter by severity and duration
            def minS = (minSeverity == null || minSeverity < 0) ? 0 : minSeverity
            def maxS = (maxSeverity == null || minSeverity < 0 || maxSeverity >= 100) ? 100 : maxSeverity
            def minD = (minDuration == null || minDuration < 0) ? 0 : minDuration
            def maxD = (maxDuration == null || maxDuration < 0) ? 100000 : maxDuration

            def filteredData = data.findAll {
                it.severity >= minS && it.severity <= maxS && it.duration >= minD && it.duration <= maxD
            }

            if (blockage != -1) {
                filteredData = filteredData.findAll {getIncidentBlockageStatus(it.id) == blockage }
            }

            if (detourNeeded != -1) {
                filteredData = filteredData.findAll {getIncidentDetourStatus(it.id) == detourNeeded }
            }

            if (mitigation != -1) {
                filteredData = filteredData.findAll {getIncidentMitigationStatus(it.id) == mitigation }
            }

            // Transform the result
            return transformData(filteredData, includesVideo, false, maxCount)
        }
    }

    def getIncidentData(Long incidentId, boolean includesVideo, boolean includeSegmentPath) {
        Optional<TrafficIncidentResult> opt = mTrafficIncidentResultRepository.findById(incidentId)
        if (!opt.isPresent()) {
            return [:]
        }
        TrafficIncidentResult incident = opt.get()
        return composeIncident(incident, includesVideo, includeSegmentPath)
    }

    def transformData(List<TrafficIncidentResult> data, boolean includesVideo, boolean includeSegmentPath, int limit) {
        // Compose result
        def result =[]
        for (int i = 0; i < Math.min(data.size(), limit); i++) {
            TrafficIncidentResult incident = data.get(i)
            def linkId = incident.segmentId
            def info = mTravelSegmentService.getInfoByLinkId(linkId)
            if (info != null) {
                result << composeIncident(incident, includesVideo, includeSegmentPath)
            }
        }

        // Sort by start time
        result.sort { it.startTime }

        return result
    }

    def composeIncident(TrafficIncidentResult incident, boolean includesVideo, boolean includeSegmentPath) {
        def linkId = incident.segmentId
        def info = mTravelSegmentService.getInfoByLinkId(linkId)

        ObjectMapper objectMapper = new ObjectMapper()
        def segments = getSegmentList(objectMapper, incident.segments)

        def flagBy = []
        def items = []
        def evidenceCounts = [:]
        if (incident.trafficFlow) {
            def entries = createItems(objectMapper, incident.trafficFlow)
            evidenceCounts << [flow: entries.size()]
            items.addAll(entries)
            flagBy << 'Flow'
        }
        if (incident.travelTime) {
            def entries = createItems(objectMapper, incident.travelTime)
            evidenceCounts << [bluetooth: entries.size()]
            items.addAll(entries)
            flagBy << 'Bluetooth'
        }
        if (incident.waze) {
            def entries = createItems(objectMapper, incident.waze)
            evidenceCounts << [waze: entries.size()]
            items.addAll(entries)
        }

        if (incident.restriction) {
            def entries = createItems(objectMapper, incident.restriction)
            evidenceCounts << [restriction: entries.size()]
            items.addAll(entries)
        }

        if (incident.alerts) {
            def alerts = createAlerts(objectMapper, incident.alerts, (segments.segments?:[linkId])).collect {
                return [type: Constants.DATA_TRAFFIC_ALERT, data: it]
            }
            evidenceCounts << [alert: alerts.size()]
            items.addAll(alerts)
        }

        if (incident.weather) {
            items << createItem(objectMapper, Constants.DATA_WEATHER, incident.weather)
        }

        // Obtain video clips related to this incident
        if (includesVideo) {
            def videos = getIncidentVideo(incident.id).collect {
                return  [type: Constants.DATA_TRAFFIC_VIDEO, data: it]
            }
            items.addAll(videos)
        }

        // Obtain nearby VMSs
        def vms = getNearbyVMSs(incident.lat, incident.lon, 2)

        // Include mitigation solution
        def count = mMitigationSolutionRepository.countByIncidentId(incident.id)
        def mitigation = count > 0 ? true : false

        def item = [:]
        item << [id: incident.id]
        item << [linkId: linkId]
        item << [shortName: info.shortName]
        item << [segments: segments.segments]
        item << [shortNames: segments.shortNames]
        item << [segmentCount: segments.segments.size()]
        item << [direction: (incident.direction ?: info.direction)]
        item << [route: incident.route]
        item << [region: getRegionName(incident.region)]
        item << [status: incident.status]
        item << [type: getIncidentType(incident.incidentType)]
        item << [reason: incident.explanation]
        item << [dateCreated: incident.dateCreated]
        item << [lastUpdated: incident.dateUpdated]
        item << [startTime: incident.startTime]
        item << [endTime: incident.endTime]
        item << [duration: incident.duration]
        item << [location: [lat: incident.lat, lng: incident.lon]]
        item << [info: info]
        item << [timeline: getTimeline(objectMapper, incident, mitigation)]
        item << [items: items]
        item << [flagBy: flagBy.join(', ')]
        item << [severity: incident.severity]
        item << [severityColor: getSeverityLevel(incident.severity)]
        item << [evidenceCount: incident.evidenceCount]
        item << [evidenceCounts: evidenceCounts]
        item << [evidences: getEvidences(incident)]
        item << [mitigation: mitigation]

        if (!vms.isEmpty()) {
            item << [vms: vms]
        }

        if (incident.address) {
            item << [address: incident.address]
        }

        if (incident.detectors) {
            item << [detectors: getDetectors(objectMapper, incident.detectors)]
        }

        if (includeSegmentPath) {
            def path = mTravelSegmentService.getPathByLinkId(linkId)
            item << [ path: path]
        }

        // Only update state when the incident is ongoing
        if (incident.status == 0) {
            def state = getIncidentState(incident.id, objectMapper)
            if (!state.isEmpty()) {
                item << [incidentState: state]
            }
        }

        return item
    }

    def getEvidences(TrafficIncidentResult incident) {
        def result = []

        if (incident.trafficFlow != null) {
            result << Constants.DATA_TRAFFIC_FLOW
        }
        if (incident.travelTime != null) {
            result << Constants.DATA_TRAVEL_TIME
        }
        if (incident.waze != null) {
            result << Constants.DATA_WAZE_ALERTS
        }
        if (incident.restriction != null) {
            result << Constants.DATA_RESTRICTION
        }

        return result
    }

    def getTimeline(ObjectMapper objectMapper, TrafficIncidentResult incident, boolean mitigation) {
        def result = []

        def startTime = AppUtils.addSeconds(incident.startTime, -300)
        def endTime = AppUtils.addSeconds(incident.endTime, 305)

        result << [color: 'indigo', icon: 'mdi-play', time: startTime, event: 'Incident start' ]

        if (incident.status > 0) {
            result << [color: 'blue', icon: 'mdi-stop', time: endTime, event: 'Incident end']
        }

        if (incident.trafficFlow) {
            def items = createItems(objectMapper, incident.trafficFlow)
            def t = getStartAndEndTime(items)
            result << [color: 'blue-grey', icon: 'mdi-car-multiple', time: t.startTime, event: 'M-dist anomaly detected' ]
            if (incident.status > 0) {
                result << [color: 'green', icon: 'mdi-car-multiple', time: t.endTime, event: 'M-dist anomaly cleared']
            }

        }
        if (incident.travelTime) {
            def items = createItems(objectMapper, incident.travelTime)
            def t = getStartAndEndTime(items)
            result << [color: 'blue-grey', icon: 'mdi-bluetooth-connect', time: t.startTime, event: 'Travel time anomaly detected' ]
            if (incident.status > 0) {
                result << [color: 'green', icon: 'mdi-bluetooth-connect', time: t.endTime, event: 'Travel time anomaly cleared']
            }
        }
        if (incident.waze) {
            def allItems = []
            def items = createItems(objectMapper, incident.waze)
            items.each { item->
                def details = createItems(objectMapper, item.data.details)
                details.each { detail->
                    allItems << [type: Constants.DATA_WAZE_ALERTS, data: detail ]
                }
            }
            def t = getStartAndEndTime(allItems)
            result << [color: 'blue-grey', icon: 'mdi-waze', time: t.startTime, event: 'First waze alert received' ]
            if (incident.status > 0) {
                result << [color: 'green', icon: 'mdi-waze', time: t.endTime, event: 'Last waze alert received']
            }
        }

        // Include mitigation solution result
        if (mitigation) {
            List<MitigationResult> mitigationResult = mMitigationResultRepository.findByIncidentId(incident.id)
            if (!mitigationResult.isEmpty()) {
                def onRampBands = createBands(mitigationResult.collect { [time: it.time, flag: it.onRampFlag] })
                def offRampBands = createBands(mitigationResult.collect { [time: it.time, flag: it.offRampFlag] })
                def travelTimeBands = createBands(mitigationResult.collect { [time: it.time, flag: it.travelTimeFlag] })
                def signalChangeBands = createBands(mitigationResult.collect { [time: it.time, flag: it.signalChangeState > 0] })

                if (!onRampBands.isEmpty()) {
                    result << [color: 'blue-grey', icon: 'mdi-arrow-top-right-thin', time: onRampBands.first()[0], event: 'On-ramp volume exceeds normal threshold']
                    result << [color: 'green', icon: 'mdi-arrow-top-right-thin', time: onRampBands.last()[1], event: 'On-ramp volume goes to normal']
                }

                if (!offRampBands.isEmpty()) {
                    result << [color: 'blue-grey', icon: 'mdi-arrow-bottom-right-thin', time: offRampBands.first()[0], event: 'Off-ramp volume exceeds normal threshold']
                    result << [color: 'green', icon: 'mdi-arrow-bottom-right-thin', time: offRampBands.last()[1], event: 'Off-ramp volume goes to normal']
                }

                if (!travelTimeBands.isEmpty()) {
                    result << [color: 'blue-grey', icon: 'mdi-clock-outline', time: travelTimeBands.first()[0], event: 'Arterial travel time exceeds normal threshold']
                    result << [color: 'green', icon: 'mdi-clock-outline', time: travelTimeBands.last()[1], event: 'Arterial travel time  goes to normal']
                }

                if (!signalChangeBands.isEmpty()) {
                    result << [color: 'blue-grey', icon: 'mdi-traffic-light', time: signalChangeBands.first()[0], event: 'Signal timing plan change started']
                    result << [color: 'green', icon: 'mdi-traffic-light', time: signalChangeBands.last()[1], event: 'Signal timing plan change ended']
                }
            }
        }

        // Sort the timeline by time
        result.sort { it.time }

        // Modify the time property
        result.each { it.time = formatTime(it.time) }

        return result
    }

    def createBands(def flags) {
        def bands = []

        def startTime = null
        def prevTime = null
        flags.each {
            if (it.flag) {
                if (startTime == null) {
                    startTime = it.time
                }
                prevTime = it.time
            } else {
                if (startTime != null) {
                    if (startTime != prevTime) {
                        bands << [startTime, prevTime]
                    }
                    startTime = null
                }
            }
        }

        return bands
    }

    def getStartAndEndTime(def items) {
        if (items.isEmpty()) {
            return null
        }
        items.sort { it.data.time }
        def startTime = new Date(items.first().data.time)
        def endTime = AppUtils.addSeconds(new Date(items.last().data.time), 300)
        return [startTime: startTime, endTime: endTime]
    }

    def createItems(ObjectMapper objectMapper, String data) {
        return objectMapper.readValue(data, List.class)
    }

    def createItem(ObjectMapper objectMapper, int type, String data) {
        def json = objectMapper.readValue(data, HashMap.class)
        return [type: type, data: json]
    }

    def createAlerts(ObjectMapper objectMapper, String alerts, List<String> linkIds) {
        def alertIds =  objectMapper.readValue(alerts, new TypeReference<List<Long>>(){})
        return alertIds.collect {alertId->
            return getTrafficAlert(alertId, linkIds)
        }
    }

    def getSegmentList(ObjectMapper objectMapper, String segmentsStr) {
        def segments = objectMapper.readValue(segmentsStr, new TypeReference<List<String>>(){})
        def shortNames = segments.collect {linkId->
            return mTravelSegmentService.getInfoByLinkId(linkId)?.shortName
        }
        return [segments: segments, shortNames: shortNames]
    }

    def getTrafficAlert(Long alertId, List<String> linkIds) {
        def result = [:]
        Optional<TrafficAlert> alertOpt = mTrafficAlertRepository.findById(alertId)
        if (alertOpt.isPresent()) {
            def alert = alertOpt.get()

            result << [id: alert.id]
            result << [updatedAt: alert.receivedAt]
            result << [subject: alert.subject]
            result << [type: alert.type]
            result << [position:[lat: alert.latitude, lng: alert.longitude]]
            result << [details: alert.details]
            result << [effect: alert.effect]
            result << [impact: alert.impact]
            result << [status: alert.status]

            if (!linkIds.isEmpty()) {
                def point = getClosestSegment(alert.latitude, alert.longitude, linkIds)
                result << [linkId: point.linkId]
                result << [dist: Math.round(point.dist)]
            }
        }
        return result
    }

    def getClosestSegment(double latitude, double longitude, List<String> linkIds) {
        def result = []
        linkIds.each {linkId->
            def path = mTravelSegmentService.getPathByLinkId(linkId)
            def dist = mTravelSegmentService.findMinDistance(latitude, longitude, path)
            result << [linkId: linkId, dist: dist]
        }

        // Sort by distance
        result.sort { it.dist }

        return result[0]
    }

    def getIncidentState(Long incidentId, ObjectMapper objectMapper) {
        def result = [:]

        def incidentStates = mIncidentStateRepository.findByIncidentId(incidentId)
        if (!incidentStates.isEmpty()) {
            // Get the latest updates (within 15 minutes it was created)
            def currentTime = new Date()
            def currentStates = incidentStates.findAll {AppUtils.getDurationInSeconds(it.dateCreated ,currentTime) < 15 * 60 }
            if (!currentStates.isEmpty()) {
                // Get the item with maximum capacity
                def state = currentStates.max {it.capacityReduction }

                result << [time: state.time]
                result << [deviceId: state.deviceId]
                result << [direction: state.direction]
                result << [capacityReduction: state.capacityReduction]
                result << [detour: state.detour]
                result << [timingPlan: state.timingPlanChange]

                if (state.laneBlockage) {
                    def item = objectMapper.readValue(state.laneBlockage, HashMap.class)
                    result << [laneBlockage: item]
                }
            }
        }

        return result
    }

    def getIncidentBlockageStatus(Long incidentId) {
        def incidentStates = mIncidentStateRepository.findByIncidentId(incidentId)
        if (incidentStates.isEmpty()) {
            return 0
        }
        def states = incidentStates.findAll {it.laneBlockage != null }
        return states.size() > 3 ? 1 : 0
    }

    def getIncidentDetourStatus(Long incidentId) {
        def incidentStates = mIncidentStateRepository.findByIncidentId(incidentId)
        if (incidentStates.isEmpty()) {
            return 0
        }
        def states = incidentStates.findAll {it.detour > 0 }
        return states.size() > 3 ? 1 : 0
    }

    def getIncidentMitigationStatus(Long incidentId) {
        def count = mMitigationSolutionRepository.countByIncidentId(incidentId)
        return count > 0 ? 1 : 0
    }

    def getIncidentVideo(Long incidentId) {
        def videos = mIncidentVideoRepository.findByIncidentId(incidentId)
        return videos.collect {
            def imageUrl = generateUrl(it.bucketName, it.imageName)
            def videoUrl = generateUrl(it.bucketName, it.videoName)
            def position = mTravelSegmentService.getCameraPositionByCameraId(it.cameraId)
            def linkId   = mTravelSegmentService.getLinkIByCameraId(it.cameraId)
            return [time: it.videoTime, camera: it.cameraId, position: position, linkId: linkId, image: imageUrl, video: videoUrl, classification: it.classification]
        }
    }

    def getNearbyVMSs(Double latitude, Double longitude, Integer counts) {
        def VMSs = []
        mMessageSignRepository.findByType(0).each {
            def dist = LatLonDist.getDistance(latitude, longitude, it.lat, it.lon)
            if (dist < 6000) {
                def info = [
                        id         : it.systemId,
                        name       : it.title,
                        message    : it.message,
                        lastUpdated: it.lastUpdated.getTime(),
                        position   : [lat: it.lat, lng: it.lon],
                ]
                VMSs << [distance: dist, data: info]
            }
        }

        if (VMSs.isEmpty()) {
            return []
        }

        // Sort by distance
        VMSs.sort { it.distance }

        return counts >= VMSs.size() ? VMSs.collect { it.data } : VMSs.subList(0, counts).collect { it.data }
    }

    def getDetectors(ObjectMapper objectMapper, String detectors) {
        return objectMapper.readValue(detectors, new TypeReference<List<Integer>>(){})
    }

    def generateUrl(String bucketName, String objectName) {
        def url = null
        def client = getMinioClient()
        if (client) {
            url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                                                .method(Method.GET)
                                                .bucket(bucketName)
                                                .object(objectName)
                                                .expiry(2, TimeUnit.HOURS)
                                                .build())

        }
        return url
    }

    def getMinioClient() {
        if (!mMinioClient) {
            def accessKey = mMinioConfiguration.accessKey
            def secretKey = mMinioConfiguration.secretKey
            def endpoint  = mMinioConfiguration.endpoint
            mMinioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()
        }
        return mMinioClient
    }

    def getRegionName(int region) {
        def result = 'Unknown'
        switch(region) {
            case 1:
                result = 'Urban'
                break
            case 2:
                result = 'Urban System'
                break
            case 3:
                result = 'CAV'
                break
            case 4:
                result = 'CAV System'
                break
            case 5:
                result = 'Rural'
                break
            case 6:
                result = 'Rural System'
                break
            case 7:
                result = 'Outside'
                break
        }
        return result
    }

    def getIncidentType(int type) {
        def result = 'Unknown'
        switch(type) {
            case 1:
                result = 'Incident'
                break
            case 2:
                result = 'Work Zone'
                break
            case 3:
                result = 'Severe Weather'
                break
            case 4:
                result = 'Congestion'
                break
        }
        return result
    }

    def getSeverityLevel(def score) {
        def colors = ['#339900', '#00ff33', '#ffe338', '#ffc031', '#fa7a28', '#B71C1C', '#c00000']

        int level = 0
        if (score <= 30) level = 0
        else if (score <= 45) level = 1
        else if (score <= 55) level = 2
        else if (score <= 65) level = 3
        else if (score <= 75) level = 4
        else if (score <= 85) level = 5
        else level = 6

        return colors[level]
    }

    def formatTime(Date time) {
        def df = new SimpleDateFormat("hh:mm aa")
        return df.format(time)
    }
}
