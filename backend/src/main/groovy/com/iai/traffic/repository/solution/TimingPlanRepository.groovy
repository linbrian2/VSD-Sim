package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.TimingPlan
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface TimingPlanRepository extends CrudRepository<TimingPlan, Long> {
    List<TimingPlan> findByPlanId(String planId)
}