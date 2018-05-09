package com.app.netstatecontrol;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robert
 * @date 2017/12/29
 */

public class NetLogHelper {
    /**
     * max size
     */
    private static final int MAX_SIZE = 50;

    private static NetLogHelper sInstance;
    /**
     * save net console information
     */
    private static final List<BusinessInfo> sConsoleList = new ArrayList<>();

    private OnNetConsoleListener mListener;

    private static final Map<String, BusinessInfo> sConsoleArrayMap = new HashMap<>();

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    private NetLogHelper() {

    }

    synchronized public static NetLogHelper getInstance() {
        if (sInstance == null) {
            sInstance = new NetLogHelper();
        }
        return sInstance;
    }

    public void setOnRequestListener(OnNetConsoleListener listener) {
        this.mListener = listener;
        if (mListener != null) {
            mListener.onNetConsole(getConsoleInfoList());
        }
    }

    public void removeListener() {
        if (mListener != null) {
            mListener = null;
        }
    }

    public synchronized String putRequest(String url, String type, String value) {
        if (sConsoleArrayMap.size() > MAX_SIZE) {
            sConsoleArrayMap.remove(sConsoleArrayMap.get(0));
        }

        RequestInfo reqInfo = RequestInfo.newBuilder()
                .requestType("[" + type + "]")
                .requestUrl(url)
                .time(getDayTime())
                .value(value)
                .build();
        BusinessInfo businessInfo = new BusinessInfo();
        businessInfo.setRequestInfo(reqInfo);
        sConsoleArrayMap.put(reqInfo.time + url, businessInfo);
        return reqInfo.time + url;
    }

    public synchronized void putResponse(String key, String value) {
        if (sConsoleArrayMap.containsKey(key)) {
            ResponseInfo responseInfo = ResponseInfo.newBuilder()
                    .time(getDayTime())
                    .responseValues(value)
                    .build();
            sConsoleArrayMap.get(key).setResponseInfo(responseInfo);
        }
    }

    private synchronized List<BusinessInfo> getConsoleInfoList() {
        sConsoleList.clear();
        sConsoleList.addAll(sConsoleArrayMap.values());
        return sConsoleList;
    }

    private static String getDayTime() {
        Date currentTime = new Date();
        return FORMAT.format(currentTime);
    }

    public interface OnNetConsoleListener {
        void onNetConsole(List<BusinessInfo> consoleInfoList);
    }

    public static class RequestInfo implements Serializable {
        private String requestType;
        private String requestUrl;
        private String time;
        private String value;

        private RequestInfo(Builder builder) {
            requestType = builder.requestType;
            requestUrl = builder.requestUrl;
            time = builder.time;
            value = builder.value;
        }

        public String getRequestType() {
            return requestType;
        }

        public void setRequestType(String requestType) {
            this.requestType = requestType;
        }

        public String getRequestUrl() {
            return requestUrl;
        }

        public void setRequestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        @Override
        public String toString() {
            return "RequestInfo{" +
                    "requestType='" + requestType + '\'' +
                    ", requestUrl='" + requestUrl + '\'' +
                    ", time='" + time + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }

        public static final class Builder {
            private String requestType;
            private String requestUrl;
            private String time;
            private String value;

            private Builder() {
            }

            public Builder requestType(String val) {
                requestType = val;
                return this;
            }

            public Builder requestUrl(String val) {
                requestUrl = val;
                return this;
            }

            public Builder time(String val) {
                time = val;
                return this;
            }

            public Builder value(String val) {
                value = val;
                return this;
            }

            public RequestInfo build() {
                return new RequestInfo(this);
            }
        }
    }

    public static class ResponseInfo implements Serializable {
        private String time;
        private String responseValues;

        private ResponseInfo(Builder builder) {
            setTime(builder.time);
            setResponseValues(builder.responseValues);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getResponseValues() {
            return responseValues;
        }

        public void setResponseValues(String responseValues) {
            this.responseValues = responseValues;
        }


        @Override
        public String toString() {
            return "ResponseInfo{" +
                    "time='" + time + '\'' +
                    ", responseValues='" + responseValues + '\'' +
                    '}';
        }

        public static final class Builder {
            private String time;
            private String responseValues;

            private Builder() {
            }

            public Builder time(String val) {
                time = val;
                return this;
            }

            public Builder responseValues(String val) {
                responseValues = val;
                return this;
            }

            public ResponseInfo build() {
                return new ResponseInfo(this);
            }
        }
    }

    public static class BusinessInfo implements Serializable, Comparable<BusinessInfo> {
        private RequestInfo requestInfo;
        private ResponseInfo responseInfo;

        public RequestInfo getRequestInfo() {
            return requestInfo;
        }

        public void setRequestInfo(RequestInfo requestInfo) {
            this.requestInfo = requestInfo;
        }

        public ResponseInfo getResponseInfo() {
            return responseInfo;
        }

        public void setResponseInfo(ResponseInfo responseInfo) {
            this.responseInfo = responseInfo;
        }

        @Override
        public int compareTo(@NonNull BusinessInfo businessInfo) {
            String lTime = this.getRequestInfo().getTime();
            String rTime = businessInfo.getRequestInfo().getTime();
            try {
                Date lDate = FORMAT.parse(lTime);
                Date rDate = FORMAT.parse(rTime);
                return -(lDate.compareTo(rDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 1;
        }
    }
}
