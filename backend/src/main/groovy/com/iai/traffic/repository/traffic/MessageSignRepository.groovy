package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.MessageSign
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface MessageSignRepository extends CrudRepository<MessageSign, Long> {
    List<MessageSign> listFirstOrderByDateCreatedDesc()
    List<MessageSign> findByType(int type)
    Optional<MessageSign> findByGeoHash(String geoHash)
}