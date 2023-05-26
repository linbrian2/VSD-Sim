package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SignalCycleSplitOffset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id
    Integer  intersectionId
    String   groupId
    String   permit
    String   pattern
    Integer  dial
    Integer  split
    Integer  offset
    Integer  cycleLength
    Integer  offsetTime
    Integer  phase
    Integer  phaseTime
    Integer  phaseMode
}