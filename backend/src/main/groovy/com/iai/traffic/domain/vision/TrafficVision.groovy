package com.iai.traffic.domain.vision

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficVision {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated
	Date      createdTime
	Integer   deviceId
	Integer   speed
	Integer   volume
	Integer   occupancy

	Integer   volCoupe
	Integer   volSedan
	Integer   volLarge
	Integer   volSuv
	Integer   volTruck
	Integer   volVan

	Integer   flowStatus
	String    video
}
