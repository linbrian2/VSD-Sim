package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import io.micronaut.data.repository.CrudRepository
import java.util.Optional
import groovy.transform.CompileStatic
import com.iai.traffic.domain.traffic.TravelTimeBaseline

@CompileStatic
@Repository
interface TravelTimeBaselineRepository extends CrudRepository<TravelTimeBaseline, Long> {
   
   List<TravelTimeBaseline> findByLinkId(String linkId)
}