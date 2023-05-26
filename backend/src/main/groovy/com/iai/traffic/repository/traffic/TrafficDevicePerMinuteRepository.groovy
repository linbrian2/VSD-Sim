package com.iai.traffic.repository.traffic


import com.iai.traffic.domain.traffic.TrafficDevicePerMinute
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficDevicePerMinuteRepository extends CrudRepository<TrafficDevicePerMinute, Long> {
    List<TrafficDevicePerMinute> findByDeviceId(Integer deviceId)
    List<TrafficDevicePerMinute> findByDeviceIdAndBound(Integer deviceId, String bound)
    List<TrafficDevicePerMinute> findByDeviceIdInList(List<Integer> devices)
}