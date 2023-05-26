package com.iai.traffic.domain.anomaly

import java.util.Date
import javax.persistence.Entity
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import io.micronaut.data.annotation.DateCreated

@Entity
class AnomalyResult {
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
    Integer   code
    Double    severity
}
