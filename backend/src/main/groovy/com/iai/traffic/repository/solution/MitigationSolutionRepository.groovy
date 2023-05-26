package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.MitigationSolution
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface MitigationSolutionRepository extends CrudRepository<MitigationSolution, Long> {
    List<MitigationSolution> findByIncidentId(Long incidentId)
    Integer countByIncidentId(Long incidentId)
    List<MitigationSolution> findByIncidentIdAndEvalTime(Long incidentId, Date time)
}