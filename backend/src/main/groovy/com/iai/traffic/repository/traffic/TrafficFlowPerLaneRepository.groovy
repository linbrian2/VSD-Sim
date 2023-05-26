package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficFlowPerLane
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficFlowPerLaneRepository extends CrudRepository<TrafficFlowPerLane, Long> {
    List<TrafficFlowPerLane> listFirstOrderByDateCreatedDesc()
    List<TrafficFlowPerLane> findByPermitNumberAndCreatedTimeBetween(String permitNumber, Date startTime, Date endTime)
}