package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BluetoothAnomalyResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated

    String    linkId
    String    timeOfDay
    Date      time
    Integer   travelTime
    Float     avgSpeed
    Integer   status
}
