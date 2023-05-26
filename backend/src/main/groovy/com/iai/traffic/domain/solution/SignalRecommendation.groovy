package com.iai.traffic.domain.solution

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SignalRecommendation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id
	Date     timestamp
	String   groupId
	String   permit
	Integer  cms
	String   levelOfService
	Integer  lostTime
	Double   saturationDegree
	Integer  intersectionCycleLength
	Integer  arterialCycleLength
	Integer  cycleLength
	Double   progressionRatio
	String   offset
	String   pattern
	Integer  patternId
}
