package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.CriticalValue
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository(value = 'anomaly')
interface CriticalValueRepository extends CrudRepository<CriticalValue, Long> {
    List<CriticalValue> find(Long deviceId, Integer type)
}