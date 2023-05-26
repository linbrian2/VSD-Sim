package com.iai.traffic.domain.traffic

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficBreakdownParam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
	Integer  deviceId
	String   direction
	String   route
	Double   p1
	Double   p2
	Double   p3
}
