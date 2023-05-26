package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.IncidentVideo
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface IncidentVideoRepository extends CrudRepository<IncidentVideo, Long> {
    List<IncidentVideo> findByIncidentId(Long incidentId)
}