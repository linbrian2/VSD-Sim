package com.iai.traffic.repository.anomaly

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import com.iai.traffic.domain.anomaly.AnomalyDevice

@Repository(value = 'anomaly')
interface AnomalyDeviceRepository extends CrudRepository<AnomalyDevice, Long> {
    List<AnomalyDevice> findByZone(Integer zone)
    List<AnomalyDevice> findByRouteAndDirection(String route, String direction)
    List<AnomalyDevice> findByDeviceIdAndDirection(Integer deviceId, String direction)
}