package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.*

@Entity
class TrafficVision {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dataCreated
	Date      createdTime
	Integer   deviceId
	Integer   speed
	Integer   volume
	Integer   occupancy
	Integer   volCar
	Integer   volTruck
	Integer   volBus
	Integer   volMotor
	Integer   flowStatus
	String    video
}
