package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficFlowPrediction
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficFlowPredictionRepository extends CrudRepository<TrafficFlowPrediction, Long> {
    List<TrafficFlowPrediction> listFirstOrderByDateCreatedDesc()
    List<TrafficFlowPrediction> findByDeviceIdAndDirectionAndTimeBetween(int deviceId, String direction, Date startTime, Date endTime)
    List<TrafficFlowPrediction> findByDeviceIdAndDirectionAndMethodAndTimeBetween(int deviceId, String direction, int method, Date startTime, Date endTime)
}