package com.iai.traffic.controllers

import com.iai.traffic.services.*
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured

import java.time.ZoneId
import java.time.Instant

import io.micronaut.http.HttpResponse

import javax.inject.Inject

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS

@Controller('/bluetooth')
//@Secured(IS_AUTHENTICATED)
@Secured(IS_ANONYMOUS)
@Slf4j
class BluetoothController {

    @Inject BluetoothDataService apiService

    // Segments

    @Get('/init/full')
    def initFullDay() {
        return apiService.processFullDayData()
    }

    @Get('/segments/full')
    def segmentsFullDay() {
        return apiService.getSegmentsFullJSON()
    }

    @Get('/segments/{endTSStr}')
    def fetchSegments(String endTSStr) {
        long endTS = Long.parseLong(endTSStr)
        Date startTime = new Date(endTS - (15 * 60 * 1000))
        Date endTime = new Date(endTS)
        return apiService.fetchSegments(startTime, endTime)
    }

    @Get('/segments/full/{endTSStr}')
    def fetchSegmentsFull(String endTSStr) {
        Date dayTime = new Date(Long.parseLong(endTSStr))
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return apiService.fetchSegments(startTime, endTime)
    }

    /* Waze */

    @Get('/waze/full')
    def wazeFullDay() {
        return apiService.getWazeAlertsFullDayJSON()
    }

    @Get('/waze/latest-{durStr}m/{endTSStr}')
    def fetchWaze(String durStr, String endTSStr) {
        long endTS = Long.parseLong(endTSStr)
        Date startTime = new Date(endTS - (Integer.parseInt(durStr) * 60 * 1000))
        Date endTime = new Date(endTS)
        return apiService.fetchWazeAlerts(startTime, endTime)
    }

    @Get('/waze/full/{endTSStr}')
    def fetchWazeFull(String endTSStr) {
        Date dayTime = new Date(Long.parseLong(endTSStr))
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime = AppUtils.getEndOfDay(dayTime)
        return apiService.fetchWazeAlerts(startTime, endTime)
    }

    /* Devices */

    @Get('/devices/full')
    def devicesFullDay() {
        return apiService.getDevicesFullJSON()
    }

    @Get('/devices/{endTSStr}')
    def fetchDevices(String endTSStr) {
        long endTS = Long.parseLong(endTSStr)
        Date startTime = new Date(endTS - (5 * 60 * 1000))
        Date endTime = new Date(endTS)
        return apiService.fetchDevices(startTime, endTime)
    }

    @Get('/devices/full/{endTSStr}')
    def fetchDevicesFull(String endTSStr) {
        Date dayTime = new Date(Long.parseLong(endTSStr))
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime = AppUtils.getEndOfDay(dayTime)
        return apiService.fetchDevices(startTime, endTime)
    }

    /* Traffic Flow */

    @Get('/historical/{name}/w-incidents')
    def historicalWIncidents(String name) {
        return apiService.getSegmentHistoricalWIncident(name)
    }

    @Get('/historical/{name}/wo-incidents')
    def historicalWoIncidents(String name) {
        return apiService.getSegmentHistoricalWoIncident(name)
    }

    @Get('/current/{name}/{endTSStr}')
    def currentTTFull(String name, String endTSStr) {
        log.info('{}', 'GET: /api/current/' + name + '/' + endTSStr)
        Date dayTime = new Date(Long.parseLong(endTSStr))
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime = dayTime
        return apiService.getCurrentTT(name, startTime, endTime)
    }

    /* Sensors */

    @Get('/sensors')
    def sensors() {
        log.info('{}', 'GET: /api/sensors')
        return apiService.getSensorsJSON()
    }

    /* Breakdown Probability */

    @Get('/breakdown-prob/list')
    def breakdownProbList() {
        return apiService.getBPDevices()
    }

    @Get('/breakdown-prob/{id}/{dir}/{endTSStr}')
    def breakdownProb(String id, String dir, String endTSStr) {
        int deviceId = Integer.parseInt(id)
        Date dayTime = new Date(Long.parseLong(endTSStr))
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime = AppUtils.getEndOfDay(dayTime)
        return apiService.getBreakdownProb(deviceId, dir, startTime, endTime)
    }

}
