package com.iai.traffic.repository.vision

import com.iai.traffic.domain.vision.TrafficVision
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'vision')
interface JetsonVisionRepository extends CrudRepository<TrafficVision, Long> {
    List<TrafficVision> findByDeviceIdAndFlowStatusAndCreatedTimeBetween(int deviceId, int flowStatus, Date startTime, Date endTime)
    List<TrafficVision> findByDeviceIdAndCreatedTimeBetween(int deviceId, Date startTime, Date endTime)
    List<TrafficVision> findByDateCreatedBetween(Date startTime, Date endTime)
}