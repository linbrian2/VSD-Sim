package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import io.micronaut.data.repository.CrudRepository
import groovy.transform.CompileStatic
import com.iai.traffic.domain.traffic.TravelAdvisory

@CompileStatic
@Repository
interface TravelAdvisoryRepository extends CrudRepository<TravelAdvisory, Long> {
    List<TravelAdvisory> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
}