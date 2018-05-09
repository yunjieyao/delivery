package com.app.proxyservice;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncHttpRequest {
	// 使用https连接的时候，默认不验证证书
	private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

	static {
		client.setTimeout(11000);
	}

	public static void get(String urlString, AsyncHttpResponseHandler res) {
		client.get(urlString, res);
	}

	public static void get(String urlString, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(urlString, params, responseHandler);
	}

	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);
	}

	public static void get(String urlString, RequestParams params,
			JsonHttpResponseHandler res) {
		client.get(urlString, params, res);
	}

	public static void get(String uString, BinaryHttpResponseHandler bHandler) {
		client.get(uString, bHandler);
	}

	public static void post(String urlString, AsyncHttpResponseHandler res) {
		client.post(urlString, res);
	}

	public static void post(String urlString, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(urlString, params, responseHandler);
	}

	public static void post(String urlString, JsonHttpResponseHandler res) {
		client.post(urlString, res);
	}

	public static void post(String urlString, RequestParams params,
			JsonHttpResponseHandler res) {
		client.post(urlString, params, res);
	}

	public static void post(String uString, BinaryHttpResponseHandler bHandler) {
		client.post(uString, bHandler);
	}

	public static AsyncHttpClient getClient() {
		return client;
	}

}