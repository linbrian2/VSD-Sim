package com.iai.traffic.services

import com.iai.traffic.domain.anomaly.RestrictionAnomalyResult
import com.iai.traffic.domain.traffic.RestrictionData
import com.iai.traffic.domain.traffic.TravelAdvisory
import com.iai.traffic.domain.traffic.WeatherData
import com.iai.traffic.repository.anomaly.RestrictionAnomalyResultRepository
import com.iai.traffic.repository.traffic.RestrictionDataRepository
import com.iai.traffic.repository.traffic.TravelAdvisoryRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Constants
import groovy.util.logging.Slf4j
import io.micronaut.data.annotation.DateCreated

import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.Column
import java.text.SimpleDateFormat

@Singleton
@Slf4j
class RestrictionDataService {
    @Inject RestrictionDataRepository mRestrictionDataRepository
    @Inject TravelAdvisoryRepository mTravelAdvisoryRepository
    @Inject RestrictionAnomalyResultRepository mRestrictionAnomalyResultRepository

    def getLatestEffectiveRestrictionData(int secondsAgo) {
        Date endTime = new Date()
        Date startDay = AppUtils.getStartOfDay(endTime)
        List<RestrictionAnomalyResult> restrictionList = mRestrictionAnomalyResultRepository.findByDateCreatedBetweenOrderByDateCreatedDesc(startDay, endTime)

        if (restrictionList.isEmpty()) {
            return []
        }

        // Filter by startMinute and endMinute
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        def startMinute = AppUtils.getOneMinIdx(startTime)
        def filteredList = restrictionList.findAll {it.startTime < startMinute && startMinute < it.endTime }
        if (filteredList.isEmpty()) {
            return []
        }

        // Combine restriction data by location
        def combinedResult = [:]
        filteredList.each { r->
            def key = r.lat + '-' + r.lon
            def item = combinedResult.get(key)
            if (item == null) {
                combinedResult << [(key): [r]]
            } else {
                def foundObject = item.find {t-> isSentenceEqual(t.description, r.description) }
                if (foundObject == null) {
                    item << r
                }
            }
        }

        def result = []
        combinedResult.each { k,v->
            def value = composeRestrictionAnomalyItem(v[0], startDay)
            if (v.size() > 1) {
                def sb = new StringBuilder()
                for (int i = 1; i < v.size(); i++) {
                    sb.append('<br/><br/>').append(v.get(i).description)
                }
                value.description += sb.toString()
            }

            result << value
        }

        return result
    }

    def composeRestrictionAnomalyItem(def res, Date startDay) {
        def startTime = AppUtils.addSeconds(startDay, res.startTime * 60)
        def endTime = AppUtils.addSeconds(startDay, res.endTime * 60)

        def data = [:]
        data << [id: res.id]
        data << [time: res.dateCreated]
        data << [startTime: startTime]
        data << [endTime: endTime]
        data << [impactType: res.impactType]
        data << [restrictionId: res.restrictionId]
        data << [description: res.description]
        data << [dist: res.distance]
        data << [location: res.location]
        data << [position: [lat: res.lat, lng: res.lon]]

        return data
    }

    def isSentenceEqual(String sentence1, String sentence2) {
        return sentence1.replaceAll("\\s+","").equalsIgnoreCase(sentence2.replaceAll("\\s+",""))
    }

    def getLatestRestrictionData(int secondsAgo) {
        // Get all data within time range
        Date endTime = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        def restrictions = getRestrictionData(startTime, endTime)
        def travelAdvisory = getTravelAdvisoryData(startTime, endTime)

        // Combine two maps
        def combinedData = restrictions + travelAdvisory

        // Convert to list
        def combinedList = combinedData.values()

        // Sort by time in desc order
        def sortedList = combinedList.sort{ it.time }
        combinedList = sortedList.reverse()

        // Combine same location
        def combinedResult = [:]
        combinedList.each { r->
            def key = r.position.lat + '-' + r.position.lon
            def item = combinedResult.get(key)
            if (item == null) {
                def values = []
                values << r
                combinedResult << [(key): values]
            } else {
                def foundObject = item.find { t-> t.desc.equals(r.desc) }
                if (foundObject == null) {
                    item << r
                }
            }
        }

        // Generate the final data
        def result = []
        combinedResult.each { k,v->
            def value = [:]
            value << [id: v[0].id]
            value << [name: v[0].name]
            value << [position: v[0].position]
            value << [data: v]
            result << value
        }

        return result
    }

    def getRestrictionData(Date startTime, Date endTime) {
        // Get all data within time range
        List<RestrictionData> data = mRestrictionDataRepository.findData(endTime, 200)

        // Get latest
        def result = [:]
        def df = new SimpleDateFormat("MMMM dd, yyyy")
        data.each {
            def desc = it.construction
            if (desc.getAt(desc.length() - 1) != '.') {
                desc += "."
            }
            desc += "<br /><br />Effective until ${df.format(it.endTime)}."

            def value = [:]
            value << [id: it.restrictionId]
            value << [time: it.dataReceived]
            value << [name: it.impactType]
            value << [desc: desc]
            value << [position: [lat: it.lat, lng: it.lon]]
            result << [( 'R' + it.restrictionId): value]
        }

        return result
    }

    def getTravelAdvisoryData(Date startTime, Date endTime) {
        // Get all data within time range
        List<TravelAdvisory> data = mTravelAdvisoryRepository.findByDateCreatedBetweenOrderByDateCreatedDesc(startTime, endTime)

        // Get latest
        def result = [:]
        data.each {
            def value = [:]
            value << [id: it.id]
            value << [time: it.dateCreated]
            value << [name: it.incidentType]
            value << [desc: it.problem]
            value << [position: [lat: it.lat, lng: it.lon]]
            result << [('A' + it.id): value]
        }

        return result
    }
}
