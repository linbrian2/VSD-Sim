package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficFlowPerMinute {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated
	Date      createdTime

	Integer   deviceId
	Integer   position

	Float     speed
	Float     volume
	Float     occupancy
}
