package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficVision
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface TrafficVisionRepository extends CrudRepository<TrafficVision, Long> {
    List<TrafficVision> findByDeviceIdAndFlowStatusAndCreatedTimeBetween(int deviceId, int flowStatus, Date startTime, Date endTime)
    List<TrafficVision> findByDeviceIdAndCreatedTimeBetween(int deviceId, Date startTime, Date endTime)
}