package com.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziv
 * @date 2018/1/11
 */

public class BirthdayTimeUtils {


    public static List<Integer> getCalendarYearList(int startYear) {

        List<Integer> yearList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        int endYear = calendar.get(Calendar.YEAR);

        int length = endYear - startYear + 1;

        for (int i = 0; i < length; i++) {
            int currentYear = startYear + i;
            yearList.add(currentYear);
        }

        return yearList;
    }


    public static Map<Integer, Map<Integer, List<Integer>>> getCalendarTimeList(int startYear) {

        Map<Integer, Map<Integer, List<Integer>>> yearToMonthAndDayMap = new HashMap<>();

        List<Integer> yearList = getCalendarYearList(startYear);

        for (Integer year : yearList) {
            yearToMonthAndDayMap.put(year, getMonthToDayMapByYear(year));
        }

        return yearToMonthAndDayMap;

    }

    private static Map<Integer, List<Integer>> getMonthToDayMapByYear(int year) {

        Map<Integer, List<Integer>> monthToDayMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            monthToDayMap.put(i, getDayListByYearAndMonth(year, i));
        }

        return monthToDayMap;


    }

    private static List<Integer> getDayListByYearAndMonth(int year, int month) {


        List<Integer> dayList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM");

        try {

            calendar.setTime(simpleDate.parse(String.format("%d-%d", year, month)));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < day + 1; i++) {
            dayList.add(i);
        }

        return dayList;
    }
}

