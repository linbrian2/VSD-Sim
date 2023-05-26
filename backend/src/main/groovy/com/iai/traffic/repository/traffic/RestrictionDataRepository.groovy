package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import io.micronaut.data.repository.CrudRepository
import groovy.transform.CompileStatic
import com.iai.traffic.domain.traffic.RestrictionData

@CompileStatic
@Repository
interface RestrictionDataRepository extends CrudRepository<RestrictionData, Long> {
    @Query(value = "SELECT * FROM restriction_data r WHERE r.end_time > :startTime GROUP BY r.restriction_id LIMIT :limit", nativeQuery = true)
    List<RestrictionData> findData(Date startTime, int limit)
}