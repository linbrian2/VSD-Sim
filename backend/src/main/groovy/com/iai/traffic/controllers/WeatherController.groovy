package com.iai.traffic.controllers


import com.iai.traffic.services.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller("/weather")
//@Secured(IS_AUTHENTICATED
@Secured(IS_ANONYMOUS)
@Slf4j
class WeatherController {
    @Inject WeatherDataService mWeatherDataService

    @Get("weatherStations")
    def weatherStations() {
        return mWeatherDataService.getWeatherStations()
    }

    @Post("weatherData")
    def weatherData(@Body def body) {
        // Extract the parameters
        def stationId = body.id
        def interval = body.interval
        def startDate = body.startDate
        def endDate = body.endDate

        // Validate parameters
        if (stationId == null || interval == null || startDate == null) {
            return [
                    status: 'ERROR',
                    message: 'No { id, interval, startDate } was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(startDate.longValue())
            Date startTime = AppUtils.getStartOfDay(dayTime)
            Date endTime   = AppUtils.getEndOfDay(dayTime)
            if (endDate != null) {
                endTime = AppUtils.getEndOfDay(endDate)
            }

            def intervalInSecs = (int)(interval.intValue()/1000)
            def json = mWeatherDataService.getWeatherData(stationId.textValue(), intervalInSecs, startTime, endTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            log.error(e.getMessage())
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Post("weatherDataOneDay")
    def weatherDataOneDay(@Body def body) {
        // Validate parameters
        if (body.stationId == null || body.start == null) {
            return [
                    status: 'ERROR',
                    message: 'No { stationId, start } was found'
            ]
        }

        // Query database
        try {
            Date dayTime = new Date(body.start.longValue())
            String stationId = body.stationId.textValue()
            def json = mWeatherDataService.getWeatherData(stationId, dayTime)
            if (json) {
                return [ status: 'OK', data: json ]
            } else {
                return [ status: 'ERROR', message: 'No data available.' ]
            }
        } catch (Exception e) {
            log.error(e.getMessage())
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestWeatherData")
    def latestWeatherData(@QueryValue Integer secondsAgo) {
        // Query database
        try {
            def json =  mWeatherDataService.getLatestWeatherData(secondsAgo.intValue())
            if (json) {
                return [ status: 'OK', data : json ]
            } else {
                return [ status: 'OK', data: [:] ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }

    @Get("latestWeatherDataInfo")
    def latestWeatherDataInfo(@QueryValue String stationId, @QueryValue Integer secondsAgo) {
        // Query database
        try {
            def json = mWeatherDataService.getLatestWeatherData(stationId, secondsAgo.intValue())
            if (json) {
                return [ status: 'OK', data : json ]
            } else {
                return [ status: 'OK', data: [:] ]
            }
        } catch (Exception e) {
            return [ status: 'ERROR', message: e.getMessage() ]
        }
    }
}