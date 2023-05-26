package com.iai.traffic.services


import com.iai.traffic.LatLonDist
import com.iai.traffic.repository.traffic.TravelSegmentNewRepository
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class SegmentMatchingService {
    @Inject TravelSegmentNewRepository mTravelSegmentNewRepository
    @Inject TravelSegmentService mTravelSegmentService

    def findClosestSegment(double latitude, double longitude, double threshold) {
        def segments = findClosestSegments(latitude, longitude, threshold)
        if (segments.isEmpty()) {
            return null
        }
        return segments[0]
    }

    def findClosestSegments(double latitude, double longitude, double threshold) {
        def result = []
        mTravelSegmentService.getPaths().each { linkId, path->
            def dist = findMinDistance(latitude, longitude, path)
            if (dist < threshold) {
                result << [segment: mTravelSegmentService.getSegmentByLinkId(linkId), distance: (int)Math.round(dist)]
            }
        }
        // Sort by distance
        result.sort { it.distance }
        return result
    }

    def findDistanceToSegment(double latitude, double longitude, String linkId) {
        def path = mTravelSegmentService.getPathByLinkId(linkId)
        if (path) {
            return findMinDistance(latitude, longitude, path)
        }
        return -1
    }

    def getSegmentDistanceInMeters(def coordinates) {
        def total = 0
        for (int i = 1; i < coordinates.size(); i++) {
            def lat0 = coordinates[i - 1].lat
            def lon0 = coordinates[i - 1].lng
            def lat1 = coordinates[i + 0].lat
            def lon1 = coordinates[i + 0].lng
            total += LatLonDist.getDistance(lat0, lon0, lat1, lon1)
        }
        return total
    }

    def findMinDistance(double latitude, double longitude, def coordinates) {
        def mind = 10000000
        for (int i = 1; i < coordinates.size(); i++) {
            def lat0 = coordinates[i - 1].lat
            def lon0 = coordinates[i - 1].lng
            def lat1 = coordinates[i + 0].lat
            def lon1 = coordinates[i + 0].lng
            def d = LatLonDist.pointToLineDistance(lat0, lon0, lat1, lon1, latitude, longitude)
            if (d < mind) {
                mind = d
            }
        }
        return mind
    }
}