package com.iai.traffic.domain.traffic


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficDeviceAttrs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	Long     deviceId
	String   uid
	String   direction
	String   title
	String   county
	Double   lat
	Double   lon
	String   type
	Integer  stationCount
	Integer  numLane
	Integer  maxVolumeFiveMin
	Integer  maxVolumeOneHour
	Integer  sampleSize
	Integer  vph
	String   linkId
	Integer  minDist
	String   cameras
	Integer  capacityFiveMin
	Integer  capacityHour
	Integer  capacityHourPerLane
	Integer  zone
	String   stationId
}
