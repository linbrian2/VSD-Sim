package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class MessageSign {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date dateCreated
	@DateUpdated Date dateUpdated

	Date       lastCreated
	Date       lastPolled
	Date       lastUpdated

	Long       systemId
	Integer    type
	String     typeName

	Double     lat
	Double     lon
	String     geoHash

	String     title
	String     message

	Boolean    enable
}
