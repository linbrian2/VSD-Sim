package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class IncidentState {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated

    Long      incidentId
    Integer   deviceId
    String    direction
    Date      time
    String    laneBlockage
    Integer   capacityReduction
    Integer   detour

    Integer   timingPlanChange
}
