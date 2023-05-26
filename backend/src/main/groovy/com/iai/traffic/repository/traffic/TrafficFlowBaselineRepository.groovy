package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficFlowBaseline
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface TrafficFlowBaselineRepository extends CrudRepository<TrafficFlowBaseline, Long> {
    Optional<TrafficFlowBaseline> findByDeviceIdAndDirectionAndWeekdayAndMinute(Integer deviceId, String direction, Integer weekday, Integer minute)
    List<TrafficFlowBaseline> findByDeviceIdAndDirectionAndWeekday(Integer deviceId, String direction, Integer weekday)
}