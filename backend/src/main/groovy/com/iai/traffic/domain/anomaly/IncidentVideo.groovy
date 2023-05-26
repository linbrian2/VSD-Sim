package com.iai.traffic.domain.anomaly

import io.micronaut.data.annotation.DateCreated

import javax.persistence.*

@Entity
class IncidentVideo {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    
    @DateCreated 
    Date      dateCreated

    Long      incidentId
    String    cameraId
    Date      videoTime
    String    imageName
    String    videoName
    String    bucketName
    Integer   classification
}
