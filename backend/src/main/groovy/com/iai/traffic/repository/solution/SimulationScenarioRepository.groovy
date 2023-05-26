package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.SimulationScenario
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface SimulationScenarioRepository extends CrudRepository<SimulationScenario, Long> {
    List<SimulationScenario> findByType(String type)
}