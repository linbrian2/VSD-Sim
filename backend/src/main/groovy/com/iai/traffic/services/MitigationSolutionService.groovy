package com.iai.traffic.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.iai.traffic.domain.anomaly.TrafficIncidentResult
import com.iai.traffic.domain.solution.DetourRamp
import com.iai.traffic.domain.solution.MitigationSolution
import com.iai.traffic.domain.traffic.TrafficDevice
import com.iai.traffic.repository.anomaly.TrafficIncidentResultRepository
import com.iai.traffic.repository.solution.*
import com.iai.traffic.repository.traffic.TrafficDeviceRepository
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class MitigationSolutionService {
    @Inject TimingPlanRepository mTimingPlanRepository
    @Inject SimulationScenarioRepository mSimulationScenarioRepository
    @Inject DetourRampRepository mDetourRampRepository
    @Inject MitigationResultRepository mMitigationResultRepository
    @Inject MitigationSolutionRepository mMitigationSolutionRepository
    @Inject TrafficIncidentResultRepository mTrafficIncidentResultRepository
    @Inject TrafficIncidentDataService mTrafficIncidentDataService
    @Inject TrafficFlowDataService mTrafficFlowDataService
    @Inject TrafficDeviceRepository mTrafficDeviceRepository

    def getTimingPlan(String planId) {
        return mTimingPlanRepository.findByPlanId(planId)
    }

    def getSimulationScenarios(String type) {
        def scenarios = mSimulationScenarioRepository.findByType(type)

        def result = []
        scenarios.each {
            def planId = it.timingPlanId
            if (planId != null) {
                def timingPlan = mTimingPlanRepository.findByPlanId(planId)
                result << [planId: planId, moe: it.moe, timingPlan: timingPlan]
            }
        }
        return result
    }

    def getMitigationSolution(Long incidentId, Date startTime) {
        def solutions = mMitigationSolutionRepository.findByIncidentIdAndEvalTime(incidentId, startTime)
        return solutions
    }

    def getDetourRampInfo(Long incidentId) {
        Optional<TrafficIncidentResult> opt = mTrafficIncidentResultRepository.findById(incidentId)
        if (!opt.isPresent()) {
            return null
        }

        TrafficIncidentResult incident = opt.get()
        if (incident.lat == null || incident.lon == null) {
            return null
        }

        return mDetourRampRepository.findData(incident.direction, incident.lat, incident.lon)
    }

    def getMitigationSolution(Long incidentId) {
        def incident = mTrafficIncidentDataService.getIncidentData(incidentId, false, false)
        def ramps = mDetourRampRepository.findData(incident.direction, incident.location.lat, incident.location.lng)

        // Transform coordinates to polyline path
        ObjectMapper objectMapper = new ObjectMapper()
        def coordinates = objectMapper.readValue(ramps.first().coordinates, new TypeReference<List<List<Double>>>() {})
        def path = coordinates.collect { coord ->
            return [lat: coord[0], lng: coord[1]]
        }

        def mitigation = composeMitigationResult(incidentId, ramps)
        def solutions = composeMitigationSolutions(incidentId)

        def ramp = ramps.first()
        def signals = getSignalDevices(ramp)

        return [
                incident: incident,
                detourRoute: path,
                ramp: ramp,
                signals: signals,
                mitigation: mitigation,
                baseline: solutions.baseline,
                solutions: solutions.solutions,
                optimalCycleLength: solutions.optimalCycleLength
        ]
    }

    def getMitigationRampVolumes(Long incidentId) {
        Optional<TrafficIncidentResult> opt = mTrafficIncidentResultRepository.findById(incidentId)
        if (!opt.isPresent()) {
            return []
        }
        def incident = opt.get()
        def ramps = mDetourRampRepository.findData(incident.direction, incident.lat, incident.lon)
        def rampVolumes = composeRampVolumes(ramps, incident.startTime, incident.endTime)
        return rampVolumes
    }

    def composeMitigationSolutions(Long incidentId) {
        def solutionList = mMitigationSolutionRepository.findByIncidentId(incidentId)

        def optimalSolution = solutionList.find{it.solutionFlag == 2 }
        def optimalCycleLength = optimalSolution != null ? optimalSolution.cycleLength : -1
        def baseline = composeMitigationBaseline(solutionList)
        def solutions = composeMitigationSolution(solutionList)

        return [baseline: baseline, solutions: solutions, optimalCycleLength:optimalCycleLength]
    }

    def composeMitigationBaseline(def solutions) {
        def result = [:]
        solutions.each {
            if (it.solutionFlag == 0) {
                def time = it.evalTime
                def timestamp = time.getTime()

                // First level: evalTime
                def items = result.get(timestamp)
                if (items == null) {
                    items = [:]
                    result << [(timestamp): items]
                }

                // Second level: timeOffset
                def timeOffset = it.timeOffset
                def offsets = items.get(timeOffset)
                if (offsets == null) {
                    offsets = []
                    items << [(timeOffset): offsets]
                }

                offsets << createSolutionItem(it)
            }
        }

        return result
    }

    def composeMitigationSolution(def solutions) {
        def result = [:]
        solutions.each {
            if (it.solutionFlag > 0) {
                def time = it.evalTime
                def timestamp = time.getTime()

                // First level: evalTime
                def items = result.get(timestamp)
                if (items == null) {
                    items = [:]
                    result << [(timestamp): items]
                }

                // Second level: timeOffset
                def timeOffset = it.timeOffset
                def offsets = items.get(timeOffset)
                if (offsets == null) {
                    offsets = [:]
                    items << [(timeOffset): offsets]
                }

                // Third level: cycleLength
                def cycleLength = it.cycleLength
                def cycles = offsets.get(cycleLength)
                if (cycles == null) {
                    cycles = []
                    offsets << [(cycleLength): cycles]
                }

                cycles << createSolutionItem(it)
            }
        }

        return result
    }

    def createSolutionItem(MitigationSolution solution) {
        def item = [:]
        item << [id: solution.id]
        item << [permitNumber: solution.permitNumber]
        item << [signalGroup: solution.signalGroup]
        item << [offset: solution.offset]
        item << [avgDelay: solution.avgDelay]
        item << [bottleneckVolume: solution.bottleneckVolume]
        item << [totalDetourTravelTime: solution.totalDetourTravelTime]
        item << [los: solution.los]
        item << [cycleLength: solution.cycleLength]
        item << [timeOffset: solution.timeOffset]
        item << [flag: solution.solutionFlag]

        def shifts = createPhaseShifts(solution.phaseSplits)
        item.putAll(shifts)

        return item
    }

    def createPhaseShifts(String phaseSplits) {
        def result = [:]

        ObjectMapper objectMapper = new ObjectMapper()

        def splits = objectMapper.readValue(phaseSplits, HashMap.class)
        for (int i = 1; i <= 8; i++) {
            def phase = splits.get(i + '')
            def value = phase != null ? phase.get('split') : '/'
            result << [('P' + i): value]
        }

        return result
    }

    def composeMitigationResult(Long incidentId, List<DetourRamp> ramps) {
        def data = mMitigationResultRepository.findByIncidentId(incidentId)

        // Sort by time
        data.sort { it.time }

        def onRampActual = []
        def onRampBaseline = []
        def offRampActual = []
        def offRampBaseline = []
        def travelTimeActual = []
        def travelTimeBaseline = []
        def capacity = []
        def demand = []

        data.each { item->
            onRampActual << [item.time.getTime(), item.onRampVolume]
            onRampBaseline << [item.time.getTime(), item.onRampBaseline]
            offRampActual << [item.time.getTime(), item.offRampVolume]
            offRampBaseline << [item.time.getTime(), item.offRampBaseline]
            travelTimeActual << [item.time.getTime(), item.travelTime]
            travelTimeBaseline << [item.time.getTime(), item.travelTimeBaseline]
            capacity << [item.time.getTime(), item.capacity]
            demand << [item.time.getTime(), item.demand]
        }

        def onRampList = ramps.collect {it.onRamp + '-' + it.onRampDir }
        onRampList.unique()
        def onRampDetectors = onRampList.join(',')

        def offRampList = ramps.collect {it.offRamp + '-' + it.offRampDir }
        offRampList.unique()
        def offRampDetectors = offRampList.join(',')

        // State changes for various flags
        def onRampBands = createBands(data.collect {[time: it.time, flag: it.onRampFlag]})
        def offRampBands = createBands(data.collect {[time: it.time, flag: it.offRampFlag]})
        def travelTimeBands = createBands(data.collect {[time: it.time, flag: it.travelTimeFlag]})
        def signalChangeBands = createBands(data.collect {[time: it.time, flag: it.signalChangeState > 0]})

        return [
                onRampActual: onRampActual,
                onRampBaseline: onRampBaseline,
                offRampActual: offRampActual,
                offRampBaseline: offRampBaseline,
                travelTimeActual: travelTimeActual,
                travelTimeBaseline: travelTimeBaseline,
                capacity: capacity,
                demand: demand,
                onRampDetectors: onRampDetectors,
                offRampDetectors: offRampDetectors,
                onRampBands: onRampBands,
                offRampBands: offRampBands,
                travelTimeBands: travelTimeBands,
                signalChangeBands: signalChangeBands
        ]
    }

    def getSignalDevices(DetourRamp ramp) {
        ObjectMapper objectMapper = new ObjectMapper()
        def signalDevices = objectMapper.readValue(ramp.signalDevices, new TypeReference<List<String>>(){})

        def result = []
        signalDevices.each {
            def uid = it
            def opt = mTrafficDeviceRepository.findByUid(uid)
            if (opt.isPresent()) {
                TrafficDevice d = opt.get()
                result << [permit: uid, name: d.title, location: [lat: d.lat, lng: d.lon]]
            }
        }
        return result
    }

    def composeRampVolumes(List<DetourRamp> ramps, Date startTime, Date endTime) {
        // Compose ramp device list
        def rampList = []
        ramps.each {
            rampList << [ type: 'on Ramp', deviceId: Integer.parseInt(it.onRamp), direction: it.onRampDir]
            rampList << [ type: 'off Ramp', deviceId: Integer.parseInt(it.offRamp), direction: it.offRampDir]
        }
        rampList.unique{ it.deviceId + it.direction }
        rampList.sort { it.type }

        def result = []
        rampList.each {
            def data = mTrafficFlowDataService.getTrafficVolumeAndBaseline(it.deviceId, it.direction, 300, startTime)
            result << [type: it.type, deviceId: it.deviceId, direction: it.direction, data: data, startTime: startTime.getTime(), endTime: endTime.getTime()]
        }

        return result
    }

    def createBands(def flags) {
        def bands = []

        def startTime = null
        def prevTime = null
        flags.each {
            if (it.flag) {
                if (startTime == null) {
                    startTime = it.time
                }
                prevTime = it.time
            } else {
                if (startTime != null) {
                    if (startTime != prevTime) {
                        bands << [startTime.getTime(), prevTime.getTime()]
                    }
                    startTime = null
                }
            }
        }

        return bands
    }
}
