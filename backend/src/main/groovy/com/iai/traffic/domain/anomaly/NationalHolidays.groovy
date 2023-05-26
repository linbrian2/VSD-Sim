package com.iai.traffic.domain.anomaly

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class NationalHolidays {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)  
	Long      id
    Date      time
    Integer   code
    Integer   level
    String    description
}
