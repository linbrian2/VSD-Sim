package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class WazeAnomalyResult {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated

    Date      time
    String    linkId
    Integer   alertType
    Integer   alertCount

    @Column(columnDefinition="TEXT")
    String    data
}
