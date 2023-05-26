package com.iai.traffic.domain.solution

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MitigationSolution {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
     @DateCreated
    Date      dateCreated

    Long      incidentId
    Date      evalTime
    Integer   timeOffset

    Integer   visumNode
    Integer   deviceId
    String    permitNumber

    String    signalGroup
    Integer   cycleLength
    Integer   offset
    String    los
    Integer   avgDelay
    String    phaseSplits
    Integer   bottleneckVolume
    Integer   totalDetourTravelTime

    Integer   solutionFlag
}
