package com.iai.traffic.controllers

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
    @Inject SimulationService mSimulationService

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

    @Post("fetchVMSData")
    def fetchVMSData(@Body def body) {
        log.info("fetchVMSData")
        println body
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
            Boolean useSampleData = body.useSampleData.booleanValue()
            def json = mSimulationService.getFileData(path, useSampleData)
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
        log.info('setSimulationData')
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

}