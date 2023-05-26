package com.iai.traffic.controllers

import com.iai.traffic.services.MitigationSolutionService
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller("/solution")
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class SolutionController {
    @Inject MitigationSolutionService mMitigationSolutionService

    @Post("solutionData")
    def getSolutionData(@Body def body) {
        // Validate parameters
        if (body.incidentId == null) {
            return [
                    status: 'ERROR',
                    message: 'No { incidentId } was found'
            ]
        }

        // Query database
        try {
            Long incidentId = body.incidentId.longValue()
            def json = mMitigationSolutionService.getMitigationSolution(incidentId)
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

    @Post("rampVolumes")
    def getRampVolumes(@Body def body) {
        // Validate parameters
        if (body.incidentId == null) {
            return [
                    status : 'ERROR',
                    message: 'No { incidentId } was found'
            ]
        }

        // Query database
        try {
            Long incidentId = body.incidentId.longValue()
            def json = mMitigationSolutionService.getMitigationRampVolumes(incidentId)
            if (json) {
                return [status: 'OK', data: json]
            } else {
                return [status: 'ERROR', message: 'No data available.']
            }
        } catch (Exception e) {
            log.error(e.getMessage())
            return [status: 'ERROR', message: e.getMessage()]
        }
    }
}