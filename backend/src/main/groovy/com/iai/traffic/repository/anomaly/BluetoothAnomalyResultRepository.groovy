package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.BluetoothAnomalyResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface BluetoothAnomalyResultRepository extends CrudRepository<BluetoothAnomalyResult, Long> {
    void deleteByLinkIdAndDateCreatedBetween(String linkId, Date startTime, Date endTime)
    List<BluetoothAnomalyResult> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
    List<BluetoothAnomalyResult> findByLinkIdAndDateCreatedBetween(String linkId, Date startTime, Date endTime)
}