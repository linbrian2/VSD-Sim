package com.iai.traffic.domain.traffic

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class WeatherStation {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
	String   stationId
	String   title
	String   county
	Double   lat
	Double   lon
	String   woeid
	String   surfaceSensors
}
