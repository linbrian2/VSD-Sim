package com.iai.traffic.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class AppUtils {

    static String[] parseArrStr(data) {
        if (!data || data == '[]') return [];
        def dataSet = [];

        def tempArr = data.replace('[', '')
        tempArr = tempArr.replace(']', '')
        String[] arr = tempArr.split(',');

        return arr;
    }

    static String capitalize(final String line) {
        return String.valueOf(Character.toUpperCase(line.charAt(0))) + line.substring(1);
    }

    static Date roundDown1Min(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static Date roundDown5Min(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)
        int minute =  calendar.get(Calendar.MINUTE)

        // Round down to 5-minute interval
        if (minute % 5 != 0) {
            minute = 5 * Math.floor(minute / 5)
        }

        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    static Date round5Min(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)
        int minute =  calendar.get(Calendar.MINUTE)

        // Round to 5-minute interval
        if (minute % 5 != 0) {
            minute = 5 * Math.round(minute / 5.0)
        }

        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    static boolean isMinuteX(Date time, int minutes) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)
        int minute =  calendar.get(Calendar.MINUTE)
        return (minute % minutes == 0);
    }

    static int getHourIndex(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    static int get5MinIndex(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour   = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int idx = hour * 12 + minute/5;
        return idx;
    }

    static Date getStartOfHour(Date date, int hourIdx) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourIdx);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static Date  getEndOfHour(Date date, int hourIdx) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourIdx);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    static def getHourFromDate(Date t) {
        Calendar now = Calendar.getInstance();
        now.setTime(t)
        return now.get(Calendar.HOUR_OF_DAY);
    }

    static long getDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.DATE, days)
        return cal.getTime()
    }

    static Date addSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.SECOND, seconds)
        return cal.getTime()
    }

    static Date minusSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.add(Calendar.SECOND, -seconds)
        return cal.getTime()
    }

    static float round2(float number) {
        return Math.round(number * 100)/100.0f
    }

    static long getDurationInSeconds(Date t0 ,Date t1) {
        long diff = t1.getTime() - t0.getTime();
        return TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
    }

    static String formatDateHHMM(Date t) {
        def df = new SimpleDateFormat("HH:mm")
        return df.format(t)
    }

    static String formatDateToDay(Date t) {
        def df = new SimpleDateFormat("yyyyMMdd")
        return df.format(t)
    }

    static String formatDateToString(Date t) {
        def df = new SimpleDateFormat("yyyyMMddHHmmss")
        return df.format(t)
    }

    static int getOneMinIdx(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)

        int hour = calendar.get(Calendar.HOUR_OF_DAY)
        int minute = calendar.get(Calendar.MINUTE)

        int index = hour * 60 + minute

        return index
    }

    static int getTimeMinuteInHHMM(Date time) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(time)

        int minute =  calendar.get(Calendar.MINUTE)

        // Round down to 5-minute interval
        if (minute % 5 != 0) {
            minute = 5 * Math.floor(minute / 5)
        }

        // Compose a integer with the format of MMss
        int result = calendar.get(Calendar.HOUR_OF_DAY) * 100 + minute

        return result
    }
    static boolean isSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance()
        Calendar cal2 = Calendar.getInstance()
        cal1.setTime(d1)
        cal2.setTime(d2)
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        return sameDay
    }

    /**
     * <p>Checks if a date is today.</p>
     * @param date the date, not altered, not null.
     * @return true if the date is today.
     * @throws IllegalArgumentException if the date is <code>null</code>
     */
    static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

}
