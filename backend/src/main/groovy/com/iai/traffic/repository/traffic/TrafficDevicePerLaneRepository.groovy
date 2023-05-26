package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficDevicePerLane
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficDevicePerLaneRepository extends CrudRepository<TrafficDevicePerLane, Long> {
    List<TrafficDevicePerLane> findByPermitNumberAndBound(String permitNumber, String bound)
    List<TrafficDevicePerLane> findByPermitNumber(String permitNumber)
}