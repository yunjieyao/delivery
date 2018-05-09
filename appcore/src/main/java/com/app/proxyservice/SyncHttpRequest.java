package com.app.proxyservice;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

// 同步请求
public class SyncHttpRequest {

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    // 使用https连接的时候，默认不验证证书
    private static SyncHttpClient sClient = new SyncHttpClient(true, 80, 443);

    static {
        sClient.setConnectTimeout(20 * 1000);
        sClient.setResponseTimeout(60 * 1000);
        //sClient.setMaxRetriesAndTimeout(0, 10 * 1000); // retry count设置为0，不进行重试
    }

    public static void get(String urlString, AsyncHttpResponseHandler res) {
        sClient.get(urlString, res);
    }

    public static void get(String urlString, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        sClient.get(urlString, params, responseHandler);
    }

    public static void get(String urlString, JsonHttpResponseHandler res) {
        sClient.get(urlString, res);
    }

    public static void get(String urlString, RequestParams params,
                           JsonHttpResponseHandler res) {
        sClient.get(urlString, params, res);
    }

    public static void get(String uString, BinaryHttpResponseHandler bHandler) {
        sClient.get(uString, bHandler);
    }

    public static void post(String urlString, AsyncHttpResponseHandler res) {
        sClient.post(urlString, res);
    }

    public static void post(String urlString, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        params.setUseJsonStreamer(true);
        sClient.post(urlString, params, responseHandler);
    }

    public static void post(Context context, String url, String jsonData,
                            ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(jsonData, HTTP.UTF_8);
        sClient.post(context, url, entity, CONTENT_TYPE, responseHandler);
    }

    @SuppressWarnings("deprecation")
    public static void get(Context context, String url, String jsonData,
                           ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(jsonData, HTTP.UTF_8);
        sClient.get(context, url, entity, CONTENT_TYPE, responseHandler);
    }

    @SuppressWarnings("deprecation")
    public static void delete(Context context, String url, String jsonData,
                              ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(jsonData, HTTP.UTF_8);
        sClient.delete(context, url, entity, CONTENT_TYPE, responseHandler);
    }

    @SuppressWarnings("deprecation")
    public static void put(Context context, String url, String jsonData,
                           ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(jsonData, HTTP.UTF_8);
        sClient.put(context, url, entity, CONTENT_TYPE, responseHandler);
    }

    public static void addHeader(String header, String value) {
        sClient.addHeader(header, value);
    }

    public static void addHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (String key : headers.keySet()) {
                sClient.addHeader(key, headers.get(key));
            }
        }
    }

    public static void post(String urlString, JsonHttpResponseHandler res) {
        sClient.post(urlString, res);
    }

    public static void post(String urlString, RequestParams params,
                            JsonHttpResponseHandler res) {
        sClient.post(urlString, params, res);
    }

    public static void post(String uString, BinaryHttpResponseHandler bHandler) {
        sClient.post(uString, bHandler);
    }

    public static void setUserAgent(String userAgent) {
        sClient.setUserAgent(userAgent);
    }

    public static void cancelRequests(Context context) {
        sClient.cancelRequests(context, true);
    }
}