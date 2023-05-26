package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.BluetoothAnomalyDevice
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface BluetoothAnomalyDeviceRepository extends CrudRepository<BluetoothAnomalyDevice, Long> {
}