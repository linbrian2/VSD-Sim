package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficSignalDevice
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficSignalDeviceRepository extends CrudRepository<TrafficSignalDevice, Long> {
    Optional<TrafficSignalDevice> findByPermit(String permit)
}