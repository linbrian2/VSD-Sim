package com.iai.traffic.domain.traffic


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficResponsiveZone {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	String   zoneId
	String   zoneTitle
	Integer  channelNumber
	String   direction
	String   permitNumber
	String   fieldDeviceName
	String   detLane
	Integer  position
	Integer  deviceId
	Integer  secondaryDeviceId
	Float    weight
}
