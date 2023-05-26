package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class WeatherData {
    @Id    @GeneratedValue(strategy = GenerationType.IDENTITY)    Long id;
    @DateCreated Date dateCreated
    String   stationId
    Date     dataReceived
    boolean  lastPollSuccessful
    Date     sunrise
    Date     sunset
    Date     totalDarkness
    Integer  airTemp
    Integer  maxTemp
    Integer  minTemp
    Integer  dewPoint
    Integer  wetBulbTemp
    Integer  relHumidity
    Double   windAvg
    Integer  windAvgDir
    String   windAvgHeading
    Double   windGust
    Integer  windGustDir
    String   windGustHeading
    Integer  visibility
    Integer  precip
    String   conditionCode
    String   conditionText
    String   roadSurface
}