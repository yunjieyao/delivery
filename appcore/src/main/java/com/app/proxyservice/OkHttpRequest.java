package com.app.proxyservice;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Villey on 2016/7/8.
 */
public class OkHttpRequest {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient sClient;
    private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    static {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        sClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                //.retryOnConnectionFailure(false)
                .sslSocketFactory(createSSLContext(xtm).getSocketFactory(), xtm)
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();
    }

    private static SSLContext createSSLContext(TrustManager trustManager){

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public static String postSync(String url, String data) throws IOException {
        return postSync(url, data, null);
    }

    public static String postSync(String url, String data, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if(headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        OkHttpClient client = sClient.newBuilder().build();
        Response response = client.newCall(builder.build()).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("Unexpected code " + response);
    }

    public static String putSync(String url, String data, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);
        if(headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        OkHttpClient client = sClient.newBuilder().build();
        Response response = client.newCall(builder.build()).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("Unexpected code " + response);
    }

    public static String deleteSync(String url, String data, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete(body);
        if(headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        OkHttpClient client = sClient.newBuilder().build();
        Response response = client.newCall(builder.build()).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("Unexpected code " + response);
    }

    // 这里并没有处理body部分
    public static String getSync(String url, String data, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        if(headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        OkHttpClient client = sClient.newBuilder().build();
        Response response = client.newCall(builder.build()).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("Unexpected code " + response);
    }

    public static void postAsync(String url, String data, Callback callback) throws IOException {
        postAsync(url, data, null, callback);
    }

    public static void postAsync(String url, String data, Map<String, String> headers, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, data);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if(headers != null) {
            for (String headerKey : headers.keySet()) {
                builder.addHeader(headerKey, headers.get(headerKey));
            }
        }
        sClient.newCall(builder.build()).enqueue(callback);
    }

    public static void getAsync(String url, Callback callback) throws IOException  {
        Request request = new Request.Builder()
                .url(url)
                .build();
        sClient.newCall(request).enqueue(callback);
    }

    public static String getSync(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = sClient.newCall(request).execute();
        if(response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("Unexpected code " + response);
    }

}
