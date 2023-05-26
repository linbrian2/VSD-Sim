package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.DetourRamp
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface DetourRampRepository extends CrudRepository<DetourRamp, Long> {
    @Query(value = "SELECT * FROM detour_ramp r WHERE r.incident_dir = :dir AND r.lat1 > :lat AND r.lat2 < :lat AND r.lon1 < :lon AND r.lon2 > :lon", nativeQuery = true)
    List<DetourRamp> findData(String dir, double lat, double lon)
}