package com.iai.traffic.services

import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import io.micronaut.discovery.event.ServiceStartedEvent

import com.iai.traffic.utils.AppUtils
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class SimulationService {

    def simsToCheck = []
    def checkpoint = 0
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
    @Scheduled(cron = '5 */1 * * * *')
    def checkSimulations() {
        log.info('Checking Simulations for new Files: Iteration {}', i)
        for (sim in simsToCheck) {
            println sim
        }
        i++
    }

    def setSimulationData(path) {
        if (!(path in simsToCheck)) {
            simsToCheck << path
            log.info('Added new path: {}', path)
        } else {
            log.info('Duplicate File not added: {}', path)
        }
        checkpoint = 0
        return [
            progress: [0],
            emissions: [[0]],
            checkpoint: checkpoint
        ]
    }

    def getFileData(path, useSampleData) {
        if (useSampleData) {
            println 'Using sample data'
            checkpoint = 9 // 2nd to last checkpoint

            // * 104 Linux Server
            // path = "/home/blin/new_progress/PPO_MultiAgentHighwayPOEnv-v0_b815bb28_2023-05-19_16-14-11meehmeiu"
            // * Local Windows
            path = "../samples/PPO_MultiAgentHighwayPOEnv-v0_b815bb28_2023-05-19_16-14-11meehmeiu"

            return [
                progress: parseProgressFile(path),
                emissions: parseEmissionFiles(path, 20),
                checkpoint: checkpoint
            ]
        } else {
            println 'Using actual data'
            return [
                progress: parseProgressFile(path),
                emissions: parseEmissionFiles(path, 20),
                checkpoint: checkpoint
            ]
        }
    }

    def parseEmissionFiles(path, itSize) {
        def emissions = []
        checkpoint++
        if (checkpoint <= 10) {
            for (int i = 1; i <= checkpoint; i++) {
                def emissionFilePath = "${path}/checkpoint_${i * itSize}"
                // if (i == 7) emissionFilePath = "${path}/checkpoint_999"
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

    def parseProgressFile(filePath) {
        String file = "${filePath}/progress.csv"
        println "Reading progress file: $file"
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
        println "Time taken: $estimatedTime ms"
        if (dataList.size() > 0) {
            return dataList
        } else {
            return [0]
        }
    }

    def parseEmissionFile(filePath) {
        String file = "${filePath}/filtered_trips.csv"
        println "Reading emissions file: $file"
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
        println "Time taken: $estimatedTime ms"
        return dataList
    }

}
