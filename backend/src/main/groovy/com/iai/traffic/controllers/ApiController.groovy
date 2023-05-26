package com.iai.traffic.controllers

import com.iai.traffic.AnomalyWebSocket
import com.iai.traffic.configs.AppConfiguration
import com.iai.traffic.services.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.security.annotation.Secured
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;

import javax.inject.Inject

@Controller("/api")
// @Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class ApiController {
    @Inject AppConfiguration config
    @Inject DatabaseService mDatabaseService
    @Inject TrafficFlowDataService mTrafficFlowDataService
    @Inject TrafficAnomalyDataService mTrafficAnomalyDataService
    @Inject AnotherTrafficAnomalyDataService mAnotherTrafficAnomalyDataService
    @Inject RestrictionDataService mRestrictionDataService
    @Inject TrafficIncidentDataService mTrafficIncidentDataService
    @Inject AnomalyThresholdService mAnomalyThresholdService
    @Inject MitigationSolutionService mMitigationSolutionService
    @Inject TrafficZoneDataService mTrafficZoneDataService
    @Inject TrafficSignalDeviceService mTrafficSignalDeviceService
    @Inject TrafficFlowQualityService mTrafficFlowQualityService
    @Inject SimulationService mSimulationService
    @Inject TravelSegmentService mTravelSegmentService
    @Inject AnomalyWebSocket mAnomalyWebSocket

    @Get("/ping")
    @Secured(IS_ANONYMOUS)
    def ping() {
        def message = [
                status: "OK",
                message: "Backend database service available",
                version: config.getVersion(),
                service: config.getName(),
                description: config.getDescription()
        ]
        return message
    }

    @Get("releaseNotes")
    def getReleaseNotes(@QueryValue String version) {
        // Query database
        try {
            def json = mDatabaseService.getReleaseNotes(version)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("trafficDevices")
    def trafficDevices() {
        return mTrafficFlowDataService.getTrafficDevices(true)
    }

    @Get("trafficCameras")
    def trafficCameras() {
        return mTrafficFlowDataService.getTrafficCameras()
    }

    @Get("trafficPerLaneDevices")
    def trafficPerLaneDevices() {
        return mTrafficFlowDataService.getTrafficPerLaneDevices()
    }

    @Get("trafficPerMinuteDevices")
    def trafficPerMinuteDevices() {
        return mTrafficFlowDataService.getTrafficPerMinuteDevices()
    }

    @Get("anomalyDevices")
    def anomalyDevices() {
        return mTrafficAnomalyDataService.getAnomalyDevices()
    }

    @Get("trafficDevice")
    def trafficDevice(@QueryValue Integer id) {
        // Query database
        try {
            def json = mTrafficFlowDataService.getTrafficDeviceInfo(id)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestDeviceInfo")
    def latestDeviceInfo(@QueryValue Integer id) {
        // Query database
        try {
            def json = mTrafficAnomalyDataService.getLatestDeviceInfo(id)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("nearbyCameras")
    def nearbyCameras(@Body def body) {
        // Validate parameters
        if (body.locations == null && (body.lat == null || body.lng == null)) {
            return [
                    status: 'ERROR',
                    message: 'No {lat, lng} or {locations} was found'
            ]
        }

        // Query database
        try {
            def json = null
            Integer  counts = body.counts != null ? body.counts.intValue() : 3
            if (body.locations != null) {
                def locations = body.locations.collect {
                    def id = it.id.intValue()
                    def latitude  = it.lat.doubleValue()
                    def longitude = it.lng.doubleValue()
                    return [id: id, latitude:latitude, longitude:longitude]
                }
                json = mTravelSegmentService.getNearbyCameras(locations, counts)
            } else {
                Double   latitude  = body.lat.doubleValue()
                Double   longitude = body.lng.doubleValue()
                json = mTravelSegmentService.getNearbyCameras(latitude, longitude, counts)
            }

            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("combinedAnomalyResult")
    def combinedAnomalyResult(@Body def body) {
        // Validate parameters
        if (body.deviceId == null || body.direction == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No {deviceId, direction, start} was found'
            ]
        }

        // Query database
        try {
            Integer deviceId = body.deviceId.intValue()
            String  direction = body.direction.textValue()
            Date    dayTime = new Date(body.start.longValue())
            def json = mAnotherTrafficAnomalyDataService.getAnomalyResultsHour(deviceId, direction, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("anomalyResult")
    def anomalyResult(@Body def body) {
        // Validate parameters
        if (body.start == null || body.route == null || body.direction == null) {
            return [
                    status: 'ERROR',
                    message: 'No {route, direction, start} was found'
            ]
        }

        // Query database
        try {
            String route = body.route.textValue()
            String direction = body.direction.textValue()
            Date   dayTime = new Date(body.start.longValue())
            def json = mTrafficAnomalyDataService.getAnomalyResults5Min(route, direction, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("anomalyTimeList")
    def anomalyTimeList(@Body def body) {
        // Validate parameters
        if (body.start == null || (body.deviceId == null && body.devices == null) ) {
            return [
                    status: 'ERROR',
                    message: 'No {deviceId|devices, direction, start} was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())

            def json = null
            if (body.deviceId != null) {
                Integer deviceId = body.deviceId.intValue()
                String direction = body.direction.textValue()
                json = mTrafficAnomalyDataService.getAnomalyTimeData(deviceId, direction, dayTime)
            } else if (body.devices != null) {
                def  devices = body.devices.collect { it.textValue() }
                json = mTrafficAnomalyDataService.getAnomalyTimeDataList(devices, dayTime)
            }

            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("anomalyHourlyResult")
    def anomalyHourlyResult(@Body def body) {
        // Validate parameters
        if (body.start == null || body.deviceId == null || body.direction == null || body.hour == null) {
            return [
                    status: 'ERROR',
                    message: 'No {deviceId, direction, start, hour} was found'
            ]
        }

        // Query database
        try {
            Integer deviceId = body.deviceId.intValue()
            Integer hourIdx  = body.hour.intValue()
            String direction = body.direction.textValue()
            Date   dayTime = new Date(body.start.longValue())
            def json = mTrafficAnomalyDataService.getAnomalyResults(deviceId, direction, hourIdx, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowBaseline")
    def trafficFlowBaseline(@Body def body) {
        // Validate parameters
        if (body.id == null || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (body.endDate != null) {
                endTime = AppUtils.getEndOfDay(body.endDate.longValue())
            }

            String direction = (body.direction != null) ? (body.direction.textValue() ?: null) : null
            def intervalInSecs = (int)(interval/1000)

            // Query database
            def json = mTrafficFlowDataService.getTrafficFlowBaselineData(deviceId, direction, intervalInSecs, startTime, endTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlow")
    def trafficFlow(@Body def body) {
        // Validate parameters
        if (body.id == null || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (body.endDate != null) {
                endTime = AppUtils.getEndOfDay(body.endDate.longValue())
            }
            def computeZoneVO = true//body.zoneVO ? body.zoneVO.booleanValue() : true

            String direction = (body.direction != null) ? (body.direction.textValue() ?: null) : null
            def intervalInSecs = (int)(interval/1000)

            // Query database
            def json = mTrafficFlowDataService.getTrafficFlowData(deviceId, direction, intervalInSecs, startTime, endTime, computeZoneVO)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowPerLane")
    def trafficFlowPerLane(@Body def body) {
        // Validate parameters
        if (body.id == null || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (body.endDate != null) {
                endTime = AppUtils.getEndOfDay(body.endDate.longValue())
            }
            def intervalInSecs = (int)(interval/1000)
            String direction = (body.direction != null) ? (body.direction.textValue() ?: null) : null
            def permitNumber = (body.uid != null) ? body.uid.textValue() : null

            if (!permitNumber) {
                permitNumber = mTrafficFlowDataService.getPermitNumberByDeviceId(deviceId)
            }

            // Query database
            def json = mTrafficFlowDataService.getTrafficFlowPerLaneData(permitNumber, direction, intervalInSecs, startTime, endTime, true)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowPerMinute")
    def trafficFlowPerMinute(@Body def body) {
        // Validate parameters
        if (body.id == null || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (body.endDate != null) {
                endTime = AppUtils.getEndOfDay(body.endDate.longValue())
            }
            def intervalInSecs = (int)(interval/1000)
            String direction = (body.direction != null) ? (body.direction.textValue() ?: null) : null

            // Query database
            //long t0 = System.currentTimeMillis();
            def json = mTrafficFlowDataService.getTrafficFlowPerMinuteData(deviceId, direction, intervalInSecs, startTime, endTime)

            //System.out.println("Per minute took " + (System.currentTimeMillis() - t0) + " milliseconds");
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowAndBaseline")
    def trafficFlowAndBaseline(@Body def body) {
        // Validate parameters
        if (body.id == null || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (body.endDate != null) {
                endTime = AppUtils.getEndOfDay(body.endDate.longValue())
            }

            String direction = (body.direction != null) ? body.direction.textValue() : null
            def intervalInSecs = (int)(interval/1000)

            // Query database
            def json = mTrafficFlowDataService.getTrafficFlowAndBaseline(deviceId, direction, intervalInSecs, startTime, endTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficVolumeAndBaseline")
    def trafficVolumeAndBaseline(@Body def body) {
        // Validate parameters
        if ((body.id == null && body.ids == null) || body.interval == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id | ids, interval, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def interval = body.interval.intValue()
            def intervalInSecs = (int)(interval/1000)
            Date dayTime = new Date(body.startDate.longValue())

            // Query database
            def json = null
            if (body.id != null) {
                def deviceId = body.id.intValue()
                String direction = (body.direction != null) ? body.direction.textValue() : null
                json = mTrafficFlowDataService.getTrafficVolumeAndBaseline(deviceId, direction, intervalInSecs, dayTime)
            } else if (body.ids != null) {
                def  devices = body.ids.collect { it.textValue() }
                json = mTrafficFlowDataService.getTrafficVolumeAndBaseline(devices, intervalInSecs, dayTime)
            }

            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("fetchData")
    def fetchData(@Body def body) {
        println "fetchData"
        // Validate parameters
        if (body.path == null) {
            return [
                    status: 'ERROR',
                    message: 'No {path} was found'
            ]
        }

        // Query database
        try {
            String path = body.path != null ? body.path.textValue() : ''
            def json = mSimulationService.getFileData(path)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("setSimulationData")
    def setSimulationData(@Body def body) {
        println 'setSimulationData'
        // Validate parameters
        if (body.path == null) {
            return [
                    status: 'ERROR',
                    message: 'No {path} was found'
            ]
        }

        // Query database
        try {
            String path = body.path != null ? body.path.textValue() : ''
            println path
            def json = mSimulationService.setSimulationData(path)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("sensorQualityReport")
    def sensorQualityReport(@Body def body) {
        // Validate parameters
        if (body.deviceId == null || body.start == null ) {
            return [
                    status: 'ERROR',
                    message: 'No {deviceId | start} was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())
            Integer deviceId = body.deviceId.intValue()
            String direction = body.direction != null ? body.direction.textValue() : ''
            def json = mTrafficFlowQualityService.getSensorErrorCountPercentage(deviceId, direction, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("flowPrediction")
    def flowPrediction(@Body def body) {
        // Extract the parameters
        def detectorId = body.id
        def dir        = body.direction
        def start      = body.start
        def method     = body.method

        // Validate parameters
        if (detectorId == null || dir == null || start == null) {
            return [
                    status: 'ERROR',
                    message: 'No {id, direction, start} was found'
            ]
        }

        // Query database
        try {
            int  deviceId = detectorId.intValue()
            Date dayTime = new Date(start.longValue())
            String direction = dir.textValue()

            def json = null
            if (method == null) {
                json = mDatabaseService.getTrafficFlowPrediction(deviceId, direction, dayTime)
            } else {
                json = mDatabaseService.getTrafficFlowPrediction(deviceId, direction, method.intValue(), dayTime)
            }

            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestAnomalyData")
    def latestAnomalyData(@QueryValue Integer secondsAgo) {
        // Query database
        try {
            def json = mTrafficAnomalyDataService.getLatestAnomalyData(secondsAgo.intValue())
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestRestrictionData")
    def latestRestrictionData(@QueryValue Integer secondsAgo) {
        // Query database
        try {
            def json = mRestrictionDataService.getLatestEffectiveRestrictionData(secondsAgo)
            if (json) {
                return [ status: 'OK', data : json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestAnomalySegments")
    def latestAnomalySegments(@QueryValue Integer secondsAgo, @QueryValue Integer severity, @QueryValue Integer duration) {
        // Query database
        try {
            def json =  mTrafficIncidentDataService.getLatestIncidentData(secondsAgo.intValue(), severity, duration)
            if (json) {
                return [ status: 'OK', data : json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("incident")
    def fetchIncident(@Body def body) {
        // Query database
        try {
            def incidentId = body.id.longValue()
            def includesVideo = body.video != null ? body.video.booleanValue() : false
            def includeSegmentPath = body.path != null ? body.path.booleanValue() : false

            def json =  mTrafficIncidentDataService.getIncidentData(incidentId, includesVideo, includeSegmentPath)
            return [ status: 'OK', data : json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("incidents")
    def fetchIncidents(@Body def body) {
        if (body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No { start } was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())
            def includesVideo = body.video != null ? body.video.booleanValue() : false
            def severity = body.severity != null ? body.severity.intValue() : 0
            def duration = body.duration != null ? body.duration.intValue() : 0

            def json =  mTrafficIncidentDataService.getIncidentData(dayTime, includesVideo, severity, duration)
            return [ status: 'OK', data : json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("searchIncidents")
    def searchIncidents(@Body def body) {
        if (body.startDate == null || body.endDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { startDate, endDate} was found'
            ]
        }

        // Query database
        try {
            Date startDate = new Date(body.startDate.longValue())
            Date endDate = new Date(body.endDate.longValue())

            def route = body.route != null ? body.route.textValue() : ''
            def region = body.region != null ? body.region.intValue() : -1
            def blockage = body.blockage != null ? body.blockage.intValue(): -1
            def detourNeeded = body.detourNeeded != null ? body.detourNeeded.intValue(): -1
            def mitigation = body.mitigation != null ? body.mitigation.intValue(): -1

            def severity = body.severity != null ? body.severity.collect { it.asInt() } : [-1, -1]
            def duration = body.duration != null ? body.duration.collect { it.asInt() } : [-1, -1]
            def minSeverity = severity[0]
            def maxSeverity = severity[1]
            def minDuration = duration[0]
            def maxDuration = duration[1]

            def  includeVideo = true
            def  maxCount = body.limit != null ? body.limit.intValue() : 100

            def json =  mTrafficIncidentDataService.searchIncidentData(startDate, endDate, route, region, minSeverity, maxSeverity, minDuration, maxDuration, blockage, detourNeeded, mitigation, includeVideo, maxCount)
            return [ status: 'OK', data : json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("mdistParameters")
    def fetchMDistParameters(@Body def body) {
        // Validate parameters
        if (body.deviceId == null || body.direction == null || body.time == null) {
            return [
                    status: 'ERROR',
                    message: 'No { deviceId, direction, time } was found'
            ]
        }

        // Query database
        try {
            int  deviceId = body.deviceId.intValue()
            String direction = body.direction.textValue()
            Date time = new Date(body.time.longValue())
            def json =  mAnomalyThresholdService.getParameters(deviceId, direction, time)
            return [ status: 'OK', data : json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowVolumeSpeed")
    def trafficFlowVolumeSpeed(@Body def body) {
        // Validate parameters
        if (body.id == null || body.direction == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, direction, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            String direction = body.direction.textValue()
            Date dayTime = new Date(body.startDate.longValue())
            def secondsBeforeAndAfter = body.range ? body.range.intValue() : null

            // Query database
            def json = secondsBeforeAndAfter == null ? mTrafficFlowDataService.getTrafficSpeedVolumeData(deviceId, direction, dayTime) :
                                                       mTrafficFlowDataService.getTrafficSpeedVolumeData(deviceId, direction, dayTime, secondsBeforeAndAfter)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("trafficFlowAnomalyVolumeSpeed")
    def trafficFlowAnomalyVolumeSpeed(@Body def body) {
        // Validate parameters
        if (body.id == null || body.direction == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, direction, startDate } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            Date dayTime = new Date(body.startDate.longValue())
            String direction = body.direction.textValue()

            // Query database
            def json = mTrafficAnomalyDataService.getAnomalyVolumeSpeedData(deviceId, direction, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("fetchLCMCurve")
    def fetchLCMCurve(@Body def body) {
        // Validate parameters
        if (body.id == null || body.direction == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, direction } was found'
            ]
        }

        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            String direction = body.direction.textValue()
            def count = body.count ? body.count.intValue() : 200

            // Query database
            def json = mTrafficFlowDataService.generateLCMCurve(deviceId, direction, count)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("lcmDevices")
    def getLCMDevices() {
        return mTrafficFlowDataService.getAllLCMDetectors()
    }

    @Get("timingPlan")
    def getTimingPlan(@QueryValue String id) {
        // Query database
        try {
            def json =  mMitigationSolutionService.getTimingPlan(id)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("simulationScenarios")
    def getSimulationScenarios(@QueryValue String type) {
        // Query database
        try {
            def json =  mMitigationSolutionService.getSimulationScenarios(type)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Get("zoneList")
    def zoneList() {
        return mTrafficZoneDataService.getZoneList()
    }

    @Get("zoneDetectorList")
    def zoneDetectorList(@QueryValue String zoneId) {
        def data = mTrafficZoneDataService.getZoneDetectors(zoneId)
        if (data) {
            return [ status: 'OK', data: data ]
        } else {
            return [ status: 'ERROR', message: 'No data available.' ]
        }
    }

    @Post("trafficResponsiveData")
    def trafficResponsiveData(@Body def body) {
        // Validate parameters
        if (body.zoneId == null || body.startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { zoneId, startDate } was found'
            ]
        }

       try {
            // Extract the parameters
            Date   dayTime = new Date(body.startDate.longValue())
            String zoneId  = body.zoneId.textValue()
            boolean includeZoneDetectors = body.zoneDetectors ? body.zoneDetectors.booleanValue() : false

            // Custom weights are optional
            def customWeights = [:]
            if (body.weights != null) {
                ObjectMapper objectMapper = new ObjectMapper()
                customWeights = objectMapper.readValue(body.weights.textValue(), HashMap.class)
            }

            // Query database
            def json = mTrafficZoneDataService.getTrafficResponsiveZoneData(zoneId, dayTime, customWeights, includeZoneDetectors)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("zoneDetectorWeights")
    def getZoneDetectorWeights(@QueryValue String zoneId) {
        // Query database
        try {
            def json = mTrafficZoneDataService.getZoneDetectorWeights(zoneId)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("updateZoneDetectorWeights")
    def updateZoneDetectorWeights(@Body def body) {
        // Validate parameters
        if (body.weights == null) {
            return [
                    status: 'ERROR',
                    message: 'No { weights } was found'
            ]
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper()
            def customWeights = objectMapper.readValue(body.weights.textValue(), List.class)

            // Update database
            mTrafficZoneDataService.updateZoneDetectorWeights(customWeights)
            return [ status: 'OK' ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    @Get("trafficSignalDevices")
    def trafficSignalDevices() {
        return mTrafficSignalDeviceService.getTrafficSignalDevices()
    }

    @Get("getSignalCycleSplit")
    def getSignalCycleSplit(@QueryValue String permit) {
        // Query database
        try {
            def json = mTrafficSignalDeviceService.getSignalCycleSplit(permit)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }
}