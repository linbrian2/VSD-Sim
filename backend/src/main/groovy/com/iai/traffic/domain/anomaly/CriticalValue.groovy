package com.iai.traffic.domain.anomaly

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class CriticalValue {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
	Long      id
    Long      deviceId // combine device_id and direction
    Integer   type
    Double    spd
    Double    vol
    Double    occ
}
