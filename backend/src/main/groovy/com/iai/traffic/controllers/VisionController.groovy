package com.iai.traffic.controllers

import com.iai.traffic.services.JetsonVisionService
import com.iai.traffic.services.TrafficVisionService
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller("/vision")
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class VisionController {
    @Inject TrafficVisionService mTrafficVisionService

    @Get("devices")
    def devices() {
        return mTrafficVisionService.getAllDevices()
    }

    @Get("device")
    def device(@QueryValue Integer id, @QueryValue String type) {
        // Query database
        try {
            def json = mTrafficVisionService.getDeviceInfo(id, type)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("counts")
    def counts(@Body def body) {
        // Validate parameters
        if (body.id == null || body.type == null || body.start == null || body.interval == null) {
            return [
                    status: 'ERROR',
                    message: 'No {id, type, start, interval} was found'
            ]
        }

        // Query database
        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def type = body.type.textValue()
            def start = body.start.longValue()
            def interval = body.interval.intValue()
            Date dayTime = new Date(start)
            def json = mTrafficVisionService.getTrafficCounts(deviceId, type, dayTime, interval, false)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("visionResult")
    def visionResult(@Body def body) {
        // Validate parameters
        if (body.id == null || body.type == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No {id, type, start} was found'
            ]
        }

        // Query database
        try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def type = body.type.textValue()
            def start = body.start.longValue()

            def  outputType = (body.output != null) ? body.output.intValue() : 0
            Date dayTime = new Date(start.longValue())

            def json = (outputType == 0) ?
                    mTrafficVisionService.getTrafficVisionResults(deviceId, type, dayTime) :
                    mTrafficVisionService.getTrafficVisionVideos(deviceId, type, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("visionData")
    def visionData(@Body def body) {
        // Validate parameters
        if (body.id == null || body.type == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No {id, type, start} was found'
            ]
        }

        // Query database
       try {
            // Extract the parameters
            def deviceId = body.id.intValue()
            def type = body.type.textValue()
            def start = body.start.longValue()

            Date dayTime = new Date(start)
            def json = mTrafficVisionService.getTrafficVision5Min(deviceId, type, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("flowData")
    def flowData(@Body def body) {
        // Validate parameters
        if (body.id == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No {id, start} was found'
            ]
        }

        // Query database
        try {
            // Extract the parameters
            def detectorId = body.id.textValue()
            def start      = body.start.longValue()
            Date dayTime = new Date(start)

            def json = mTrafficVisionService.getTrafficFlow(detectorId, dayTime)
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