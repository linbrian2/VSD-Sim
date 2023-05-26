package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TravelSegmentNew {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    String    linkId
    String    shortName
    String    description
    String    direction
    String    route
    String    startPoint
    String    endPoint
    Integer   routeOrder
    Integer   baseSpeed
    Integer   freeFlowTime
    Float     freeFlowSpeed
    Float     distance
    String    coordinates
    String    stationId
    String    cameras
}