package com.app.util;

import android.text.TextUtils;

// 数字转化 
public class NumberParser {

    // 字符串转 long 整型
    public static long parseLong(String value) {
        return parseLong(value, 0L);
    }

    // 字符串转 long 整型
    public static long parseLong(String value, long defaultValue) {
        long resultValue = defaultValue;
        try {
            resultValue = Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
            return resultValue;
        }
        return resultValue;
    }

    // 字符串转 double
    public static double parseDouble(String value, double defaultValue) {
        double resultValue;
        try {
            resultValue = Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 double
    public static double parseDouble(String value) {
        return parseDouble(TextUtils.isEmpty(value) ? "0" : value, 0.0D);
    }

    // 字符串转 float
    public static float parseFloat(String value, float defaultValue) {
        float resultValue;
        try {
            resultValue = Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 float
    public static float parseFloat(String value) {
        return parseFloat(value, 0.0F);
    }

    // 字符串转 int
    public static int parseInt(String value, int defaultValue) {
        int resultValue;
        try {
            resultValue = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            resultValue = defaultValue;
        }
        return resultValue;
    }

    // 字符串转 int
    public static int parseInt(String value) {
        return parseInt(value, 0);
    }

    // Object转int
    public static int parseInt(Object object) {
        return NumberParser.parseInt(String.valueOf(object));
    }


}
