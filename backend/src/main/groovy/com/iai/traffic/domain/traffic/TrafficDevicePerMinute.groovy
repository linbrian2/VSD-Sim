package com.iai.traffic.domain.traffic


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficDevicePerMinute {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	Integer  deviceId
	Integer  position
	String   bound
	String   zoneName
	String   permitNumber
	Double   latitude
	Double   longitude
	String   description
}
