package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficAlert
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficAlertRepository extends CrudRepository<TrafficAlert, Long> {
    Optional<TrafficAlert> findById(Long id)
}