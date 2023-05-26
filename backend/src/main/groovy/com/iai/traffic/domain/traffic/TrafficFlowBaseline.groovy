package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficFlowBaseline {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated
    @DateUpdated Date  dateUpdated
    Integer   deviceId
    String    direction
    Integer   weekday
    Integer   minute
    Float     avgSpeed
    Float     avgVolume
    Float     avgOccupancy
    Float     stdSpeed
    Float     stdVolume
    Float     stdOccupancy
    Integer   spdCount
    Integer   volCount
    Integer   occCount
}