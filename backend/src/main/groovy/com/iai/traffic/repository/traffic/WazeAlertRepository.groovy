package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.WazeAlert
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import io.micronaut.data.annotation.Query

@CompileStatic
@Repository
interface WazeAlertRepository extends CrudRepository<WazeAlert, Long> {

    List<WazeAlert> findByUpdatedAtBetweenOrderByUpdatedAtDesc(Date startTime, Date endTime)
    List<WazeAlert> findByUpdatedAtBetweenOrderByUpdatedAt(Date startTime, Date endTime)

    @Query(value = """SELECT * FROM deldot_traffic.waze_alert WHERE updated_at BETWEEN :startTime AND :endTime
                      GROUP BY latitude, longitude ORDER BY updated_at;""", nativeQuery = true)
    List<WazeAlert> findByUpdatedAtBetweenGroupByLatLonOrderByUpdatedAt(Date startTime, Date endTime)

}
