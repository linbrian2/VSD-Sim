package com.iai.traffic.messaging

import com.iai.traffic.AnomalyWebSocket
import com.iai.traffic.domain.anomaly.AnomalyResult
import com.iai.traffic.domain.anomaly.BluetoothAnomalyResult
import com.iai.traffic.domain.anomaly.RestrictionAnomalyResult
import com.iai.traffic.domain.anomaly.TrafficIncidentResult
import com.iai.traffic.domain.anomaly.WazeAnomalyResult
import com.iai.traffic.domain.solution.MitigationResult
import com.iai.traffic.utils.SimulationProgress
import groovy.util.logging.Slf4j
import io.micronaut.configuration.rabbitmq.annotation.Queue
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener
import io.micronaut.context.annotation.Requires

import javax.inject.Inject

@Slf4j
@RabbitListener
@Requires(property="rabbitmq.enabled", value='true')
class MessageListener {
    @Inject AnomalyWebSocket mAnomalyWebSocket

    @Queue("trafficFlow")
    void receiveTrafficFlowAnomaly(AnomalyResult result) {
        log.info("Traffic flow anomaly received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_FLOW', [result])
    }

    @Queue("travelTime")
    void receiveTravelTimeAnomaly(BluetoothAnomalyResult result) {
        log.info("Travel time anomaly received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_BLUETOOTH', [result])
    }

    @Queue("waze")
    void receiveWazeAnomaly(WazeAnomalyResult result) {
        log.info("Waze anomaly received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_WAZE', [result])
    }

    @Queue("restriction")
    void receiveRestrictionAnomaly(RestrictionAnomalyResult result) {
        log.info("Restriction anomaly received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_RESTRICTION', [result])
    }

    @Queue("incident")
    void receiveIncident(TrafficIncidentResult result) {
        log.info("Traffic incident received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_ANOMALY', [result])
    }

    @Queue("mitigation")
    void receiveMitigationResult(MitigationResult result) {
        log.info("Incident mitigation result received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_MITIGATION', result)
    }

    @Queue("progress")
    void receiveSimulationProgressResult(SimulationProgress result) {
        log.info("Incident simulation progress received!")
        mAnomalyWebSocket.broadcastMessage('SOCKET_SIMULATION', result)
    }
}