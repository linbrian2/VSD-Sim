package com.iai.traffic.domain.solution

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class DetourRamp {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    String    incidentDir
    Double    lat1
    Double    lon1
    Double    lat2
    Double    lon2

    String    offRamp
    String    offRampDir

    String    onRamp
    String    onRampDir

    String    freeway
    String    arterial

    Integer   arterialDetectorId
    String    arterialDetectorBound

    Integer   highwayDetectorId
    String    highwayDetectorBound

    @Column(columnDefinition="TEXT")
    String    signalDevices

    @Column(columnDefinition="TEXT")
    String    coordinates
}
