package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TravelSegmentNew
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TravelSegmentNewRepository extends CrudRepository<TravelSegmentNew, Long> {
    Optional<TravelSegmentNew> findByLinkId(String linkId)
    List<TravelSegmentNew> findByRouteAndDirection(String route, String direction)
    List<TravelSegmentNew> findByRoute(String route)
    List<TravelSegmentNew> findByIdGreaterThan(Long id)
}