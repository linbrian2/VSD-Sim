package com.iai.traffic.domain.solution


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SimulationScenario {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    String    type
    String    scenarioName
    Integer   scenarioId
    Integer   incidentSegment
    String    direction
    Integer   numLaneClosure
    String    timeType
    String    timingPlanId
    String    note
    Double    moe
}
