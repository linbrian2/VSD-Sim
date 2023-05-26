package com.iai.traffic

import com.iai.traffic.services.TrafficAnomalyDataService
import com.iai.traffic.services.TrafficIncidentDataService
import com.iai.traffic.services.TravelTimeDataService
import com.iai.traffic.services.WeatherDataService
import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.security.annotation.Secured
import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnClose
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.OnOpen
import io.micronaut.websocket.annotation.ServerWebSocket
import org.reactivestreams.Publisher
import groovy.util.logging.Slf4j

import com.fasterxml.jackson.databind.ObjectMapper
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton

import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED

// https://stackoverflow.com/questions/63523069/micronaut-websocket-security
@Slf4j
@Singleton
@ServerWebSocket("/ws")
@Secured(IS_ANONYMOUS)
class AnomalyWebSocket {
    @Inject WebSocketBroadcaster       mWebSocketBroadcaster
    @Inject TrafficAnomalyDataService  mTrafficAnomalyDataService
    @Inject TravelTimeDataService      mTravelTimeDataService
    @Inject WeatherDataService         mWeatherDataService
    @Inject TrafficIncidentDataService mTrafficIncidentDataService

    def mOpenSessions = [:]

    @PostConstruct
    def initialize() {
        log.info("AnomalyWebSocket started. Start anomaly checking task ...")
    }

    @OnOpen
    Publisher<String> onOpen(WebSocketSession session) {
        log.info("Opening connection from ${session.getId()}")
        mOpenSessions << [(session.getId()): session]
        return session.send(stringify("Hello from backend service."))
    }

    @OnMessage
    Publisher<String> onMessage(String message, WebSocketSession session) {
        log.info("message received: $message")
        return session.send(stringify("Thanks for the message"))
    }

    @OnClose
    Publisher<String> onClose(WebSocketSession session) {
        mOpenSessions.remove(session.getId())
        log.info("Closing")
    }

    def isValid() {
        return !mOpenSessions.isEmpty()
    }

    // https://docs.micronaut.io/latest/api/io/micronaut/scheduling/cron/CronExpression.html
    //    +---------------- second (0 - 59)
    //    |   |---------------- minute (0 - 59)
    //    |   |  +------------- hour (0 - 23)
    //    |   |  |  +---------- day of month (1 - 31)
    //    |   |  |  |  +------- month (1 - 12)
    //    |   |  |  |  |  +---- day of week (1 - 7) (MON-SUN)
    //    |   |  |  |  |  |
    //
    // Trigger the job every one minute at 5 seconds
    @Scheduled(cron = "5 */1 * * * *")
    def detectBluetoothAnomaly() {
        if (isValid()) {
            //log.info("Running bluetooth anomaly checking ...")
            def result = mTravelTimeDataService.getLatestBluetoothAnomalyData(60)
            if (!result.isEmpty()) {
                broadcastMessage('SOCKET_BLUETOOTH', result)
            }
        }
    }

    // Trigger the job every five minutes at 10 seconds
    @Scheduled(cron = "10 */5 * * * *")
    def detectTrafficFlowAnomaly() {
        if (isValid()) {
            //log.info("Running traffic flow anomaly checking ...")
            def result = mTrafficAnomalyDataService.getLatestAnomalyData(300)
            if (!result.isEmpty()) {
                broadcastMessage('SOCKET_FLOW', result)
            }
        }
    }

    // Trigger the job every five minutes at 20 seconds
    @Scheduled(cron = "20 */5 * * * *")
    def detectWeatherChanges() {
        if (isValid()) {
            //log.info("Running weather checking ...")
            def result = mWeatherDataService.getLatestWeatherData(300)
            if (!result.isEmpty()) {
                broadcastMessage('SOCKET_WEATHER', result)
            }
        }
    }

    // Trigger the job every one minutes at 40 seconds
    @Scheduled(cron = "40 */1 * * * *")
    def detectIncidentChanges() {
        if (isValid()) {
            //log.info("Running incident checking ...")
            def result = mTrafficIncidentDataService.getLatestIncidentData(60)
            if (!result.isEmpty()) {
                broadcastMessage('SOCKET_ANOMALY', result)
            }
        }
    }

    def broadcastMessage(String type, def data) {
        if (isValid()) {
            mWebSocketBroadcaster.broadcastAsync(ObjToString(type, data))
        }
    }

    def ObjToString(String type, def data) {
        def value = [data: data, namespace: 'traffic', mutation: type]
        ObjectMapper objectMapper = new ObjectMapper()
        return objectMapper.writeValueAsString(value)
    }

    def stringify(String message) {
        def value = [data: message]
        ObjectMapper objectMapper = new ObjectMapper()
        return objectMapper.writeValueAsString(value)
    }
}