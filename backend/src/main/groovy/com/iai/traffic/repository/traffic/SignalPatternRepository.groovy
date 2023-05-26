package com.iai.traffic.repository.traffic

import com.iai.traffic.domain.traffic.SignalPattern
import groovy.transform.CompileStatic
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@CompileStatic
@Repository
interface SignalPatternRepository extends CrudRepository<SignalPattern, Long> {
    List<SignalPattern> listFirstOrderByDateCreatedDesc()
    List<SignalPattern> findFirstByGroupIdAndTimestampBeforeOrderByTimestampDesc(String groupId, Date timestamp)
    List<SignalPattern> findByGroupIdAndTimestampBetweenOrderByTimestampDesc(String groupId, Date startTime, Date endTime)
    List<SignalPattern> findByGroupIdAndTimestampBetweenOrderByTimestampAsc(String groupId, Date startTime, Date endTime)
}