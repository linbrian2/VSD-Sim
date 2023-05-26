package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.AnomalyThreshold
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface AnomalyThresholdRepository extends CrudRepository<AnomalyThreshold, Long> {
    List<AnomalyThreshold> find(long deviceId, int weekday, int minute)
    List<AnomalyThreshold> find(long deviceId, int weekday)
    List<AnomalyThreshold> findByDeviceId(long deviceId)
}