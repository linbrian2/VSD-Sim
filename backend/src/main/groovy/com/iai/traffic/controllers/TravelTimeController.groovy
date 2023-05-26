package com.iai.traffic.controllers


import com.iai.traffic.services.*
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller("/travelTime")
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class TravelTimeController {
    @Inject TravelTimeDataService mTravelTimeDataService

    @Get("bluetoothAnomalyDevices")
    def bluetoothAnomalyDevices() {
        return mTravelTimeDataService.getBluetoothAnomalyDevices()
    }

    @Get("bluetoothSegments")
    def bluetoothSegments() {
        return mTravelTimeDataService.getBluetoothSegments()
    }

    @Get("congestedBluetoothSegments")
    def congestedBluetoothSegments(@QueryValue Integer level) {
        return mTravelTimeDataService.getSelectedBluetoothSegments(level)
    }

    @Get("latestBluetoothAnomalyData")
    def latestBluetoothAnomalyData(@QueryValue Integer secondsAgo) {
        // Query database
        try {
            def json = mTravelTimeDataService.getLatestBluetoothAnomalyData(secondsAgo.intValue())
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("travelTimeData")
    def travelTimeData(@Body def body) {
        // Validate parameters
        if (body.linkId == null || body.start == null || body.interval == null) {
            return [
                    status: 'ERROR',
                    message: 'No { linkId, start, interval } was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())
            String linkId = body.linkId.textValue()
            int intervalInSecs = (int)(body.interval.intValue()/1000)

            def json = mTravelTimeDataService.getTravelTimeData(linkId, intervalInSecs, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("travelTimeSegments")
    def getTravelTimeSegments(@Body def body) {
        // Validate parameters
        if (body.route == null) {
            return [
                    status: 'ERROR',
                    message: 'No { route } was found'
            ]
        }

        // Query database
        try {
            String route = body.route.textValue()
            String direction = body.direction != null ? body.direction.textValue() : null
            def json = mTravelTimeDataService.getSegments(route, direction)
            return [ status: 'OK', data: json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("travelTimeHeatMapData")
    def travelTimeHeatMapData(@Body def body) {
        // Validate parameters
        if (body.route == null || body.direction == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No { route, direction, start } was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())
            Integer interval = body.interval != null ? body.interval.intValue() : 300
            String route = body.route.textValue()
            String direction = body.direction.textValue()
            def json = mTravelTimeDataService.getTravelTimeHeatMapData(route, direction, dayTime, interval)
            return [ status: 'OK', data: json ]
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestTravelTimeInfo")
    def latestTravelTimeInfo(@QueryValue String linkId) {
        // Query database
        try {
            def json = mTravelTimeDataService.getLatestTravelTimeInfo(linkId, 300)
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