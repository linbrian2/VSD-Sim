package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TravelTimeBaselineWoIncident {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date dateCreated
    String    linkId
    String    linkName
    String    timeOfDay
    Integer   timeIndex
    Integer   avgLinkSpeed
    Integer   stdLinkSpeed
    Integer   baseTravelTime
    Integer   meanTravelTime
    Integer   stdTravelTime
    Integer   maxTravelTime
    Integer   minTravelTime
    Integer   pc25TravelTime
    Integer   pc75TravelTime
    Integer   sampleSize
}