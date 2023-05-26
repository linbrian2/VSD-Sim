package com.iai.traffic.domain.traffic


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficDevicePerLane {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	Integer  deviceId
	Integer  position
	String   bound
	String   name
	String   permitNumber
	Integer  stationId
	Integer  detectorId
	Double   latitude
	Double   longitude
	String   description
	Integer  sampleSize
}
