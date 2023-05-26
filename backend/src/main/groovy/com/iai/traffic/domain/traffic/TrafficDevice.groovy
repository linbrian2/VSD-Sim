package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
class TrafficDevice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	Long     deviceId
	String   uid
	String   title
	String   county
	Double   lat
	Double   lon
	String   type
	Integer  stationCount
	String   directions
	String   lanes
	String   maxVolumeFiveMin
	String   maxVolumeOneHour
	String   sampleSize
	String   vph
	String   linkId
	Integer  flags
	Integer  region
	Integer  deviceType
}
