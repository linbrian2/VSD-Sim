package com.iai.traffic.repository.traffic

import java.util.Optional
import io.micronaut.data.annotation.*
import io.micronaut.data.repository.CrudRepository
import groovy.transform.CompileStatic
import com.iai.traffic.domain.traffic.TrafficDevice

@CompileStatic
@Repository
interface TrafficDeviceRepository extends CrudRepository<TrafficDevice, Long> {
    Optional<TrafficDevice> findByDeviceId(Long deviceId)
    Optional<TrafficDevice> findByUid(String uid)
    List<TrafficDevice> findByDeviceIdInList(List<Long> devices)
    List<TrafficDevice> findByIdGreaterThan(Long id)
}