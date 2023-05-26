package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.TrafficFlow
import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import java.util.Optional
import com.iai.traffic.domain.traffic.TravelTimeNew
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface TravelTimeNewRepository extends CrudRepository<TravelTimeNew, Long> {
        List<TravelTimeNew> findByLinkIdAndDateCreatedBetween(String linkId, Date startTime, Date endTime)
        List<TravelTimeNew> findByLinkIdInListAndDateCreatedBetween(List<String> linkIds, Date startTime, Date endTime)
        List<TravelTimeNew> findByDateCreatedBetweenOrderByCalculationTimestampDesc(Date startTime, Date endTime)
        List<TravelTimeNew> findByDateCreatedBetweenOrderByCalculationTimestamp(Date startTime, Date endTime)

        @Query(value = """SELECT * FROM deldot_traffic.travel_time_new
                        WHERE calculation_timestamp BETWEEN :startTime AND :endTime AND mod(minute(calculation_timestamp), 20) = 0
                        """, nativeQuery = true)
        List<TravelTimeNew> findEvery15Min(Date startTime, Date endTime)
}
