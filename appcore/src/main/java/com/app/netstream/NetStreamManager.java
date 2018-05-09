package com.app.netstream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.json.JSONException;

import com.app.log.Log;
import com.app.util.JSONParser;

/**
 * 网络文件流工具类，支持文件上传和下载
 * 
 * @author dujianglei5130@gmail.com
 *
 */
public class NetStreamManager {

	private static final String TAG = "NetFileStreamUtil";

	private static final NetStreamManager STREAM_MANAGER = new NetStreamManager();

	private NetStreamManager() {
	}

	public static NetStreamManager getStreamManager() {
		return STREAM_MANAGER;
	}

	/**
	 * 文件下载返回流对象
	 * 
	 * @param strUrl
	 * @return
	 * @throws IOException
	 */
	public InputStream download(String strUrl) throws IOException {

		URL url = new URL(strUrl);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setConnectTimeout(30000);
		http.setReadTimeout(30000);
		http.setDoInput(true); // 允许输入流
		http.setRequestMethod("GET");
		http.setRequestProperty("Connection", "Keep-Alive");

		return http.getInputStream();
	}

	private static final String BOUNDARY = "AABBCC7d*%x";
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";

	private String resUrl;

	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}

	public class UploadResp {

		public int errorCode = -1;
		public String error;

		public String goupName;
		public String remoteFileName;
		public String httpUrl;

	}

	/**
	 * {"errorCode":0,"data":{"goupName":"group1","remoteFileName":
	 * "M00/00/00/wKgBQ1XB4t6AQvBdAAAAa9neTFI581.txt"}}
	 * 
	 * errorCode == 0表示上传无误
	 * 
	 * 文件上传
	 * 
	 * @param filePath
	 * @param strUrl
	 * @return
	 * @throws IOException
	 */
	public UploadResp upload(String filePath) throws IOException {

		UploadResp uploadResp = new UploadResp();

		if (null == resUrl) {
			uploadResp.errorCode = -1;
			uploadResp.error = "资源服务器地址未获取";
			return uploadResp;
		}

		String fileName = ""; // 默认上传文件名为空字符
		long fileSize = 0; // 默认文件大小
		File simpleFile = new File(filePath);
		if (!simpleFile.exists()) {
			uploadResp.errorCode = -1;
			uploadResp.error = "上传文件不存在";
			return uploadResp;
		}

		fileSize = simpleFile.length();
		fileName = simpleFile.getName();

		URL url = new URL(String.format("%s/upload", resUrl));
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setConnectTimeout(30000);
		http.setReadTimeout(30000);
		http.setDoInput(true); // 允许输入流
		http.setDoOutput(true); // 允许输出流
		http.setUseCaches(false); // 不允许使用缓存
		http.setRequestMethod("POST");
		http.setRequestProperty("Charset", "UTF-8");
		http.setRequestProperty("Connection", "Keep-Alive");
		http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
		http.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(BOUNDARY);
		sb.append(LINE_END);
		sb.append("Content-Disposition: form-data; name=fileName\r\n\r\n" + fileName);
		sb.append(LINE_END + PREFIX + BOUNDARY + LINE_END);
		
		sb.append("Content-Disposition: form-data; name=fileSize\r\n\r\n" + fileSize);
		sb.append(LINE_END + PREFIX + BOUNDARY + LINE_END);
		
		sb.append("Content-Disposition: form-data; name=\"file\";" + "filename=\"" + fileName + "\"" + LINE_END);
		
		sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
		sb.append(LINE_END);
		
		Log.debug(TAG, "Upload: " + sb.toString());

		DataOutputStream dos = new DataOutputStream(http.getOutputStream());

		dos.write(sb.toString().getBytes());

		InputStream is = new FileInputStream(simpleFile);
		byte[] bytes = new byte[1024];
		int len = 0;
		while ((len = is.read(bytes)) != -1) {
			dos.write(bytes, 0, len);
		}
		is.close();

		dos.write(LINE_END.getBytes());
		dos.write((PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes());

		dos.flush();
		dos.close();

		// 获取响应码 200=成功 当响应成功，获取响应的流
		int res = http.getResponseCode();
		if (res == HttpURLConnection.HTTP_OK) {
			InputStream input = http.getInputStream();
			StringBuffer sb1 = new StringBuffer();
			int ss;
			while ((ss = input.read()) != -1) {
				sb1.append((char) ss);
			}
			String result = sb1.toString();
			Log.error(TAG, "result : " + result);
			Map<String, Object> resultMap = null;
			try {
				resultMap = JSONParser.parse(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (resultMap != null) {

				int errorCode = (Integer) resultMap.get("errorCode");

				if (errorCode == 0) {
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) resultMap.get("data");

					String goupName = (String) data.get("goupName");
					String remoteFileName = (String) data.get("remoteFileName");

					uploadResp.goupName = goupName;
					uploadResp.remoteFileName = remoteFileName;
					uploadResp.httpUrl = String.format("%s/%s/%s", resUrl, goupName, remoteFileName);
					uploadResp.errorCode = errorCode;
					uploadResp.error = "文件上传成功";

				} else {
					uploadResp.errorCode = -1;
					uploadResp.error = "文件上传 失败";
				}

			} else {
				uploadResp.errorCode = -1;
				uploadResp.error = "返回数据格式不正确";
			}
		} else {
			Log.error(TAG, "request error");
			uploadResp.errorCode = -1;
			uploadResp.error = "文件上传失败";
		}
		return uploadResp;
	}

	// 文件删除
	public int delete(String fileID) throws Exception {
		return 0;
	}
}
