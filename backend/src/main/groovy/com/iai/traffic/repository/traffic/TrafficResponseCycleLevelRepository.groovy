package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficResponseCycleLevel
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficResponseCycleLevelRepository extends CrudRepository<TrafficResponseCycleLevel, Long> {
    List<TrafficResponseCycleLevel> findByZoneIdAndCalculatorType(String zoneId, Integer calculatorType)
}