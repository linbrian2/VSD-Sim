package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SignalPattern {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id
    @DateCreated Date  dateCreated
    String   groupId
    Date     timestamp
    String   pattern
}