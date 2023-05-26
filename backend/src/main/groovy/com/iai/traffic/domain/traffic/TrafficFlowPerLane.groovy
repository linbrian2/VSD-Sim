package com.iai.traffic.domain.traffic;

import io.micronaut.data.annotation.DateCreated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id
import javax.persistence.Table;
import java.util.Date;

@Entity
class TrafficFlowPerLane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated
    Date      dateCreated
    Date      createdTime
    Integer   zoneId
    Float     speed
    Float     volume
    Float     occupancy
    Integer   flowStatus
    String    permitNumber
}

