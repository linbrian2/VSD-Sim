package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.TrafficAnomalyResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface TrafficAnomalyResultRepository extends CrudRepository<TrafficAnomalyResult, Long> {
    Optional<TrafficAnomalyResult> findByDeviceIdAndDirectionAndTime( Integer deviceId, String direction, Date time)
    List<TrafficAnomalyResult> findByDeviceIdAndDirectionAndTimeBetween( Integer deviceId, String direction, Date startTime, Date endTime)
}