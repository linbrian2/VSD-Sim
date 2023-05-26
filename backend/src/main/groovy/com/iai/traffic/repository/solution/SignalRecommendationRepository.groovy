package com.iai.traffic.repository.solution

import com.iai.traffic.domain.solution.SignalRecommendation
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository(value = 'solution')
interface SignalRecommendationRepository extends CrudRepository<SignalRecommendation, Long> {
    List<SignalRecommendation> findByGroupIdAndTimestampBetween(String groupId, Date startTime, Date endTime)
    List<SignalRecommendation> findByGroupIdAndTimestampBetweenOrderByTimestampAsc(String groupId, Date startTime, Date endTime)
    List<SignalRecommendation> findByPermitAndTimestampBetweenOrderByTimestampDesc(String permit, Date startTime, Date endTime)
    List<SignalRecommendation> findByPermitAndTimestampBetweenOrderByTimestampAsc(String permit, Date startTime, Date endTime)
}