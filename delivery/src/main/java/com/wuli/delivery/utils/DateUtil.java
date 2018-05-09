package com.wuli.delivery.utils;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;

import com.app.util.NumberParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_02 = "MM-dd HH:mm";
    public static final String DATETIME_FORMAT_03 = "HH:mm";
    public static final String DATETIME_FORMAT_04 = "MM-dd";
    public static final String DATETIME_FORMAT_05 = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_06 = "M月d日";
    public static final String DATETIME_FORMAT_07 = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_FORMAT_08 = "HH:00";
    public static final String DATETIME_FORMAT_09 = "mm:ss";
    public static final String DATETIME_FORMAT_10 = "HH:mm:ss";
    public static final String DATETIME_FORMAT_11 = "yyyyMMddHHmmss";
    public static final String DATETIME_FORMAT_12 = "yyyy年MM月dd日";

    private static SimpleDateFormat sSDF = new SimpleDateFormat();

    public static String getFormatTimeByCalendar(Calendar calendar, String pattern) {
        sSDF.applyPattern(pattern);
        return sSDF.format(calendar.getTime());
    }

    public static String getFormaTimeByDate(Date date, String pattern) {
        sSDF.applyPattern(pattern);
        return sSDF.format(date.getTime());
    }


    public static String currentTime(String pattern, long time) {
        sSDF.applyPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        return sSDF.format(new Date(time));
    }

    public static Date getTime(String pattern, String time) {
        sSDF.applyPattern(pattern);
        Date date = null;
        try {
            date = sSDF.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String currentTime(String pattern) {
        sSDF.applyPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sSDF.format(getFixedDate());
    }

    public static String getCurrentTime(int afterMinute, String pattern) {
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Calendar nowTime = getFixedCanlendar();
        nowTime.add(Calendar.MINUTE, afterMinute);
        return sSDF.format(nowTime.getTime());
    }

    public static String getCurrentTimeFormatByMinute(String pattern) {
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Calendar nowTime = getFixedCanlendar();
        nowTime.set(Calendar.MINUTE, 0);
        return sSDF.format(nowTime.getTime());
    }

    public static String getCurrentSplitTime(String splitTime, String pattern) {
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Calendar nowTime = getFixedCanlendar();
        nowTime.set(Calendar.MINUTE, NumberParser.parseInt(splitTime));
        return sSDF.format(nowTime.getTime());
    }

    /**
     * 获取当前时间之后的一个小时
     *
     * @return
     */
    public static String getCurrentTimeFormatByHour(String startTime) {
        Calendar nowTime = getFixedCanlendar();
        nowTime.add(Calendar.HOUR, 1);
        sSDF.applyLocalizedPattern("HH:00");
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        return compareTwoTime("HH:00", sSDF.format(nowTime.getTime()), startTime) ? sSDF
                .format(nowTime.getTime()) : startTime;
    }

    public static String getCurrentTimeFormatByHour() {
        Calendar nowTime = getFixedCanlendar();
        nowTime.add(Calendar.HOUR, 1);
        sSDF.applyLocalizedPattern("HH:00");
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        return sSDF.format(nowTime.getTime());
    }

    private static Calendar getFixedCanlendar() {
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        nowTime.setTimeInMillis(getFixedCurrTime());
        return nowTime;
    }

    // 取得当前服务器时间
    private static Date getFixedDate() {
        Date date = new Date(getFixedCurrTime());
        return date;
    }

    // 取得当前服务器时间
    private static long getFixedCurrTime() {
        long timeOffset = getTimeOffset();
        if (timeOffset == 0L) {
            return System.currentTimeMillis();
        }
        Log.e("DateUtil", "timeOffset is " + timeOffset);
        Log.e("DateUtil", "SystemClock.elapsedRealtime() + timeOffset is " + SystemClock.elapsedRealtime() + timeOffset);
        return SystemClock.elapsedRealtime() + timeOffset;
    }

    private static long getTimeOffset() {
        return 0L;
    }

    public static int getCurYear() {
        Calendar nowTime = getFixedCanlendar();
        nowTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return nowTime.get(Calendar.YEAR);
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        if (sdate == null || "".equals(sdate)) {
            return null;
        }
        sSDF.applyLocalizedPattern(DATETIME_FORMAT);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            return sSDF.parse(sdate);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static String format2(String time) {
        Date date = toDate(time);
        if (date == null) {
            return null;
        } else {
            sSDF.applyLocalizedPattern(DATETIME_FORMAT_02);
            sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            return sSDF.format(date);
        }
    }

    public static String format(String time, String pattern) {
        Date date = toDate(time);
        if (date == null) {
            return null;
        } else {
            sSDF.applyLocalizedPattern(pattern);
            sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            return sSDF.format(date);
        }
    }

    public static String splitTime(String startTime, String splitTime) {
        sSDF.applyLocalizedPattern(DATETIME_FORMAT_03);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = null;
        try {
            date = sSDF.parse(startTime);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(date);
        nowTime.add(Calendar.MINUTE, Integer.valueOf(splitTime));
        String time = sSDF.format(nowTime.getTime());
        return time;
    }

    public static int getIntervalBetweenTwoTime(String startTime,
                                                String endTime,
                                                String splitTime,
                                                String pattern) {
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Date startDate = null;
        Date endDate = null;
        int interval = 0;

        try {
            startDate = sSDF.parse(startTime);
            endDate = sSDF.parse(endTime);
            long timeInterVal = endDate.getTime() - startDate.getTime();
            long splitTimeInterval = NumberParser.parseInt(splitTime) * 60 * 1000;
            if (timeInterVal > splitTimeInterval) {
                return (int) (timeInterVal / splitTimeInterval);
            } else {
                return 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return interval;

    }

    public static String getFormatTime(int interval, String pattern) {
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Calendar calendar = getFixedCanlendar();
        calendar.add(Calendar.DATE, interval);// 把日期往后增加一天.整数往后推,负数往前移动
        Date date = calendar.getTime();         // 这个时间就是日期往后推一天的结果
        String dateString = sSDF.format(date);
        return dateString;
    }

    public static String getLocalFormatTime(int interval) {
        sSDF.applyLocalizedPattern(DATETIME_FORMAT_06);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Calendar calendar = getFixedCanlendar();
        calendar.add(Calendar.DATE, interval);// 把日期往后增加一天.整数往后推,负数往前移动
        Date date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        String dateString = sSDF.format(date);
        return dateString;
    }

    public static Date getDateLocalFormatTime(int interval) {
        Calendar calendar = getFixedCanlendar();
        calendar.add(Calendar.DATE, interval);// 把日期往后增加一天.整数往后推,负数往前移动
        Date date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        return date;
    }

    public static Calendar getCalendarLocalFormatTime(int interval) {
        Calendar calendar = getFixedCanlendar();
        calendar.add(Calendar.DATE, interval);// 把日期往后增加一天.整数往后推,负数往前移动
        return calendar;
    }

    public static String getShowDateText(Date date) {
        sSDF.applyPattern(DATETIME_FORMAT);
        return String.format("%s(%s月%s日)", formatDateTime(date), String.format("%tm", date), String.format("%td", date));
    }

    public static String getShowDateText(Calendar calendar) {
        sSDF.applyPattern(DATETIME_FORMAT);
        return String.format("%s(%s月%s日)", formatDateTime(calendar), String.format("%tm", calendar), String.format("%td", calendar));
    }

    public static String parseLocalTime(CharSequence localTime) {
        try {
            String curTime = new SimpleDateFormat("yyyy年").format(getFixedDate()).concat(localTime.toString());
            Date parse = new SimpleDateFormat("yyyy年M月d日").parse(curTime);
            sSDF.applyLocalizedPattern(DATETIME_FORMAT_04);
            sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            return sSDF.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isCurrentTimeBetweenTwoTime(String startTime, String endTime) {
        boolean isCurrentTimeBetweenTwoTime = false;
        sSDF.applyLocalizedPattern(DATETIME_FORMAT_03);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String currentTime = sSDF.format(getFixedDate());
        try {
            long currentDate = sSDF.parse(currentTime).getTime();
            long workOffDate = sSDF.parse(endTime).getTime();
            long startDate = sSDF.parse(startTime).getTime();
            if (currentDate <= workOffDate && currentDate >= startDate) {
                isCurrentTimeBetweenTwoTime = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isCurrentTimeBetweenTwoTime;
    }

    public static boolean isStoreWorkOff(String pattern, String startTime, String endTime) {
        boolean isStoreWorkOff = false;
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String currentTime = sSDF.format(getFixedDate());
        try {
            long currentDate = sSDF.parse(currentTime).getTime();
            long workOffDate = sSDF.parse(endTime).getTime();
            long startDate = sSDF.parse(startTime).getTime();
            if (currentDate > workOffDate || currentDate < startDate) {
                isStoreWorkOff = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isStoreWorkOff;
    }

    public static boolean compareTwoTime(String pattern, String time1, String time2) {
        boolean isBiger = false;
        sSDF.applyLocalizedPattern(pattern);
        sSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        try {
            long time_1 = sSDF.parse(time1).getTime();
            long time_2 = sSDF.parse(time2).getTime();
            if (time_1 > time_2) {
                isBiger = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isBiger;
    }

    public static String formatDate(String oPattern, String tPattern, String time) {
        SimpleDateFormat oFormat = new SimpleDateFormat(oPattern);
        oFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        SimpleDateFormat tFormat = new SimpleDateFormat(tPattern);
        tFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        Date date = null;
        try {
            date = oFormat.parse(time);
            return tFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String formatByHHMM(String tPattern, String HHMM) {
        SimpleDateFormat tFormat = new SimpleDateFormat(tPattern);
        tFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        tFormat.setCalendar(Calendar.getInstance());
        Calendar calendar = getFixedCanlendar();
        int hour = 0;
        int minute = 0;
        try {
            String[] splitTime = HHMM.split(":");
            if (splitTime.length >= 2) {
                hour = Integer.valueOf(splitTime[0]);
                minute = Integer.valueOf(splitTime[1]);
            }
        } catch (IndexOutOfBoundsException e) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        HHMM = tFormat.format(calendar.getTime());
        return HHMM;
    }

    /**
     * 是否是明天
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static boolean isTomorrow(final String time) {
        return isTheDay(toDate(time), getFixedDate());
    }

    /**
     * 是否是指定日期
     *
     * @param date
     * @param day
     * @return
     */
    public static boolean isTheDay(final Date date, final Date day) {
        return date.getTime() >= tomorrowBegin(day).getTime()
                && date.getTime() <= tomorrowEnd(day).getTime();
    }

    /**
     * 获取指定时间的明天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date tomorrowBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定时间的明天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date tomorrowEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    // 计算两个时间相隔的秒数
    public static long getIntervalTimesBettweenTwoTimes(long serverTime, long localTime) {

        long interval = serverTime - localTime;

        return Math.abs(interval / 1000 * 60);

    }

    public static String getIntervalByCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_08);
        long currentTimeMillis = System.currentTimeMillis();
        String currentTime = format.format(new Date(currentTimeMillis));
        String nextTime = format.format(new Date(currentTimeMillis + 60 * 60 * 1000));
        return String.format("%s-%s", currentTime, nextTime);
    }

    public static String getIntervalWithMinuteAndSecond(String timeExpire) {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT);
        SimpleDateFormat format2 = new SimpleDateFormat(DATETIME_FORMAT_09);
        long internal = 0;
        long currentServerTime = getFixedCurrTime();
        try {
            long expireTime = format.parse(timeExpire).getTime();
            internal = expireTime - currentServerTime <= 0 ? 0 : (expireTime - currentServerTime);

            Log.e("DateUtil", "expireTime is " + expireTime);

        } catch (ParseException e) {
            e.printStackTrace();
            internal = 0;
        }

        Log.e("DateUtil", "currentServerTime is " + currentServerTime);
        Log.e("DateUtil", "internal is " + internal);

        return format2.format(internal);
    }

    public static void main(String[] args) {
        int expireTime = 5;
        int currentServerTime = 2;

        int internal = expireTime - currentServerTime <= 0 ? 0 : (expireTime - currentServerTime);
        System.out.println("A  = " + internal);

        SimpleDateFormat format2 = new SimpleDateFormat(DATETIME_FORMAT_09);
        System.out.println("B  = " + format2.format(internal));

    }

    public static String formatDateTime(Date date) {
        int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
        long today = (getFixedCurrTime() + offSet) / 86400000;
        long start = (date.getTime() + offSet) / 86400000;
        long interval = start - today;
        if (interval == 0) {
            return "今天";
        } else if (interval == 1) {
            return "明天";
        } else if (interval == 2) {
            return "后天";
        }

        return "";
    }

    public static String formatDateTime(Calendar calendar) {
        int offSet = calendar.getTimeZone().getRawOffset();
        long today = (getFixedCurrTime() + offSet) / 86400000;
        long start = (calendar.getTime().getTime() + offSet) / 86400000;
        long interval = start - today;
        if (interval == 0) {
            return "今天";
        } else if (interval == 1) {
            return "明天";
        } else if (interval == 2) {
            return "后天";
        }

        return "";
    }

}
