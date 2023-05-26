package com.iai.traffic.domain.traffic

import io.micronaut.data.annotation.DateCreated

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TravelTimeNew {
    @Id	@GeneratedValue(strategy = GenerationType.IDENTITY)	Long id;
    @DateCreated Date  dateCreated
    String    linkId
    Date      calculationTimestamp
    Date      dataStartTimestamp
    Date      dataEndTimestamp
    Integer   minTravelTimeSecs
    Integer   maxTravelTimeSecs
    Integer   meanTravelTimeSecs
    Float     speedMph
    Integer   traversalCount
    Integer   outlierCount
    Integer   travelTimeTrend
    Integer   score
    Integer   iqrSeconds
    Integer   pctl25thSecs
    Integer   pctl75thSecs
    Integer   pctl95thSecs
    Float     waitTimeDelta
    Integer   currentWaitSecs
    Integer   actualWaitSecs
    Integer   freeFlowRunTimeSecs
}