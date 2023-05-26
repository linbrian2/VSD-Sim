package com.iai.traffic.domain.traffic

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import io.micronaut.data.annotation.DateCreated;

@Entity
class WazeAlert {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated

    Date      updatedAt

    Integer   roadType
    Integer   alertType
    Integer   subtype

    Integer   reportRating
    Integer   confidence
    Integer   reliability
    Integer   thumbsUp

    Double    latitude
    Double    longitude
    Integer   driverHeading
    String    driverBearing
    String    address

    @Column(columnDefinition="TEXT")
    String    description

    def String toString() {
        String str = "Id: " + id + ", dateCreated: " + dateCreated + ", updatedAt: " + updatedAt +  ", driverBearing: " + driverBearing + 
                     ", address: " + address + ", description: " + description + ", latitude: " + latitude +
                     ", longitude: " + longitude + ", alertType: " + alertType + ", subtype: " + subtype + ", roadType: " + roadType +
                     ", confidence: " + confidence + ", reliability: " + reliability + ", thumbsUp: " + thumbsUp + 
                     ", reportRating: " + reportRating + ", driverHeading: " + driverHeading;
        return str
    }
}