package com.iai.traffic.domain.traffic


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficResponseCycleLevel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	String   zoneId
	Integer  calculatorType
	Integer  levelNumber
	Integer  entryValue
	Integer  exitValue
}
