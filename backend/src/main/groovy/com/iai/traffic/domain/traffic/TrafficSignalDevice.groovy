package com.iai.traffic.domain.traffic

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficSignalDevice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id
	String   permit
	String   name
	String   ip
	Double   lat
	Double   lon
	String   groupId
	String   groupName
	String   controllerVersion
	Integer  intersectionId
	Integer  parentZoneId
	Integer  numLanes
	Integer  enabledTr
	Integer  enabledNtcip
	Integer  region
}