package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficFlowQuality {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date dateCreated
	Date      createdTime
	Integer   deviceId
	String    direction
	Integer   speed
	Integer   volume
	Integer   occupancy
	Integer   flowStatus
	Integer   status
}
