package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import io.micronaut.data.annotation.DateCreated;

@Entity
class TravelTimeBaseline {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;

    @DateCreated Date  dateCreated

    String linkId
    String timeOfDay

    Integer timeIndex
    Integer avgLinkSpeed
    Integer stdLinkSpeed
    Integer meanTravelTime
    Integer stdTravelTime
    Integer maxTravelTime
    Integer minTravelTime
    Integer pc25TravelTime
    Integer pc75TravelTime
}