package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.WazeAnomalyResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface WazeAnomalyResultRepository extends CrudRepository<WazeAnomalyResult, Long> {
    void deleteByTimeBetween(Date startTime, Date endTime)
    List<WazeAnomalyResult> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
}