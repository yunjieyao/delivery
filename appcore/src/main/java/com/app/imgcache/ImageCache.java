package com.app.imgcache;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。 硬 引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
 * 
 * @author dujianglei5130@gmail.com
 *
 */
public class ImageCache {

	private static final int SOFT_CACHE_SIZE = 16; // 软引用缓存容量
	private static final int LRU_CACHE_SIZE = (int) Runtime.getRuntime().maxMemory() / 4; // 默认的CacheSize大小

	private static LruCache<String, Bitmap> mLruCache; // 硬引用缓存
	private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache; // 软引用缓存

	/**
	 * 默认Cache缓存大小
	 */
	public ImageCache() {
		this(LRU_CACHE_SIZE);
	}

	/**
	 * 指定Cache缓存大小
	 * 
	 * @param cacheSize
	 */
	public ImageCache(int cacheSize) {
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null)
					return value.getRowBytes() * value.getHeight();
				else
					return 0;
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null)
					// 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
					mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_SIZE, 0.75f, true) {

			private static final long serialVersionUID = 6040103833179403725L;

			@Override
			protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 从缓存中获取图片
	 */
	public Bitmap getBitmapFromCache(String key) {
		Bitmap bitmap;
		// 先从硬引用缓存中获取
		synchronized (mLruCache) {
			bitmap = mLruCache.get(key);
			if (bitmap != null) {
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				mLruCache.remove(key);
				mLruCache.put(key, bitmap);
				return bitmap;
			}
		}
		// 如果硬引用缓存中找不到，到软引用缓存中找
		synchronized (mSoftCache) {
			SoftReference<Bitmap> bitmapReference = mSoftCache.get(key);
			if (bitmapReference != null) {
				bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 将图片移回硬缓存
					mLruCache.put(key, bitmap);
					mSoftCache.remove(key);
					return bitmap;
				} else {
					mSoftCache.remove(key);
				}
			}
		}
		return null;
	}

	public void addBitmapToCache(String key, Bitmap bitmap) {
		synchronized (mLruCache) {
			if (bitmap != null) {
				mLruCache.put(key, bitmap);
			}
		}
	}

	public void removeBitmapFromCache(String key) {
		synchronized (mLruCache) {
			mLruCache.remove(key);
		}
	}

	// 清除图片缓存
	public void clearCache() {
		// 清除硬缓存
		Iterator<Entry<String, Bitmap>> iter = mLruCache.snapshot().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Bitmap> entry = iter.next();
			Bitmap bitmap = entry.getValue();
			if (null != bitmap && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}
		mLruCache.evictAll();
		// 清除软缓存
		mSoftCache.clear();
	}
	
}