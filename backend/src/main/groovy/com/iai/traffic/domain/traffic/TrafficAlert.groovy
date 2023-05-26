package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.*

@Entity
class TrafficAlert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id
    @DateCreated Date  dateCreated

	Date     updatedAt
    Date     receivedAt
    String   subject
    String   type
    String   county

    @Column(length = 1024)
    String   details

    @Column(length = 4096)
    String   effect

    @Column(length = 1024)
    String   impact
    String   status
    Double   latitude
    Double   longitude
}