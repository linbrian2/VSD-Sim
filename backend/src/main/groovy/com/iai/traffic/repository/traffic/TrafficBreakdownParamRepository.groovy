package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficBreakdownParam
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficBreakdownParamRepository extends CrudRepository<TrafficBreakdownParam, Long> {
    Optional<TrafficBreakdownParam> findByDeviceIdAndDirection(Integer deviceId, String direction)
    List<TrafficBreakdownParam> findByIdGreaterThan(Long id)
}