package com.app;

import android.content.Context;

import com.android.icdispatch.ICDispatchApp;

public abstract class CoreApp extends ICDispatchApp {

	public static Context getContext() {
		return context;
	}

	public static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();

//		if(!AndroidUtil.isMainProcess(this)) {
//			Log.d("CoreApp", "start CoreApp not called by main process, so skip!!");
//			return;
//		}
//		// 初始化config
//		Config.getInstance().init(context, "config/config.xml");
//
//		// 通过config的配置，实现动态初始化LogManager
//		String logPath = "config/log.xml";
//		ConfigModule module = Config.getInstance().getMoudle("Engine");
//		if (module != null) {
//			// String type = module.getStringItem("releaseType", "");
//			String type = getConfiguredReleaseType();
//			String logCfgPath = module.getStringItem(type + ".logConfig", "");
//			if (!"".equals(type) && !"".equals(logCfgPath)) {
//				logPath = logCfgPath;
//				android.util.Log.d("CoreApp", "logPath: " + logPath);
//			} else {
//				android.util.Log.w("CoreApp", "config/engine.xml文件缺少相关字段!");
//			}
//		}
//		// 使用adb shell getprop debug.pagoda.log.enable 来查看该系统属性，如果为1，则强制开启log
//		// 若需要在app运行时，强制开启log，只需要设置debug.pagoda.log.enable为1,然后重启app即可
//		// (执行 adb shell setprop debug.pagoda.log.enable 1)
//		if("1".equals(AndroidUtil.getProperties("debug.pagoda.log.enable"))) {
//			logPath = "config/log_debug.xml";
//		}
//		LogManager.getInstance().init(context, logPath);
//
//		SafeControl.getSafeControl().init(context);
//		CustomerException.getExceptionControl().init(context);
//
//		// 初始化网络状态控制组件
//		NetConnectivity.getConnectivityManager().init(context);
//
//		// 初始化 db 组件
//		DBHelper.getInstance().init(context, new AssetDBVersionManager(context));
	}

	//
//	// 必须在BuildConfig中定义，该值将取代engine.xml中releaseType的值
//	public String getConfiguredReleaseType() {
//		return "";
//	}


}
