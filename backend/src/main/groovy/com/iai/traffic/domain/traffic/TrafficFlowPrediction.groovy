package com.iai.traffic.domain.traffic



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import io.micronaut.data.annotation.DateCreated;

@Entity
class TrafficFlowPrediction {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
	@DateCreated Date  dateCreated
		
	Integer   deviceId
	String    direction
	Date      time          // prediction time
	Integer   method        // prediction algorithm
	
	Float     speed5
	Float     speed10
	Float     speed15
	
	Float     volume5
	Float     volume10
	Float     volume15
	
	Float     occupancy5
	Float     occupancy10
	Float     occupancy15
}
