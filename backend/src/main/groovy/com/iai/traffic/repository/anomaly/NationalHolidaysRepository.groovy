package com.iai.traffic.repository.anomaly

import com.iai.traffic.domain.anomaly.NationalHolidays
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'anomaly')
interface NationalHolidaysRepository extends CrudRepository<NationalHolidays, Long> {
}