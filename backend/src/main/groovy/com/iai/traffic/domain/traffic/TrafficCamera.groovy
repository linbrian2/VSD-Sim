package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TrafficCamera {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id
	@DateCreated Date dateCreated
	String   cameraId
	String   title
	String   county
	Double   lat
	Double   lon
	String   rtmp
	String   rtsp
	String   f4m
	String   m3u8
	String   mssmooth
	boolean  enabled
}
