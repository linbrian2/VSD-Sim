package com.iai.traffic.domain.traffic

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ReleaseNotes {
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
	String  versionNumber
	Date    buildTime
	String  notes
}
