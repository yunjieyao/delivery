package com.app.proxyservice;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * Http请求 主要应用于网络请求应答，获取网络流
 * 
 * @author dujianglei@kingnode.com
 *
 */
public class HttpRequest {

	// 获取 HttpClient 对象
	private static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient http = new DefaultHttpClient(httpParams);
		return http;
	}

	// get请求
	private static HttpResponse getHttpResponseFromURL(String url) throws IOException {
		HttpClient http = getHttpClient();
		HttpUriRequest req = new HttpGet(url);
		HttpResponse rsp = http.execute(req);
		return rsp;
	}

	// post请求
	private static HttpResponse postHttpResponseFromURL(String url, String json) throws IOException {
		HttpClient http = getHttpClient();
		HttpEntity entity = new StringEntity(json, HTTP.UTF_8);
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		return http.execute(post);
	}

	// 主要用于网络请求应答
	public static String getHttpContent(String url, String json) throws Exception {
		HttpResponse rsp = postHttpResponseFromURL(url, json);
		HttpEntity entity = rsp.getEntity();
		return EntityUtils.toString(entity, HTTP.UTF_8);
	}

	// get主要用于网络流的获取
	public static InputStream getHttpContentStream(String url) throws IOException {
		HttpResponse rsp = getHttpResponseFromURL(url);
		HttpEntity entity = rsp.getEntity();
		return entity.getContent();
	}

	// get请求返回字符串
	public static String getHttpContent(String url) throws IOException {
		HttpResponse rsp = getHttpResponseFromURL(url);
		HttpEntity entity = rsp.getEntity();
		return EntityUtils.toString(entity, HTTP.UTF_8);
	}
}
