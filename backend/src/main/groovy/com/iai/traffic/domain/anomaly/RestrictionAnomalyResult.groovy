package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.*

@Entity
class RestrictionAnomalyResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated

    Long      restrictionId
    String    impactType

    Double    lat
    Double    lon

    String    segmentId
    Integer   distance

    Integer   startTime
    Integer   endTime

    @Column(columnDefinition="TEXT")
    String    description

    @Column(columnDefinition="TEXT")
    String    location
}
