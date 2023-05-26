package com.iai.traffic.domain.vision

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficCamera {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated
	String    serialNo
	String    machineId
	String    location
	String    direction
	String    description

	String    ipAddr
	Integer   port

	Double    lat
	Double    lon

	Integer   idx

	String    name
	String    uri
	String    detectorId

	Integer   groupId
	Integer   enabled
}
