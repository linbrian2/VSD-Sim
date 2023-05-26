package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.SignalCycleSplitOffset
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface SignalCycleSplitOffsetRepository extends CrudRepository<SignalCycleSplitOffset, Long> {
    List<SignalCycleSplitOffset> findByPermit(String permit)
    List<SignalCycleSplitOffset> findByGroupId(String groupId)
}