package com.iai.traffic.domain.anomaly

import java.util.Date
import javax.persistence.Entity
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import io.micronaut.data.annotation.DateCreated

@Entity
class AnomalyDevice {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id
    Integer   deviceId
    Integer   seq
    String    route
    String    direction
    String    upstream
    String    downstream
    String    segmentId
    Integer   zone
    Integer   enabled
}
