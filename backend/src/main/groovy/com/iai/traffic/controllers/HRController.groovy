package com.iai.traffic.controllers

// import com.iai.traffic.service.HighResolutionDataService
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Body
import io.micronaut.http.HttpStatus
import javax.inject.Inject
import groovy.util.logging.Slf4j

import io.micronaut.security.annotation.Secured
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;

@Controller("/hr")
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class HRController {
    // @Inject HighResolutionDataService mHighResolutionDataService

    @Get("/")
    HttpStatus index() {
        return HttpStatus.OK
    }

    @Get("devices")
    def devices() {
        return 'devices'
        // return mHighResolutionDataService.getAllSignalDevices()
    }

    @Get("status")
    def status() {
        return 'status'
        // return mHighResolutionDataService.getAllSignalStatus()
    }
}