package com.test;

/**
 * Created by ziv on 2017/8/29.
 */

public class test {

    public static void main(String[] args) {
////        String url = "/group1/M00/00/07/wKgBH1hTjP2AYTSYAAAe013Kuzs184.png?width=180&height=80";
////        System.out.println(getMetroHeight(url,144));
////        System.out.println(getMetroWidth(url,144));
//        System.out.println(getFixedMessage("哈哈哈\neheheheh\nhahahahah"));

//        List<Integer> yearList = BirthdayTimeUtils.getCalendarYearList(1990);
//
//        for (int year : yearList) {
//            Map<Integer, Map<Integer, List<Integer>>> map = BirthdayTimeUtils.getCalendarTimeList(1990);
//
//
//            for (int i = 1; i < 13; i++) {
//
//                Map<Integer, List<Integer>> monthToDayMap = map.get(year);
//
//                List<Integer> dayList = monthToDayMap.get(i);
//
//                for (Integer day : dayList) {
//                    System.out.println(String.format("%d年%d月%d日", year, i, day));
//                }
//            }
//
//        }

//        String code = "2011040011043".replaceAll("\\d{4}(?!$)", "$0 ");
//        System.out.println(code);

        outer:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {

                if (j == 0) {
                    break outer;
                }

                System.out.println("i == " + i + "\n" + "j == " + j);
            }

            System.out.println("i == " + i);
        }


    }

//    public static int getMetroWidth(String url, int defaultWidth) {
//        int width = 0;
//        int start = url.indexOf("?");
//        String startText = url.substring(start);
//        int middle = startText.indexOf("&");
//        String text = startText.substring(0, middle);
//        Pattern pattern = Pattern.compile("\\d+");
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            width = Integer.valueOf(matcher.group());
//        }
//
//        return Math.min(width == 0 ? defaultWidth : width, defaultWidth);
//
//    }
//
//    public static int getMetroHeight(String url, int defaultHeight) {
//        int width = 0;
//        int start = url.indexOf("&");
//        String text = url.substring(start);
//        Pattern pattern = Pattern.compile("\\d+");
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            width = Integer.valueOf(matcher.group());
//        }
//
//        return Math.min(width == 0 ? defaultHeight : width, defaultHeight);
//
//    }
//
//    public static String getFixedMessage(CharSequence message) {
//
//        StringBuilder sb = new StringBuilder();
//        String[] messageArray = message.toString().split("\n", 5);
//        int length = messageArray.length;
//
//        if (length == 1) {
//            return message.toString();
//        } else {
//            sb.append(messageArray[0]).append("\n");
//            for (int i = 1; i < length - 1; i++) {
//                sb.append(getMaxLengthString(messageArray[i])).append("\n");
//            }
//            sb.append(messageArray[length - 1]);
//
//            return sb.toString();
//        }
//    }
//
//    public static String getMaxLengthString(String message) {
//        int length = message.length();
//        if (length > 14) {
//            return message.substring(0, 14).concat("...");
//        } else {
//            return message.substring(0, length);
//        }
//    }
}
