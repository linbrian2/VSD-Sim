package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficAnomalyResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated
  
    Integer   deviceId
    String    direction
    Date      time
    Integer   speed
    Integer   occupancy
    Integer   volume
    Double    mdist
    Integer   status
    Double    severity
    Double    mdistL5
    Integer   statusL5
    Double    mdistL10
    Integer   statusL10
    Double    mdistL15
    Integer   statusL15
    Double    mdistX5
    Integer   statusX5
    Double    mdistX10
    Integer   statusX10
    Double    mdistX15
    Integer   statusX15
}
