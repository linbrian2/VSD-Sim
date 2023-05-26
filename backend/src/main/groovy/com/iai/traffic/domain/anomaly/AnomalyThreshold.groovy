package com.iai.traffic.domain.anomaly

import java.util.Date
import javax.persistence.Entity
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import io.micronaut.data.annotation.DateCreated

@Entity
class AnomalyThreshold {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long      id
    Long      deviceId // combine device_id and direction
    Integer   weekday
    Integer   minute

    Integer   singular
    Integer   num
    String    cov

    Double    spd
    Double    vol
    Double    occ

    Double    maSpd
    Double    maVol
    Double    maOcc

    Double    sLq
    Double    sHq
    Double    sL
    Double    sH
    Double    sMin
    Double    sMax

    Double    vLq
    Double    vHq
    Double    vL
    Double    vH
    Double    vMin
    Double    vMax

    Double    oLq
    Double    oHq
    Double    oL
    Double    oH
    Double    oMin
    Double    oMax

    Double    mLq
    Double    mHq
    Double    mL
    Double    mH
    Double    mMin
    Double    mMax
}
