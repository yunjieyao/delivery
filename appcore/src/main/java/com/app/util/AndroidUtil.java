package com.app.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

public class AndroidUtil {

	static final String TAG = "Android";

	public static boolean isMainProcess(Context context) {
		ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		String mainProcessName = context.getPackageName();
		int myPid = android.os.Process.myPid();
		for (ActivityManager.RunningAppProcessInfo info : processInfos) {
			if (info.pid == myPid && mainProcessName.equals(info.processName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查context对应的Activity的状态
	 *
	 */
	public static boolean isValidContext(Activity activity) {
		if(activity == null ){
			return false;
		}
		if (isDestroyed(activity) || activity.isFinishing()) {
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

	@TargetApi(11)
	// @TargetApi(VERSION_CODES.HONEYCOMB)
	public static void enableStrictMode() {
		if (hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

			if (hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class,
				// 1).setClassInstanceLimit(ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	/**
	 * API level is or higher than 8
	 */
	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= 8; // VERSION_CODES.FROYO;
	}

	/**
	 * API level is or higher than 9
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= 9; // VERSION_CODES.GINGERBREAD;
	}

	/**
	 * API level is or higher than 11
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= 11; // VERSION_CODES.HONEYCOMB;
	}

	/**
	 * API level is or higher than 12
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= 12; // VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * API level is or higher than 16
	 */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= 16; // VERSION_CODES.JELLY_BEAN;
	}

	/**
	 * API level is higher than 19
	 */
	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= 19; // VERSION_CODES.KITKAT;
	}

	public static boolean checkDeviceIsRoot() {

		// get from build info
		String buildTags = android.os.Build.TAGS;
		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}

		// check if /system/app/Superuser.apk is present
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				Log.d("command", "/system/app/Superuser.apk file existed!");
				return true;
			} else {
				Log.e("command", file.getName() + " Not present!");
			}
		} catch (Exception e1) {
			// ignore
		}
		if (findBinary("su")) {
			return true;
		}
		return false;
	}

	public static boolean findBinary(String binaryName) {
		boolean found = false;
		if (!found) {
			String[] places = { "/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
					"/system/bin/failsafe/", "/data/local/" };
			for (String where : places) {
				if (new File(where + binaryName).exists()) {
					found = true;
					break;
				}
			}
		}
		return found;
	}

	/**
	 * 获取内存大小
	 * 
	 * @return
	 */
	public static String getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2 = "";
		BufferedReader localBufferedReader = null;
		FileReader fr = null;
		try {
			fr = new FileReader(str1);
			localBufferedReader = new BufferedReader(fr, 8192);
			while ((str2 = localBufferedReader.readLine()) != null) {
				// Log.i(TAG, "---" + str2);
				if (str2.startsWith("MemTotal")) {
					str2 = str2.trim().substring(9);
					return str2.trim();
				}
			}
		} catch (IOException e) {
			return "0";
		} finally {
			try {
				if (fr != null)
					fr.close();
				if (localBufferedReader != null)
					localBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "0";
	}

	/**
	 * 最小频率
	 * 
	 * @return
	 */
	public static String getMinCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	/**
	 * 获取cpu个数
	 * 
	 * @return
	 */
	public static int getNumCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}
		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Log.debug(TAG, "CPU Count: " + files.length);
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Print exception
			Log.e(TAG, "CPU Count: Failed.");
			e.printStackTrace();
			// Default to return 1 core
			return 1;
		}
	}

	/**
	 * 最大频率（单位:兆赫）
	 * 
	 * @return
	 */
	public static String getCpuFrequence() {
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);

			Process process = cmd.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = reader.readLine();
			// Log.debug(TAG, line);
			long z = Long.parseLong(line);
			z = z / 1000; // Mhz 兆赫
			return z + "";
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return "0";
	}

	// 检测SDcard是否存在
	public static boolean checkSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	// 检查app是否已安装
	public static boolean chechAppInstallState(Context ctx, String packageName) {
		// 获取所有应用的名称，包名，以及权限 有了包名就可以判断是否有某个应用了
		List<PackageInfo> list = ctx.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);

		for (PackageInfo packageInfo : list) {
			if (packageInfo.packageName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static void installApp(Context ctx, String packageName) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + packageName), "application/vnd.android.package-archive");
		ctx.startActivity(i);
	}

	public static void startAnotherApp(Context ctx, String packageName, String userToken) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(packageName, 0);
			if (packageInfo == null) {
				System.out.println("packageInfo==null");
			} else {
				System.out.println("packageInfo!=null");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageInfo.packageName);

		List<ResolveInfo> resolveInfoList = ctx.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveInfo = resolveInfoList.iterator().next();
		if (resolveInfo != null) {
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(activityPackageName, className);

			intent.setComponent(componentName);
			intent.putExtra("token", userToken);
			ctx.startActivity(intent);
		}
	}

	public static String getProperties(String key) {
		String value;
		try {
			Class<?> clazz = Class.forName("android.os.SystemProperties");
			Method methodGet = clazz.getDeclaredMethod("get", String.class);
			//Method methodSet = clazz.getDeclaredMethod("set", String.class, String.class);
			value = (String) methodGet.invoke(clazz.newInstance(), key);
		} catch (Exception e) {
			e.printStackTrace();
			value = null;
		}
		Log.d(TAG, "getProperties: [key, value] = [" + key + ", " + value + "]");
		return value;
	}
}
