package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.anomaly.AnomalyDevice
import com.iai.traffic.domain.anomaly.AnomalyResult
import com.iai.traffic.domain.anomaly.AnomalyThreshold
import com.iai.traffic.domain.anomaly.CriticalValue
import com.iai.traffic.domain.traffic.TrafficFlow
import com.iai.traffic.repository.*
import com.iai.traffic.repository.anomaly.AnomalyThresholdRepository
import com.iai.traffic.repository.anomaly.CriticalValueRepository
import com.iai.traffic.repository.anomaly.NationalHolidaysRepository
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class AnomalyThresholdService {
    @Inject AnomalyThresholdRepository mAnomalyThresholdRepository
    @Inject CriticalValueRepository mCriticalValueRepository
    @Inject NationalHolidaysRepository mNationalHolidaysRepository

    def mNationalHolidays = new HashSet<Date>()

    def getParameters(Integer deviceId, String direction, Date time) {
        def result = null
        def threshold = getAnomalyThreshold(deviceId, direction, time)
        def critical =  getCriticalValue(deviceId, direction)
        if (threshold != null && critical != null) {
            result = composeDecisionParameters(threshold, critical)
        }
        return result
    }

    def getAnomalyThreshold(Integer deviceId, String direction, Date time) {
        def t = getTimeValues(time)
        List<AnomalyThreshold> data = mAnomalyThresholdRepository.find(getId(deviceId, direction), t.weekday, t.minute)
        if (data.size() == 0) {
            return null
        }
        AnomalyThreshold result = data.get(0)
        if (result.singular || result.cov == null || result.mMax == 0) {
            return null
        }
        return result
    }

    def getAnomalyThresholdList(Integer deviceId, String direction, Date time) {
        def t  = getTimeValues(time)
        def id = getId(deviceId, direction)
        def data =  mAnomalyThresholdRepository.find(id, t.weekday)
        return data.collect { d->
            return [ minute: HHMM2MinuteIdx(d.minute), speed: d.spd, volume: d.vol, occupancy: d.occ]
        }
    }

    def getCriticalValue(Integer deviceId, String direction) {
        def DAYS = [ NORMAL: 0, LIGHT_RAIN: 1, MODERATE_RAIN: 2, HEAVY_RAIN: 3, SNOW: 4, HOLIDAYS: 5]
        List<CriticalValue> result = mCriticalValueRepository.find((Long)getId(deviceId, direction), DAYS.NORMAL)
        return result?.size() ? result[0]: null
    }

    def composeDecisionParameters(AnomalyThreshold threshold, CriticalValue criticalValue) {
        def result = [:]

        result << [mCutoff: 3.5]
        result << [mH: threshold.mH]
        result << [mHQ: threshold.mHq]

        result << [sC: criticalValue.spd]
        result << [sLQ: threshold.sLq]
        result << [sHQ: threshold.sHq]
        result << [sL: threshold.sL]
        result << [sH: threshold.sH]

        result << [vC: criticalValue.vol]
        result << [vLQ: threshold.vLq]
        result << [vHQ: threshold.vHq]
        result << [vL: threshold.vL]
        result << [vH: threshold.vH]

        result << [oC: criticalValue.occ]
        result << [oLQ: threshold.oLq]
        result << [oHQ: threshold.oHq]
        result << [oL: threshold.oL]
        result << [oH: threshold.oH]

        return result
    }

    def getId(Integer deviceId, String direction) {
        def DIR_MAP = ['NB': 1, 'EB': 2, 'SB': 3, 'WB': 4]
        if (DIR_MAP.containsKey(direction)) {
            return (deviceId * 10 + DIR_MAP[direction])
        } else {
            log.error("Direction ${direction} is not found for device ${deviceId}")
            return -1
        }
    }

    def getTimeValues(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)

        def holiday = isHoliday(time)

        // Weekday: Holiday = 0, Sunday = 1, Monday = 2, ...  Saturday = 7
        int weekday = holiday ? 0 : calendar.get(Calendar.DAY_OF_WEEK)

        // Minute in 5-minute interval
        int minute = AppUtils.getTimeMinuteInHHMM(time)

        return [weekday: weekday, holiday:holiday, minute: minute]
    }

    def isHoliday(Date time) {
        if (mNationalHolidays.isEmpty()) {
            mNationalHolidaysRepository.findAll().each {
                mNationalHolidays.add(it.time)
            }
        }

        def startTime = AppUtils.getStartOfDay(time)
        return mNationalHolidays.contains(startTime)
    }

    def HHMM2MinuteIdx(int hhmm) {
        int hour = (int)Math.floor(hhmm/100)
        int minute = (hhmm - hour * 100)

        // Round down to 5-minute interval
        if (minute % 5 != 0) {
            minute = 5 * Math.floor(minute / 5)
        }

        int index = (int)Math.floor(hour * 60 + minute)
        return index
    }
}
