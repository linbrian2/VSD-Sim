package com.iai.traffic.domain.solution

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MitigationResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
     @DateCreated
    Date      dateCreated

    Long      incidentId
    Date      time

    Boolean   incidentFlag
    Boolean   onRampFlag
    Boolean   offRampFlag
    Boolean   travelTimeFlag
    Boolean   detourFlag
    Integer   totalFlags
    Integer   mitigationState
    Integer   signalChangeState

    String    detourRampIds

    Integer   onRampVolume
    Integer   offRampVolume
    Integer   onRampBaseline
    Integer   offRampBaseline
    Integer   travelTime
    Integer   travelTimeBaseline
    Integer   capacity
    Integer   demand
    String    pattern

    Boolean   live
}
