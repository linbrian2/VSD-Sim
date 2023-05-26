package com.iai.traffic.domain.solution


import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TimingPlan {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
    Long      id

    String    planId
    Integer   intId
    Integer   s1
    Integer   s2
    Integer   s3
    Integer   s4
    Integer   s5
    Integer   s6
    Integer   s7
    Integer   s8
    Integer   cl
    Integer   off
}
