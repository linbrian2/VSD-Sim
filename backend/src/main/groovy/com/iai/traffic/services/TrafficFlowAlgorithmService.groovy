package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.traffic.TrafficDevice
import com.iai.traffic.repository.traffic.TrafficBreakdownParamRepository
import com.iai.traffic.repository.traffic.TrafficDeviceRepository
import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class TrafficFlowAlgorithmService {
    @Inject TrafficDeviceRepository mTrafficDeviceRepository
    @Inject TrafficBreakdownParamRepository mTrafficBreakdownParamRepository

    def getTrafficBreakdownParam(int deviceId, String direction) {
        if (direction == null) {
            return []
        }
        def param =  mTrafficBreakdownParamRepository.findByDeviceIdAndDirection(deviceId, direction)
        if (param.isPresent()) {
            def p = param.get()
            return [p.p1, p.p2, p.p3]
        } else {
            return []
        }
    }

    def getMaxVolumeFiveMin(Integer deviceId, String direction) {
        Optional<TrafficDevice> deviceOpt = mTrafficDeviceRepository.findByDeviceId((long)deviceId)
        if (!deviceOpt.isPresent()) {
            return null
        }

        def device = deviceOpt.get()
        return getMaxVolumeFiveMin(device, direction)
    }

    def getMaxVolumeFiveMin(TrafficDevice device, String direction) {
        def maxVolumeFiveMin = getNumbers(device.maxVolumeFiveMin)
        if (maxVolumeFiveMin == null) {
            return null
        }
        def directions = getDirections(device.directions)

        def idx = directions.findIndexOf {it.equals(direction)}
        if (idx < 0) {
            return null
        }

        return maxVolumeFiveMin[idx]
    }

    def getVolPlusOccValue(Integer deviceId, String direction, Integer volume, Integer occupancy) {
        def maxVolumeFiveMin = getMaxVolumeFiveMin(deviceId, direction)
        if (maxVolumeFiveMin == null) {
            return 0
        }
        return Math.round(volume * 100/maxVolumeFiveMin + occupancy)
    }

    def getVolPlusOccValue(TrafficDevice device, String direction, Integer volume, Integer occupancy) {
        def maxVolumeFiveMin = getMaxVolumeFiveMin(device, direction)
        if (maxVolumeFiveMin == null) {
            return 0
        }
        return Math.round(volume * 100/maxVolumeFiveMin + occupancy)
    }

    def getVolPlusOccValue(Integer maxVolumeFiveMin, Integer volume, Integer occupancy) {
        if (maxVolumeFiveMin == null) {
            return 0
        }
        return Math.round(volume * 100/maxVolumeFiveMin + occupancy)
    }

    def getTrafficBreakdownProbability(Integer deviceId, String direction, Integer volume) {
        def params = getTrafficBreakdownParam(deviceId, direction)
        if (params.size() != 3) {
            return null
        }
       return genlogisticCdfDfvol(volume, params)
    }

    def getTrafficBreakdownParams() {
        return mTrafficBreakdownParamRepository.findAll().collect {
            return [
                    id:   it.deviceId,
                    direction: it.direction,
                    name: it.deviceId + " - " + it.title,
                    params: [it.p1, it.p2, it.p3]
            ]
        }.sort {it.id }
    }

    def genlogisticCdfDfvol(Integer volume, def params) {
        float p1 = (float)params[0]
        float p2 = (float)params[1]
        float p3 = (float)params[2]
        float y  = (volume - p2) / p3
        float cdf = (float)(1.0f / Math.pow((1.0f + Math.pow(2.718281, 0 - y)), p1))
        return round2(cdf * 100)
    }

    def getTrafficStatusColorByLevel(int level) {
        def colors = ['#339900','#00ff33','#ffff00','#ffc031','#fa7a28','#ff0000','#c00000']
        return colors[level]
    }

    def getTrafficStatusLevelByOccupancy(int occupancy) {
        int level = 0;
        if (occupancy <= 8) level = 0
        else if (occupancy <= 15) level = 1
        else if (occupancy <= 25) level = 2
        else if (occupancy <= 35) level = 3
        else if (occupancy <= 45) level = 4
        else if (occupancy <= 95) level = 5
        else level = 6

        return level
    }

    def getTrafficStatusByOccupancy(int occupancy) {
        def status = 'No Delay'
        if (occupancy < 25) {
            status = 'No Delay'
        } else if (occupancy < 35) {
            status = 'Minor Delay'
        } else if (occupancy < 95) {
            status = 'Major Delay'
        } else {
            status = 'Stop and Go'
        }
        return status
    }

    def round2(def value) {
        return Math.round(value * 100) / 100
    }

    def getDirections(String directions) {
        if (directions == null) {
            return []
        }

        ObjectMapper objectMapper = new ObjectMapper()
        return objectMapper.readValue(directions, new TypeReference<List<String>>(){})

        // Sort directions
//        def result = []
//        ['NB','SB','EB','WB'].each { dir->
//            if (directionList.contains(dir)) {
//                result << dir
//            }
//        }
//        return result
    }

    def getNumbers(String values) {
        if (values?.trim()) {
            ObjectMapper objectMapper = new ObjectMapper()
            return objectMapper.readValue(values, new TypeReference<List<Integer>>() {})
        } else {
            return null
        }
    }
}
