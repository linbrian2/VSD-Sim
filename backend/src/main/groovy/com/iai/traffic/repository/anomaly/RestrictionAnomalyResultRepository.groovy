package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.RestrictionAnomalyResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface RestrictionAnomalyResultRepository extends CrudRepository<RestrictionAnomalyResult, Long> {
    void deleteByDateCreatedBetween(Date startTime, Date endTime)
    List<RestrictionAnomalyResult> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
}