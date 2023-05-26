package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TravelTimeBaselineWoIncident
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface TravelTimeBaselineWoIncidentRepository extends CrudRepository<TravelTimeBaselineWoIncident, Long> {
    void deleteByLinkId(String linkId)
    List<TravelTimeBaselineWoIncident> findByLinkId(String linkId)
    List<TravelTimeBaselineWoIncident> find(String linkId, Integer timeIndex)
}