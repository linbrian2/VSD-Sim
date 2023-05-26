package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class VisionCamera {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
	Integer   deviceId
	String    deviceName
	String    name
	String    uri
	String    mediaServer
	String    mapSrc
	String    mapDst

	Double    lat
	Double    lon

	Integer   minLimity
	Integer   maxLimity
	Integer   minSpeed
	Integer   maxSpeed

	String    clPos
	String    hash

	Integer   cutoff
	String    description
	byte[]    photo

	String    detectorId

	Integer   groupId
	Integer   enabled
}
