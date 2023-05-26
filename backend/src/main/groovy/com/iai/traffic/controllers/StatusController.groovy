package com.iai.traffic.controllers

// import com.iai.traffic.services.TrafficFlowQualityService
import groovy.util.logging.Slf4j
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED

@Controller("/status")
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class StatusController {
    // @Inject TrafficFlowQualityService mTrafficFlowQualityService

    @Post("/errors")
    def errors(@Body def body) {
        return 'errors'
    //     // Validate parameters
    //     if (body.start == null) {
    //         return [
    //                 status: 'ERROR',
    //                 message: 'No { start } was found'
    //         ]
    //     }

    //     // Query database
    //     try {
    //         Date dayTime = new Date(body.start.longValue())
    //         def json = mTrafficFlowQualityService.getErrorDistribution(dayTime)
    //         if (json) {
    //             return [ status: 'OK', data: json ]
    //         } else {
    //             return [ status: 'ERROR', message: 'No data available.' ]
    //         }
    //     } catch (Exception e) {
    //         return [ status: 'ERROR', message: e.getMessage() ]
    //     }
    }
}