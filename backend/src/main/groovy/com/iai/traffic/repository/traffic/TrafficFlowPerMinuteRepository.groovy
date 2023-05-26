package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficFlowPerMinute
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficFlowPerMinuteRepository extends CrudRepository<TrafficFlowPerMinute, Long> {
    List<TrafficFlowPerMinute> listFirstOrderByDateCreatedDesc()
    List<TrafficFlowPerMinute> findByDeviceIdAndCreatedTimeBetween(int deviceId, Date startTime, Date endTime)
}