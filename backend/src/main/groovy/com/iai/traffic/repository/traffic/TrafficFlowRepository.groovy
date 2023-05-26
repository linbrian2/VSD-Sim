package com.iai.traffic.repository.traffic

import io.micronaut.data.annotation.*
import io.micronaut.data.repository.CrudRepository
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.*
import io.micronaut.data.model.*
import java.util.Optional
import io.micronaut.data.annotation.Query

import com.iai.traffic.domain.traffic.TrafficFlow

@CompileStatic
@Repository
interface TrafficFlowRepository extends CrudRepository<TrafficFlow, Long> {
    List<TrafficFlow> listFirstOrderByDateCreatedDesc()
    List<TrafficFlow> findByDeviceIdAndDirectionAndCreatedTimeBetween(int deviceId, String direction, Date startTime, Date endTime)
    List<TrafficFlow> findByDateCreatedBetweenAndCreatedTimeBetweenOrderByCreatedTimeDescOrderByDeviceIdDesc(Date startTime, Date endTime, Date startTimeC, Date endTimeC)
    List<TrafficFlow> findByDateCreatedBetweenOrderByDateCreatedDesc(Date startTime, Date endTime)
    List<TrafficFlow> findByDateCreatedBetween(Date startTime, Date endTime)

    @Query(value = """SELECT * FROM deldot_traffic.traffic_flow
                    WHERE date_created BETWEEN :startTimeOff AND :endTimeOff AND
                    created_time BETWEEN :startTime AND :endTime
                    GROUP BY device_id, UNIX_TIMESTAMP(created_time) DIV 1200, direction""", nativeQuery = true)
    List<TrafficFlow> findEvery15Min(Date startTimeOff, Date endTimeOff, Date startTime, Date endTime)

    @Query(value = """SELECT * FROM deldot_traffic.traffic_flow 
                      WHERE created_time BETWEEN :startTime AND :endTime AND mod(minute(created_time), 20) = 0
                   """, nativeQuery = true)
    List<TrafficFlow> findEvery20Min(Date startTime, Date endTime)
}
