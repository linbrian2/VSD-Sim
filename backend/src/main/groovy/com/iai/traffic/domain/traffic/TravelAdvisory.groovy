package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import io.micronaut.data.annotation.DateCreated;

@Entity
class TravelAdvisory {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated

    Date     createdTime
    String   incidentType
    String   county
    String   location
    Double   lat
    Double   lon
    String   impact

    @Column(columnDefinition="TEXT")
    String   effect
    @Column(columnDefinition="TEXT")
    String   problem
}