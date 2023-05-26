package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficResponsiveZone
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TrafficResponsiveZoneRepository extends CrudRepository<TrafficResponsiveZone, Long> {
    List<TrafficResponsiveZone> findByZoneIdAndChannelNumber(String zoneId, Integer channelNumber)
    List<TrafficResponsiveZone> findByZoneIdAndChannelNumberAndPermitNumber(String zoneId, Integer channelNumber, String permitNumber)
}