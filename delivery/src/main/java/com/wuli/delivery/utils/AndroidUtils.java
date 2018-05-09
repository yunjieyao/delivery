package com.wuli.delivery.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.log.Log;

public class AndroidUtils {
	private final static String TAG = AndroidUtils.class.getSimpleName();

	/**
	 * 检查context对应的Activity的状态
	 *
	 * @return
	 */
	public static boolean isValidContext(Activity activity) {
		if(activity == null ){
			return false;
		}
		if (isDestroyed(activity) || activity.isFinishing()) {
			Log.info(TAG, "Activity is invalid." + "[isFinishing]:" + activity.isFinishing());
			return false;
		} else {
			return true;
		}
	}
	
	@TargetApi(17)
	private static boolean isDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return activity.isDestroyed();
		}
		return false;
	}

	/**
	 * 判断是否在v之外区域(目前适用EditText之外点击隐藏键盘)
	 * @param v
	 * @param event
     * @return
     */
	public static boolean isOutSizeView(View v, MotionEvent event) {
		if (v != null && event != null) {
			int[] leftTop = { 0, 0 };
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
		}
		return false;
	}

	/**
	 * 设置屏幕亮度 0 - 255 越高越亮
	 */
	public static void setWindowBrightness(Activity context, int brightness) {
		Window window = context.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.screenBrightness = brightness / 255.0f;
		window.setAttributes(lp);
	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context){
		try {
			PackageManager packageManager=context.getPackageManager();
			PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
