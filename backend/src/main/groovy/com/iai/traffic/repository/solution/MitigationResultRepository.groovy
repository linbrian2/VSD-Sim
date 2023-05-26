package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.MitigationResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface MitigationResultRepository extends CrudRepository<MitigationResult, Long> {
    List<MitigationResult> findByIncidentId(Long incidentId)
    List<MitigationResult> findByIncidentIdAndTime(Long incidentId, Date time)
}