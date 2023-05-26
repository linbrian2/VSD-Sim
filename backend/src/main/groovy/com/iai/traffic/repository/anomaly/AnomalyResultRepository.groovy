package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.AnomalyResult
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface AnomalyResultRepository extends CrudRepository<AnomalyResult, Long> {
    void deleteByDeviceIdAndDirectionAndTimeBetween(Integer deviceId, String direction, Date startTime, Date endTime)
    List<AnomalyResult> findByDeviceIdInListAndDirectionAndTimeBetween(List<Integer> devices, String direction, Date startTime, Date endTime)
    List<AnomalyResult> findByDeviceIdAndDirectionAndTimeBetween(Integer deviceId, String direction, Date startTime, Date endTime)
    List<AnomalyResult> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
}