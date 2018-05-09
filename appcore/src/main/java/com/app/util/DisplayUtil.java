package com.app.util;

public class DisplayUtil {
	/**
	 * @param pxValue
	 * 
	 * @param scale （DisplayMetrics类中属性density）
	 * 
	 * @return
	 */
	public static int px2dip(float density, float pxValue) {
		return (int) (pxValue / density + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(float density, float dipValue) {
		return (int) (dipValue * density + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float scaledDensity, float pxValue) {
		return (int) (pxValue / scaledDensity + 0.5f);

	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(float scaledDensity, float spValue) {
		return (int) (spValue * scaledDensity + 0.5f);
	}

}
