package com.iai.traffic.repository.traffic


import com.iai.traffic.domain.traffic.WeatherData
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@CompileStatic
@Repository
interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByStationIdAndDataReceivedBetween(String stationId,Date startTime, Date endTime)
    List<WeatherData> findByStationIdInListAndDataReceivedBetween(List<String> stationIds, Date startTime, Date endTime)
}