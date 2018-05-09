package com.app.proxyservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;

import com.app.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProxyCache {
	
	private static final String TAG = "ProxyCache";

	/**
	 *  客户端内置的网络请求缓存阈值 1个小时
	 */
	static int CACHE_CONFIG_THRESHOLD = 3600; // 单位：秒

	private static final ProxyCache PROXY_CACHE = new ProxyCache();

	/*-------------线程控制相关变量--------------*/
	private final int corePoolSize = 0; // 线程池维护线程的最少数量
	private final int maximumPoolSize = 2; // 线程池维护线程的最大数量
	private final long keepAliveTime = 30000L; // 线程池维护线程所允许的空闲时间、

	private ThreadPoolExecutor poolExecutor; // 自定义线程池子
	/*-------------线程控制相关变量--------------*/

	private String proxyCacheDir;

	private Map<String, ProxyCacheConfig> mShouldCachedRequestMap = new HashMap<>();

	private ProxyCache() {

	}

	public static ProxyCache getProxyCache() {
		return PROXY_CACHE;
	}

	public void init(Context context, String proxyCacheDir) {
		this.proxyCacheDir = proxyCacheDir;
		// 初始化线程池，用来执行异步图片下载任务
		poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		// 检测目录是否存在，不存在创建缓存目录
		File cacheDir = new File(proxyCacheDir);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	// 通过url+params获取缓存数据
	public Serializable fetchCachedDataWithProxy(String url, Map<String, Object> params, int cacheExpiretime) {
		return this.fetchCachedDataWithProxy(keyWithProxy(url, params, cacheExpiretime), cacheExpiretime);
	}

	// 保存缓存文件到本地文件
	public void saveCacheWithData(Serializable cachedData, String url, Map<String, Object> params, int cacheExpiretime) {
		this.saveCacheWithData(cachedData, keyWithProxy(url, params, cacheExpiretime), cacheExpiretime);
	}

	public void saveShouldCacheWithExpirateTime() {

	}


	// 删除指定缓存
	public void deleteCacheWithProxy(String url, Map<String, Object> params, int cacheExpiretime) {
		this.deleteCacheWithProxy(keyWithProxy(url, params, cacheExpiretime), cacheExpiretime);
	}

	@SuppressLint("DefaultLocale")
	private String keyWithProxy(String url, Map<String, Object> params, int cacheExpiretime) {
		return String.format("%d%s", url.hashCode(), transformedParams(params));
	}

	/*
	 * 可以看到，edit()方法接收一个参数key，这个key将会成为缓存文件的文件名，并且必须要和图片的URL是一一对应的。
	 * 那么怎样才能让key和图片的URL能够一一对应呢？直接使用URL来作为key？不太合适
	 * ，因为图片URL中可能包含一些特殊字符，这些字符有可能在命名文件时是不合法的
	 * 。其实最简单的做法就是将图片的URL进行MD5编码，编码后的字符串肯定是唯一的，并且只会包含0-F这样的字符，完全符合文件的命名规则。
	 */
	// 通过url+params匹配key值
	private Object transformedParams(Map<String, Object> params) {
		String result = asString(params);
		try {
			// MD5转化
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(result.getBytes());
			byte[] bytes = mDigest.digest();

			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					stringBuilder.append('0');
				}
				stringBuilder.append(hex);
			}
			result = stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			result = String.valueOf(result.hashCode());
		}
		return result;
	}

	private String asString(Map<String, Object> map){
		if(map == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(String key : map.keySet()) {
			sb.append(key).append(map.get(key));
		}
		return sb.toString();
	}

	// 从本地缓存获取值
	private Serializable fetchCachedDataWithProxy(String key, int cacheExpiretime) {

		String directoryPath = proxyCacheDir + String.format("%d", cacheExpiretime);
		String path = directoryPath + String.format("/%s", key);

		File file = new File(path);
		if (file.exists()) {
			Serializable result = null;
			ObjectInputStream obinputStream = null;
			try {
				obinputStream = new ObjectInputStream(new FileInputStream(file));
				result = (Serializable) obinputStream.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					obinputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
		return null;
	}



	// 添加文件到本地缓存
	private void saveCacheWithData(Serializable cachedData, String key, int cacheExpiretime) {

		String directoryPath = proxyCacheDir + String.format("/%d", cacheExpiretime);
		String path = directoryPath + String.format("/%s", key);

		File directoryDir = new File(directoryPath);

		boolean issucc = true;

		if (!directoryDir.exists()) {
			issucc = directoryDir.mkdir();
		}

		if (issucc) {
			File directoryFile = new File(path);
			boolean createSucc = false;
			if (!directoryFile.exists()) {
				try {
					createSucc = directoryFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (createSucc) {
				FileChannel fileChannel = null;
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objectOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(directoryFile);
					fileChannel = fileOutputStream.getChannel();
					fileChannel.lock();

					objectOutputStream = new ObjectOutputStream(fileOutputStream);
					objectOutputStream.writeObject(cachedData);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
						if (objectOutputStream != null) {
							objectOutputStream.close();
						}
						if (fileChannel != null) {
							fileChannel.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	// 删除指定的缓存文件
	private void deleteCacheWithProxy(String key, int cacheExpiretime) {
		String directoryPath = proxyCacheDir + String.format("/%d", cacheExpiretime);
		String path = directoryPath + String.format("/%s", key);

		File directoryfile = new File(path);
		if (directoryfile.exists()) {
			directoryfile.delete();
		}
	}

	private boolean isCleaning = false; // 是否正在清理中

	// 清除缓存文件
	public void cleanExpireProxyCache() {

		if (isCleaning) {
			return;
		}

		poolExecutor.execute(new Runnable() {

			@Override
			public void run() {
				isCleaning = true;

				File cacheDir = new File(proxyCacheDir);
				String[] dirNames = cacheDir.list();
				if(dirNames != null) {
					for (String dirname : dirNames) {

						long cacheMaxCacheTime = Long.parseLong(dirname);
						Date expirationDate = new Date(System.currentTimeMillis() - cacheMaxCacheTime);

						String directoryPath = String.format("%s/%s", proxyCacheDir, dirname);

						File onedirectory = new File(directoryPath);

						File[] files = onedirectory.listFiles();

						Log.debug(TAG, "expirationDate ::" + expirationDate.toString());
						if(files != null) {
							for (File file : files) {
								
								Date lastModifiedDate = new Date(file.lastModified());
								//Log.debug(TAG, "file " + file.getName() + ">>>" + lastModifiedDate.toGMTString());
								if (lastModifiedDate.before(expirationDate)) {
									//Log.debug(TAG, "delete...");
									file.delete();
								}
							}
						}
					}
				}
				isCleaning = false;
			}
		});
	}

	public boolean cleanAllProxyCache() {
		return deleteDir(new File(proxyCacheDir));
	}

	public boolean cleanProxyCache(int cacheExpiretime) {
		return deleteDir(new File(proxyCacheDir + String.format("/%d", cacheExpiretime)));
	}

	private boolean deleteDir(File dir) {
		// 文件不存在
		if (!dir.exists()) {
			return false;
		}

		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public boolean shouldReadCacheFromBackendServiceConfigWithURL(String url, Map<String, Object> params){
		String key = keyWithProxy(url, params, 0);
		ProxyCacheConfig config = getCacheConfigWithCacheKey(key);
		if(config != null) {
			long now = SystemClock.elapsedRealtime() / 1000;
			if(now - config.lastCacheTime < CACHE_CONFIG_THRESHOLD) {
				return true;
			}
		}
		return false;
	}

	public int getCacheExpireTimeFromBackendServiceWithURL(String url, Map<String, Object> params) {
		String key = keyWithProxy(url, params, 0);
		ProxyCacheConfig config = getCacheConfigWithCacheKey(key);
		if(config != null) {
			return (int) config.configTime;
		}
		return 0;
	}

	public void setCacheConfigFromBackendServiceWithURL(String url, Map<String, Object> params, int cacheExpiretime){
		if(cacheExpiretime <= 0) {
			return;
		}
		String key = keyWithProxy(url, params, 0);
		ProxyCacheConfig config = new ProxyCacheConfig();
		config.cacheKey = key;
		config.lastCacheTime = SystemClock.elapsedRealtime() / 1000;
		config.configTime = cacheExpiretime;
		setCacheConfigWithCacheKey(key, config);
	}

	private ProxyCacheConfig getCacheConfigWithCacheKey(String key){
		synchronized (this) {
			return mShouldCachedRequestMap.get(key);
		}

	}

	private void setCacheConfigWithCacheKey(String key, ProxyCacheConfig config) {
		synchronized (this) {
			mShouldCachedRequestMap.put(key, config);
		}
	}


	class ProxyCacheConfig {
		/**
		 *  上次缓存网络请求的系统开机时间戳 单位（s）
		 */
		public long lastCacheTime;

		/**
		 *  服务端配置的缓存失效时间 单位（s）
		 */
		public long configTime;

		/**
		 *  根据URL和Key生成的对应请求的key
		 */
		public String cacheKey;
	}
}
