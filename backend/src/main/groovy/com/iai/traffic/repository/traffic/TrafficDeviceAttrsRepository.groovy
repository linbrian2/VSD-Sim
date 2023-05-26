package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficDeviceAttrs
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficDeviceAttrsRepository extends CrudRepository<TrafficDeviceAttrs, Long> {
    Optional<TrafficDeviceAttrs> findByDeviceIdAndDirection(Long deviceId, String direction)
    List<TrafficDeviceAttrs> findByDeviceIdInList(List<Long> devices)
}