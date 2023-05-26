package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.TrafficFlowQuality
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface TrafficFlowQualityRepository extends CrudRepository<TrafficFlowQuality, Long> {
    long countByDateCreatedBetween(Date startTime, Date endTime)
    List<TrafficFlowQuality> findByDateCreatedBetween(Date startTime, Date endTime)
    List<TrafficFlowQuality> findByDeviceIdAndDateCreatedBetween(Integer deviceId, Date startTime, Date endTime)
}