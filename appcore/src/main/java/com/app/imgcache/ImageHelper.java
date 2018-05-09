package com.app.imgcache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.app.log.Log;
import com.app.netstream.NetStreamManager;

public class ImageHelper {

	private static final ImageHelper IMAGE_HELPER = new ImageHelper();
	private static final String TAG = "ImageHelper";

	/*-------------线程控制相关变量--------------*/
	private final int corePoolSize = 4; // 线程池维护线程的最少数量
	private final int maximumPoolSize = 64 * 2; // 线程池维护线程的最大数量
	private final long keepAliveTime = 30000L; // 线程池维护线程所允许的空闲时间、

	private ThreadPoolExecutor poolExecutor; // 自定义线程池子
	/*-------------线程控制相关变量--------------*/

	// 默认的CacheSize大小 50M
	private final int imageCacheSize = 50 * 1024 * 1024;
	private ImageCache imageCache;

	private String imgCacheDir;

	private Context mContext;

	private String proxyServer;

	private ImageHelper() {
	}

	public static ImageHelper getImageHelper() {
		return IMAGE_HELPER;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	// 初始化图片缓存组件 imgCacheDir 本地图片缓存路径
	@SuppressLint("NewApi")
	public void initImageHelper(Context context, String imgCacheDir) {
		this.mContext = context;
		this.imgCacheDir = imgCacheDir;
		// 初始化线程池，用来执行异步图片下载任务
		poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		// 内存缓存初始化
		imageCache = new ImageCache(imageCacheSize);

		// 检测目录是否存在，不存在创建缓存目录
		File cacheDir = new File(imgCacheDir);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	// 关闭图片缓存组件
	// 此方法执行时，必须保证使用imageCache获取的位图的界面全部关闭，否则会报错
	public void closedImageHelper() {
		// 清除内存缓存
		if (imageCache != null) {
			imageCache.clearCache();
			imageCache = null;
		}
		// 关闭线程池控制
		if (poolExecutor != null) {
			poolExecutor.shutdown();
			poolExecutor = null;
		}
	}

	// 无缓存，一次获取位图对象
	public Bitmap getImageOnce(String imageName) {
		return null;
	}

	@SuppressLint("DefaultLocale")
	public Bitmap diskCacheImageWithUrl(int resid, boolean shouldMemoryCache) {
		String key = String.format("%d", resid);
		// 从内存缓存中获取
		Bitmap bitmap = null;
		if (imageCache != null
				&& (bitmap = imageCache.getBitmapFromCache(key)) != null) {
			return bitmap;
		}

		Bitmap localBitmap = BitmapFactory.decodeResource(
				mContext.getResources(), resid);
		if (shouldMemoryCache && imageCache != null) {
			imageCache.addBitmapToCache(key, localBitmap);
		}
		return localBitmap;
	}

	public Bitmap diskCacheImageWithUrl(String path, boolean shouldMemoryCache) {
		String key = String.format("%s", keyWithURL(path));
		// 从内存缓存中获取
		Bitmap bitmap = null;
		if (imageCache != null
				&& (bitmap = imageCache.getBitmapFromCache(key)) != null) {
			return bitmap;
		}

		Bitmap localBitmap = BitmapFactory.decodeFile(path);
		if (shouldMemoryCache && imageCache != null) {
			imageCache.addBitmapToCache(key, localBitmap);
		}
		return localBitmap;
	}

	
	@SuppressLint("DefaultLocale")
	public Bitmap asyncImageWithUrl(final String url, final int width, final int height, final boolean shouldMemoryCache, final ImageBlock imageBlock) {
		// 如果url为空值，直接返回
		if (url == null || url.length() == 0) {
			return null;
		}

		// 通过 url 匹配键值对里面的key值
		final String key = keyWithURL(url);

		// 从内存缓存中获取
		Bitmap bitmap = null;
		if (imageCache != null && (bitmap = imageCache.getBitmapFromCache(key)) != null) {
			return bitmap;
		}

		// 从本地缓存中获取
		if (poolExecutor != null) {
			poolExecutor.execute(new Runnable() {

				@Override
				public void run() {

					Bitmap localBitmap;
					if ((localBitmap = fetchCachedDataWithProxy(key)) != null && imageBlock != null) {

						if (shouldMemoryCache && imageCache != null) {
							imageCache.addBitmapToCache(key, localBitmap);
						}
						// 回调图片
						if (imageBlock != null) {
							imageBlock.onImageloaded(url, localBitmap);
						}
					}
					// 无本地缓存
					else {
						InputStream inputStream = null;
						try {
							String imgUrl = rebuildImageUrlString(url);
							inputStream = NetStreamManager.getStreamManager().download(imgUrl);

							Bitmap bitmap = null;
							try {
								BitmapFactory.Options ops = new BitmapFactory.Options();
								ops.inPreferredConfig = Bitmap.Config.RGB_565;
								bitmap = BitmapFactory.decodeStream(inputStream, null, ops);
							} catch (Exception e) {
								e.printStackTrace();
							}

							// 如果下载位图为空 则直接返回
							if (bitmap == null) {

								// 回调图片
								if (imageBlock != null) {
									imageBlock.onDowndloadFailure(ImageBlock.NO_NET);
								}

								return;
							}

							// 本地缓存图片
							saveCacheWithData(key, bitmap);

							// 回调图片
							if (imageBlock != null) {
								imageBlock.onImageloaded(url, bitmap);
							}
							// 内存缓存图片
							if (imageCache != null && shouldMemoryCache) {
								imageCache.addBitmapToCache(key, bitmap);
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			});
		}
		return null;
	}

	private String rebuildImageUrlString(String urlStr) {
		String result = urlStr;
		// 不是以http或https开头，则判断为相对路径,进行拼接处理
		if (!urlStr.matches("^http[s]?://.+")) {
			result = String.format("%s%s%s", proxyServer,
					(urlStr.startsWith("/") ? "" : "/"), urlStr);
		}
		Log.debug(TAG, "rebuildURL: " + result);
		return result;

	}

	// 添加文件到本地缓存
	private void saveCacheWithData(String key, Bitmap bitmap) {

		String path = imgCacheDir + String.format("/%s", key);

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

				objectOutputStream.writeObject(bitmap);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectOutputStream != null) {
						objectOutputStream.close();
					}

					if (fileOutputStream != null) {
						fileOutputStream.close();
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

	// 从本地缓存获取值
	private Bitmap fetchCachedDataWithProxy(String key) {
		String path = imgCacheDir + String.format("/%s", key);

		File file = new File(path);
		if (file.exists()) {
			Bitmap result = null;
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				result = BitmapFactory.decodeStream(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return result;
		}
		return null;
	}

	// 匹配key
	private String keyWithURL(String value) {
		try {
			// MD5转化
			MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(value.getBytes());

			byte[] bytes = mDigest.digest();
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					builder.append('0');
				}
				builder.append(hex);
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			return String.valueOf(value.hashCode());
		}
	}

	// 图片按质量压缩方法
	private Bitmap ImageCompressByQuality(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 图片按比例大小压缩方法
	@SuppressWarnings("unused")
	private Bitmap ImageCompressByScale(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return ImageCompressByQuality(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	// 压缩图片
	@SuppressWarnings("unused")
	private Bitmap compressImage(URL imgUrl, CGSize srcSize) throws IOException {

		CGSize cgSize = scaleImageSize(imgUrl, srcSize);

		if (cgSize == null) {
			return null;
		}

		InputStream is = null;
		HttpURLConnection http = null;
		Bitmap bitmap_compressed = null;// 这个是被等比缩放过的Bitmap
		try {
			http = (HttpURLConnection) imgUrl.openConnection();
			is = http.getInputStream();

			BitmapFactory.Options options__ = new BitmapFactory.Options();
			options__.inSampleSize = cgSize.inSampleSize;
			options__.inJustDecodeBounds = false;

			bitmap_compressed = BitmapFactory.decodeStream(is, null, options__);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (http != null) {
				http.disconnect();
				http = null;
			}
		}
		if (bitmap_compressed == null) {
			// 啥也没读出来..
			return null;
		}
		return Bitmap.createScaledBitmap(bitmap_compressed, cgSize.width,
				cgSize.height, true);
	}

	// 获取缩放后的 CGSize
	private CGSize scaleImageSize(URL imgUrl, CGSize srcSize) {

		InputStream is = null;
		HttpURLConnection http = null;
		BitmapFactory.Options options = null;

		try {
			http = (HttpURLConnection) imgUrl.openConnection();
			is = http.getInputStream();

			options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, options);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (http != null) {
				http.disconnect();
				http = null;
			}
		}

		if (options == null) {
			return null;
		}

		int height = options.outHeight;
		int width = options.outWidth;

		int wantedWidth = srcSize.width;
		int wantedHeight = srcSize.height;

		// 宽和高，谁大用谁
		int wrapSize = wantedWidth > wantedHeight ? wantedWidth : wantedHeight;

		// 第二步 大小压缩比按照 wrapSize X wrapSize 计算出我们所期望的图片大小
		int outWidth = 0;
		int outHeight = 0;

		if (width > height) {
			if (width > wrapSize) {
				// 如果宽比高要长 又超过了400
				outWidth = wrapSize; // 宽就是400
				outHeight = wrapSize * height / width; // 高就是400xratio
			} else {
				outWidth = width;
				outHeight = height;
			}
		} else if (height > width) {
			if (height > wrapSize) {
				// 如果高比宽要长 又超过了400
				outHeight = wrapSize; // 宽就是400
				outWidth = wrapSize * width / height; // 高就是400xratio
			} else {
				outWidth = width;
				outHeight = height;
			}
		} else {
			outWidth = wrapSize;
			outHeight = wrapSize;
		}

		int inSampleSize = 1;

		if (height > outHeight || width > outWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) outHeight);
			final int widthRatio = Math.round((float) width / (float) outWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		CGSize cgSize = new CGSize();
		cgSize.inSampleSize = inSampleSize;
		cgSize.width = outWidth;
		cgSize.height = outHeight;

		return cgSize;
	}

	class CGSize {
		int width;
		int height;
		int inSampleSize;
	}
	
	/**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

}
