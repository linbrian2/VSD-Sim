package com.iai.traffic.services

import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import io.micronaut.discovery.event.ServiceStartedEvent

import com.iai.traffic.utils.AppUtils
import groovy.io.FileType
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class SimulationService {

    def simsToCheck = []
    def i = 1

    // https://docs.micronaut.io/latest/api/io/micronaut/scheduling/cron/CronExpression.html
    //    +---------------- second (0 - 59)
    //    |   |---------------- minute (0 - 59)
    //    |   |  +------------- hour (0 - 23)
    //    |   |  |  +---------- day of month (1 - 31)
    //    |   |  |  |  +------- month (1 - 12)
    //    |   |  |  |  |  +---- day of week (1 - 7) (MON-SUN)
    //    |   |  |  |  |  |

    // Trigger the job every one minute at 5 seconds
    @Scheduled(cron = '*/10 * * * * *')
    def checkSimulations() {
        // getListOfFiles()
        addDummySim()
        // getErrorFile('/home/blin/Simulation App/samples/PPO_MultiAgentHighwayPOEnv-v0_b815bb28_2023-05-19_16-14-11meehmeiu')
        log.info('Checking Simulations for new Files: Iteration {}', i)
        for (sim in simsToCheck) {
            println sim
            if (sim.checkpoint == 10) {
                simsToCheck -= sim
            }
        }
        i++
    }

    def addDummySim() {
        // Add Sim if not added
        def path = 'Dummy Simulation 1'
        if (!simsToCheck.find { x -> x.name == path }) {
            simsToCheck << [name: path, checkpoint: 0]
        }
        // Find sim and increase checkpoint
        def sim = simsToCheck.find { x -> x.name == path }
        sim.checkpoint++
    }

    def getListOfFiles() {
        def list = []

        def dir = new File("/home/vms_public/ray_results/template_ZM")
        dir.eachFileRecurse (FileType.FILES) { file ->
            list << file
        }
        list.each {
            println it.path
        }
        return list
    }

    def setSimulationData(path, checkpoint = 0) {
        if (!(simsToCheck.find { x -> x.name == path })) {
            simsToCheck << [name: path, checkpoint: checkpoint]
            log.info('Added new path: {}', path)
        } else {
            log.info('Duplicate File not added: {}', path)
        }
        def errorGenerated = getErrorFile(path)
        if (errorGenerated) {
            def sim = simsToCheck.find { x -> x.name == path }
            simsToCheck.remove(sim)
        }
        return [
            progress: [0],
            emissions: [[0]],
            checkpoint: checkpoint,
            errorGenerated: errorGenerated
        ]
    }

    def getFileData(path, getFile, itSize, emulateSim) {
        if (getFile == 'progress') {
            def progressData = parseProgressFile(path, itSize, emulateSim)
            def errorGenerated = getErrorFile(path)
            if (errorGenerated) {
                def sim = simsToCheck.find { x -> x.name == path }
                simsToCheck.remove(sim)
            }
            return [
                progress: progressData,
                proProgress: progressData.size() / (itSize * 10),
                errorGenerated: errorGenerated
            ]
        } else if (getFile == 'emission') {
            def emissionData = parseEmissionFiles(path, itSize, emulateSim)
            def sim = simsToCheck.find { x -> x.name == path }
            def checkpoint = sim.checkpoint
            def errorGenerated = getErrorFile(path)
            if (errorGenerated) {
                simsToCheck.remove(sim)
            }
            return [
                emissions: emissionData,
                checkpoint: checkpoint,
                emmProgress: checkpoint / 10,
                errorGenerated: errorGenerated
            ]
        } else {
            println 1
            def sim = simsToCheck.find { x -> x.name == path }
            if (sim) {
                simsToCheck.remove(sim)
            }
            println 2
            simsToCheck << [name: path, checkpoint: 10]
            println 3
            def progressData = parseProgressFile(path, itSize, emulateSim)
            def emissionData = parseEmissionFiles(path, itSize, emulateSim)
            println 4
            sim = simsToCheck.find { x -> x.name == path }
            println 5
            def checkpoint = sim.checkpoint
            println 6
            simsToCheck.remove(sim)
            println 7
            return [
                progress: progressData,
                proProgress: progressData.size() / (itSize * 10),
                emissions: emissionData,
                checkpoint: checkpoint,
                emmProgress: checkpoint / 10
            ]
        }
    }

    def getErrorFile(path) {
        String errPath = "${path}/error.txt"
        def file = new File(errPath)
        if (file.exists()) {
            def list = file.collect { it }
            simsToCheck -= path
            return list
        } else {
            return null
        }
    }

    def parseEmissionFiles(path, itSize, emulateSim) {
        def sim = simsToCheck.find { x -> x.name == path }
        def emissions = []
        if (!emulateSim) {
            println 'Implement checkpoint checker'
            while (sim.checkpoint < 10) {
                File f = new File("$path/checkpoint_${sim.checkpoint + 1}/filtered_trips.csv");
                if (f.exists()) {
                    sim.checkpoint++
                } else {
                    break
                }
            }

        } else {
            sim.checkpoint++
        }
        if (sim.checkpoint <= 10) {
            log.info("Reading ${sim.checkpoint} emission files.")
            for (int i = 1; i <= sim.checkpoint; i++) {
                def emissionFilePath = "${path}/checkpoint_${i * itSize}"
                def result = parseEmissionFile(emissionFilePath)
                emissions << result ? result : 0
            }
        }
        if (emissions.size() > 0) {
            return emissions
        } else {
            return [[0]]
        }
    }

    def parseProgressFile(filePath, itSize, emulateSim) {
        def sim = simsToCheck.find { x -> x.name == filePath }
        def checkpoint = sim.checkpoint
        String file = "${filePath}/progress.csv"
        log.info("Reading progress file.")
        // println "Reading progress file: $file"
        long startTime = System.currentTimeMillis()
        BufferedReader br = null
        String line = ''
        def dataList = []
        def i = 0
        try {
            InputStream is = new FileInputStream(file)
            if (!is) return
            br = new BufferedReader(new InputStreamReader(is))
            while ((line = br.readLine()) != null) {
                if (emulateSim && i >= itSize * (checkpoint + 1) + 1) {
                    break
                }
                if (i > 0) {
                    String[] info = line.split(',')
                    dataList.add([
                        episodeRewardMax: info[0] == 'nan' ? 0 : Double.parseDouble(info[0]),
                        episodeRewardMin: info[1] == 'nan' ? 0 : Double.parseDouble(info[1]),
                        episodeRewardMean: info[2] == 'nan' ? 0 : Double.parseDouble(info[2]),
                        episodeLenMean: info[3] == 'nan' ? 0 : Double.parseDouble(info[3]),
                        episodesThisIter: Integer.parseInt(info[4]),
                        timestepsThisIter: Integer.parseInt(info[5]),
                        done: info[6] == 'FALSE' ? false : true,
                        timestepsTotal: Integer.parseInt(info[7]),
                        episodesTotal: Integer.parseInt(info[8]),
                        trainingIteration: Integer.parseInt(info[9]),
                        experimentId: info[10],
                        date: info[11],
                        timestamp: Integer.parseInt(info[12]),
                        timeThisIterS: Double.parseDouble(info[13]),
                        timeTotalS: Double.parseDouble(info[14]),
                    ])
                }
                i++
            }
        } catch (FileNotFoundException e) {
            log.error('FileNotFoundException')
            return null
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime
        // println "Time taken: $estimatedTime ms"
        if (dataList.size() > 0) {
            return dataList
        } else {
            return [0]
        }
    }

    def parseEmissionFile(filePath) {
        String file = "${filePath}/filtered_trips.csv"
        // println "Reading emissions file: $file"
        long startTime = System.currentTimeMillis()
        BufferedReader br = null
        String line = ''
        def dataList = []
        def i = 0
        try {
            InputStream is = new FileInputStream(file)
            if (!is) return
            br = new BufferedReader(new InputStreamReader(is))
            while ((line = br.readLine()) != null) {
                if (i > 0) {
                    String[] info = line.split(',')
                    dataList.add([
                        time: Double.parseDouble(info[1]),
                        id: info[2],
                        x: Double.parseDouble(info[3]),
                        y: Double.parseDouble(info[4]),
                        speed: Double.parseDouble(info[5]),
                        headway: Double.parseDouble(info[6]),
                        leaderId: info[7],
                        accel: Double.parseDouble(info[12]),
                        distance: Double.parseDouble(info[16]),
                        followerId: info[18],
                        leaderRelSpeed: Double.parseDouble(info[19]),
                    ])
                }
                i++
            }
        } catch (FileNotFoundException e) {
            log.error('FileNotFoundException')
            return null
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime
        // println "Time taken: $estimatedTime ms"
        return dataList
    }

}
