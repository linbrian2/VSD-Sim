package com.iai.traffic.domain.anomaly

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficLcmParam {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long      id

    Integer   deviceId
    String    direction

    Integer   type

    Double    vf
    Double    spdC
    Double    volC
    Double    spdA
    Double    volA

    Double    r
    Double    t
    Double    l

    Double    a2
    Double    b2
    Double    c2
    Double    d2

    Double    a3
    Double    b3
}
