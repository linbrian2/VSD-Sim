package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class RestrictionData {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated
    Date     dateCreated
    Long     restrictionId
    Date     startTime
    Date     endTime
    String   title
    String   impactType
    String   location
    Double   lat
    Double   lon
    String   impactedRoadway
    String   contractorName

    @Column(columnDefinition="TEXT")
    String   construction
}