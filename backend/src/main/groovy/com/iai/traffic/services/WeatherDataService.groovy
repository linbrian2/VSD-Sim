package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.LatLonDist
import com.iai.traffic.domain.traffic.WeatherData
import com.iai.traffic.repository.traffic.WeatherDataRepository
import com.iai.traffic.repository.traffic.WeatherStationRepository
import com.iai.traffic.utils.AppUtils
import com.iai.traffic.utils.Interpolation
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton
import java.text.SimpleDateFormat

@Singleton
@Slf4j
class WeatherDataService {
    @Inject WeatherDataRepository mWeatherDataRepository
    @Inject WeatherStationRepository mWeatherStationRepository

    def getWeatherStations() {
        def stations = mWeatherStationRepository.findAll()

        def result = [:]

        // Remove duplicates
        stations.each {
            def key = it.stationId
            def value = [
                    id:   it.stationId,
                    name: it.stationId + " - " + it.title,
                    woeid: it.woeid,
                    position: [lat: it.lat, lng: it.lon],
                    code: -1,
                    temp: -1000
            ]
            def item = result.get(key)
            result << [(key): value]
        }

        return result.values()
    }

    def getWeatherStationInfo(String stationId) {
        def station = mWeatherStationRepository.findByStationId(stationId)
        if (!station.isPresent()) {
            return [:]
        }
        
        def w = station.get()
        return [
                id:   w.stationId,
                title: w.title,
                county: w.county,
                woeid: w.woeid,
                position: [lat: w.lat, lng: w.lon]
        ]
    }

    def getHourlyWeatherData(String stationId, Date currentTime, int hours) {
        // Formulate time range
        Date endTime   = currentTime
        Date startTime = AppUtils.minusSeconds(endTime, hours * 3600)

        // Obtain data
        List<WeatherData> data = mWeatherDataRepository.findByStationIdAndDataReceivedBetween(stationId, startTime, endTime)

        // Only retrieve the whole hour data
        def result = [:]
        data.each {
            def time = it.dataReceived
            def temp = temperatureF(it.airTemp)
            def code = it.conditionCode
            def hour = AppUtils.getHourFromDate(time)
            def value = result.get(hour)
            if (value == null) {
                result << [(hour): [time: time, temp: temp, code: code]]
            } else if (time.before(value.time)) {
                value.time = time
                value.temp = temp
                value.code = code
            }
        }

        // Sort by time in desc order
        def hourlyWeather = result.values().sort { it.time }

        // Only keep 6 hours
        if (hourlyWeather.size() > 6) {
            for (int i = 0; i < (hourlyWeather.size() - 6); i++) {
                hourlyWeather.remove(0)
            }
        }

        return hourlyWeather
    }

    def getLatestWeatherData(String stationId, int secondsAgo) {
        def result = [:]

        // Formulate time range
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)

        // Obtain data
        List<WeatherData> data = mWeatherDataRepository.findByStationIdAndDataReceivedBetween(stationId, startTime, endTime)
        if (!data.isEmpty()) {
            // Get the last one row
            def item = data.last()
            result = composeWeatherData(item)

            // Get the last six hour weather
            def hourlyWeather = getHourlyWeatherData(stationId, endTime, 6)
            if (hourlyWeather.size() > 0) {
                result << [hourlyWeather: hourlyWeather]
            }
        }

        return result
    }

    def getLatestWeatherData(int secondsAgo) {
        // Get all weather stations
        def stationIds = mWeatherStationRepository.findAll().collect { it.stationId }

        // Get all data within time range
        Date endTime   = new Date()
        Date startTime = AppUtils.minusSeconds(endTime, secondsAgo)
        List<WeatherData> data = mWeatherDataRepository.findByStationIdInListAndDataReceivedBetween(stationIds, startTime, endTime)

        // Get latest
        def result = [:]
        data.each {
            def key = it.stationId
            def item = result.get(key)
            if ( item == null || it.dataReceived.after(item.time)) {
                result << [(key): [time: it.dataReceived, value: composeWeatherData(it)]]
            }
        }

        return result.values().collect {
            return [
                    stationId: it.value.stationId,
                    code: it.value.conditionCode,
                    temp: it.value.airTemp
            ]
        }
    }

    def composeWeatherData(WeatherData item) {
        def result = [:]
        result << [stationId: item.stationId]
        result << [dataReceived: item.dataReceived]
        result << [lastPollSuccessful: item.lastPollSuccessful]
        result << [airTemp: temperatureF(item.airTemp)]
        result << [maxTemp: temperatureF(item.maxTemp)]
        result << [minTemp: temperatureF(item.minTemp)]
        result << [dewPoint: item.dewPoint]
        result << [roadSurface: getRoadSurface(item.roadSurface)]
        result << [wetBulbTemp: temperatureF(item.wetBulbTemp)]
        result << [relHumidity: item.relHumidity]
        result << [windAvg: item.windAvg]
        result << [windAvgDir: item.windAvgDir]
        result << [windAvgHeading: item.windAvgHeading]
        result << [windGust: item.windGust]
        result << [windGustDir: item.windGustDir]
        result << [windGustHeading: item.windGustHeading]
        result << [visibility: item.visibility]
        result << [precip: item.precip]
        result << [sunrise: formatTime(item.sunrise)]
        result << [sunset: formatTime(item.sunset)]
        result << [conditionCode: item.conditionCode]
        result << [conditionText: item.conditionText]
        return result
    }

    def getRoadSurface(String roadSurface) {
        if (roadSurface == null || roadSurface.trim().length() == 0) {
            return 'N/A'
        }
        // Convert to JSON object
        ObjectMapper objectMapper = new ObjectMapper()
        def surfaces = objectMapper.readValue(roadSurface, new TypeReference<List<Integer>>(){}) as List
        def SURFACE_CODES = [
            'Not Available',
            'Other',
            'Error',
            'Dry',
            'Trace Moisture',
            'Wet',
            'Chemically wet',
            'Ice Warning',
            'Ice watch',
            'Snow warning',
            'Snow watch',
            'Absortion',
            'Dew',
            'Frost',
            'Absorption'
        ]

        if (surfaces.size() > 0) {
            def idx = surfaces[0]
            if (idx > 2 && idx < SURFACE_CODES.size()) {
                return SURFACE_CODES[idx]
            }
        }

        return 'N/A'
    }

    def temperatureF(def temp) {
        if (temp == null) {
            return null
        }
        return Math.round((temp * 0.01 * 9/5.0 + 32.0) * 100) / 100
    }

    def formatTime(Date time) {
        def df = new SimpleDateFormat("hh:mm aa")
        return df.format(time)
    }

    def getWeatherData(String stationId, Date dayTime) {
        Date startTime = AppUtils.getStartOfDay(dayTime)
        Date endTime   = AppUtils.getEndOfDay(dayTime)
        return getWeatherData(stationId, 300, startTime, endTime)
    }

    def getWeatherData(String stationId, int interval, Date startTime, Date endTime) {
        // Limit the date range within two weeks
        long days = AppUtils.getDays(startTime, endTime)
        if (days > 14) {
            endTime = AppUtils.addDays(startTime, 14)
        }

        // Get weather data
        List<WeatherData> data = mWeatherDataRepository.findByStationIdAndDataReceivedBetween(stationId, startTime, endTime)
        if (data.isEmpty()) {
            return [:]
        }

        def collectedData = data.collect {
            return [
                    time: it.dataReceived,
                    airTemp: temperatureF(it.airTemp),
                    relHumidity: it.relHumidity,
                    windAvg: it.windAvg,
                    visibility: it.visibility,
                    precip: it.precip
            ]
        }

        def airTempL = []
        def relHumidityL = []
        def windAvgL = []
        def visibilityL = []
        def precipL = []

        // Collect separate array list
        collectedData.each {d->
            def t = d.time.getTime()
            if (d.airTemp != null) {
                airTempL << [t, d.airTemp]
            }
            if (d.relHumidity != null) {
                relHumidityL << [t, d.relHumidity]
            }
            if (d.windAvg != null) {
                windAvgL << [t, d.windAvg]
            }
            if (d.visibility != null) {
                visibilityL << [t, d.visibility]
            }
            if (d.precip != null) {
                precipL << [t, d.precip]
            }
        }

        if (interval <= 300) {
            return  [
                    airTemp: airTempL,
                    relHumidity: relHumidityL,
                    windAvg: windAvgL,
                    visibility: visibilityL,
                    precip: precipL
            ]
        }

        // Accumulate data based on interval
        def interp = new Interpolation()
        return  [
                airTemp: interp.averageArrayList(airTempL, interval, 1),
                relHumidity: interp.averageArrayList(relHumidityL, interval, 1),
                windAvg: interp.averageArrayList(windAvgL, interval, 1),
                visibility: interp.averageArrayList(visibilityL, interval, 1),
                precip: interp.averageArrayList(precipL, interval, 1)
        ]
    }
}
