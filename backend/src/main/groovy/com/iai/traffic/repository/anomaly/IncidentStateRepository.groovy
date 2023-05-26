package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.IncidentState
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface IncidentStateRepository extends CrudRepository<IncidentState, Long> {
    List<IncidentState> findByIncidentId(Long incidentId)
    List<IncidentState> findTop1ByIncidentIdAndDeviceIdAndDirectionOrderByTimeDesc(Long incidentId, Integer deviceId, String direction)
}