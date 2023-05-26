package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated

import javax.persistence.*

@Entity
class TrafficIncidentResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated
    Date      dateCreated

    @DateUpdated
    Date      dateUpdated

    String    segmentId
    String    segments
    String    direction
    String    route
    Integer   region

    String    location
    Double    lat
    Double    lon
    String    address
    String    detectors
    Integer   method

    Date      startTime
    Date      endTime
    Integer   duration

    Integer   incidentType
    String    explanation
    Integer   status

    Integer   severity
    Integer   evidenceCount

    @Column(columnDefinition="TEXT")
    String    trafficFlow

    @Column(columnDefinition="TEXT")
    String    travelTime

    @Column(columnDefinition="TEXT")
    String    waze

    @Column(columnDefinition="TEXT")
    String    restriction

    @Column(columnDefinition="TEXT")
    String    weather

    @Column(columnDefinition="TEXT")
    String    alerts
}
