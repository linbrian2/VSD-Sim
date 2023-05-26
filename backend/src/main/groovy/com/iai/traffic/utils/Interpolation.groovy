package com.iai.traffic.utils

class Interpolation {

    def averageArrayList(def listData, int intervalInSeconds, int method) {
        return averageArrayList(listData, intervalInSeconds, method, null, null)
    }

    def averageArrayList(def listData, int intervalInSeconds, int method, Long startTime, Long endTime) {
        if (listData == null) {
            return []
        }
        if (listData.size() < 10) {
            return listData
        }

        def invalidCount = 0
        listData.each {
            if (it[1] == null) {
                invalidCount ++
            }
        }
        def total = listData.size()
        if (invalidCount * 2 > total) {
            return []
        }

        // Get duration in seconds
        long start    = startTime ?: listData.get(0)[0]
        long end      = endTime ?: listData.get(listData.size() - 1)[0]
        long duration = (end - start)/ 1000
        long binSize  = duration/intervalInSeconds + 1

        if(binSize < 2) {
            return []
        }

        // Fill in missing data points
        long normalDistance = 300
        long thresholdDistance = 550
        if (foundMissingPointsInArray(listData, thresholdDistance)) {
            def newListData = []
            int listCount = listData.size()
            for (int i = 0; i <  listCount - 1; i ++) {
                newListData << listData.get(i)

                long t0 = listData[i][0]
                long t1 = listData[i + 1][0]
                long d = (t1 - t0)/1000

                if (d > thresholdDistance) {
                    int n = d/normalDistance - 1
                    if (n < 1) n = 1
                    for (int j = 0; j < n; j++) {
                        def alpha = (j + 1)/(n + 1.0)
                        def time  = t0 + normalDistance  * 1000 * j
                        def v0 = listData[i][1]
                        def v1 = listData[i + 1][1]
                        if (v0 != null && v1 != null) {
                            def value = (Integer)(v0 * (1 - alpha) + v1 * alpha)
                            newListData << [time, value]
                        }
                    }
                }

            }
            newListData << listData.get(listCount - 1)
            listData.clear()
            listData.addAll(newListData)
        }

        int[] bin = new int[binSize]
        int[] cc  = new int[binSize]
        long[] tt = new long[binSize]
        for(int i = 0; i < binSize; i++) {
            bin[i] = 0
            tt[i] = start + i * intervalInSeconds * 1000
        }

        listData.each {
            def t = it[0]
            int  idx = (int)((t - start)/(intervalInSeconds * 1000))
            if(idx >= 0 && idx < binSize && it[1] != null) {
                bin[idx] += it[1]
                cc[idx]++
            }
        }

        // method = 0, accumulate, method = 1, average
        if (method > 0) {
            for(int i = 0; i < binSize; i++) {
                if (cc[i] > 0) {
                    bin[i] /= cc[i]
                }
            }
        }

        // Remove the first zero appearances
        int startIdx = 0
        for(long i = 0; i < binSize; i++) {
            if(bin[(int)i] > 0) {
                startIdx = i;
                break;
            }
        }
        // Remove the last zero appearances
        int endIdx = binSize - 1
        for(long i = binSize - 1; i > startIdx ; i--) {
            if(bin[(int)i] > 0) {
                endIdx = i;
                break;
            }
        }
        if(endIdx - startIdx <= 0)
            return [];

        def result = []
        for (int i = startIdx; i < endIdx; i++) {
            if (bin[i] > 0) {
                result << [tt[i], bin[i]]
            }
        }

        return result
    }

    def averageMapList(def listData, int intervalInSeconds, int method) {
        if (listData == null || listData.size() < 2) {
            return []
        }

        def invalidCount = 0
        listData.each {
            if (it.value == null) {
                invalidCount ++
            }
        }
        def total = listData.size()
        if (invalidCount * 2 > total) {
            return []
        }

        // Get duration in seconds
        def startDate = listData.get(0).time
        def endDate   = listData.get(listData.size() - 1).time

        long start    = startDate.getTime();
        long end      = endDate.getTime();
        long duration = (end - start)/ 1000;
        long binSize  = duration/intervalInSeconds + 1

        if(binSize < 2) {
            return []
        }

        // Fill in missing data points
        long normalDistance = 300
        long thresholdDistance = 550
        if (foundMissingPoints(listData, thresholdDistance)) {
            def newListData = []
            int listCount = listData.size()
            for (int i = 0; i <  listCount - 1; i ++) {
                newListData << listData.get(i)

                long t0 = listData[i].time.getTime()
                long t1 = listData[i + 1].time.getTime()
                long d = (t1 - t0)/1000
                if (d > thresholdDistance) {
                    int n = d/normalDistance - 1
                    if (n < 1) n = 1
                    for (int j = 0; j < n; j++) {
                        def alpha = (j + 1)/(n + 1.0)
                        def time  = new Date(t0 + normalDistance  * 1000 * j)
                        if (listData[i].value != null && listData[i + 1].value != null) {
                            def value = (Integer)(listData[i].value * (1 - alpha) + listData[i + 1].value * alpha)
                            newListData << [time:time, value:value]
                        }
                    }
                }

            }
            newListData << listData.get(listCount - 1)
            listData.clear()
            listData.addAll(newListData)
        }

        int[] bin = new int[binSize]
        int[] cc  = new int[binSize]
        long[] tt = new long[binSize]
        for(int i = 0; i < binSize; i++) {
            bin[i] = 0
            tt[i] = start + i * intervalInSeconds * 1000
        }

        listData.each {
            Date t = it.time;
            int  idx = (int)((t.getTime() - start)/(intervalInSeconds * 1000))
            if(idx >= 0 && idx < binSize && it.value != null) {
                bin[idx] += it.value
                cc[idx]++
            }
        }

        // method = 0, accumulate, method = 1, average
        if (method > 0) {
            for(int i = 0; i < binSize; i++) {
                if (cc[i] > 0) {
                    bin[i] /= cc[i]
                }
            }
        }

        // Remove the first zero appearances
        int startIdx = 0
        for(long i = 0; i < binSize; i++) {
            if(bin[(int)i] > 0) {
                startIdx = i;
                break;
            }
        }
        // Remove the last zero appearances
        int endIdx = binSize - 1
        for(long i = binSize - 1; i > startIdx ; i--) {
            if(bin[(int)i] > 0) {
                endIdx = i;
                break;
            }
        }
        if(endIdx - startIdx <= 0)
            return [];

        def result = []
        for (int i = startIdx; i < endIdx; i++) {
            result << [time: tt[i], value: bin[i]]
        }

        return result
    }

    def foundMissingPoints(def listData, long thresholdDistance) {
        boolean found = false
        for (int i = 0; i < listData.size() - 1; i ++) {
            long d = (listData[i + 1].time.getTime() - listData[i].time.getTime())/1000
            if (d > thresholdDistance) {
                found = true
                break
            }
        }
        return found
    }

    def foundMissingPointsInArray(def listData, long thresholdDistance) {
        boolean found = false
        for (int i = 0; i < listData.size() - 1; i ++) {
            long d = (listData[i + 1][0] - listData[i][0])/1000
            if (d > thresholdDistance) {
                found = true
                break
            }
        }
        return found
    }

    def toHighChartSeries(def listData) {
        return listData.collect { v->
            return [v.time, v.value]
        }
    }
}
