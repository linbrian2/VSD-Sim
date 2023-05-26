package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.TrafficIncidentResult
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface TrafficIncidentResultRepository extends CrudRepository<TrafficIncidentResult, Long> {
    List<TrafficIncidentResult> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
    List<TrafficIncidentResult> findByDateUpdatedBetweenOrderByDateUpdatedDesc(Date startTime, Date endTime)
    List<TrafficIncidentResult> findByDateUpdatedBetweenAndRouteOrderByDateUpdatedDesc(Date startTime, Date endTime, String route)
    List<TrafficIncidentResult> findByDateUpdatedBetweenAndRegionOrderByDateUpdatedDesc(Date startTime, Date endTime, Integer region)
    List<TrafficIncidentResult> findByDateUpdatedBetweenAndRouteAndRegionOrderByDateUpdatedDesc(Date startTime, Date endTime, String route, Integer region)

    Optional<TrafficIncidentResult> findById(long id)
}