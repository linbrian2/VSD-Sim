package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import io.micronaut.data.repository.CrudRepository
import groovy.transform.CompileStatic
import java.util.Optional
import com.iai.traffic.domain.traffic.WeatherStation

@CompileStatic
@Repository
interface WeatherStationRepository extends CrudRepository<WeatherStation, Long> {
    Optional<WeatherStation> findByStationId(String stationId)
}