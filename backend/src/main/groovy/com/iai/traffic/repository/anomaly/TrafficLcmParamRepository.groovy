package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.TrafficLcmParam
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface TrafficLcmParamRepository extends CrudRepository<TrafficLcmParam, Long> {
    Optional<TrafficLcmParam> findByDeviceIdAndDirection(Integer deviceId, String direction)
}