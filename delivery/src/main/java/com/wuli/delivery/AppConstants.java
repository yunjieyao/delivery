package com.wuli.delivery;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.app.log.Log;

import java.io.File;

public class AppConstants {

    public static final String DBNAME_COMMON = "portalcommon";

    public static final String DBNAME_COMMON_FILE = "portal.db";

    // 数据库存放目录
    public static String DB_DIR;
    // 数据外置存储目录
    public static String PKG_DIR;

    public static String DATA_ROOT_DIR;     // 数据文件根目录
    public static String DATA_ROOT_DIR_OUTER; // 外置卡中数据文件根目录(系统拍照等产生的文件，不允许存放到应用系统目录下)
    public static String CACHE_ROOT_DIR;    // 缓存根目录
    public static String DB_ROOT_DIR;       // 数据库根目录

    public final static class DB {

        public final static String EXPRESSAGE_LEAD_TYPE_RELEASE = "1";
        public final static String EXPRESSAGE_LEAD_TYPE_RECEIVE = "2";
    }

    public static void init(Context context) {
        final boolean innerFirst = true;
        // 优先使用app系统目录，即/data/data/com.pagoda.xxx/
        if (innerFirst) {
            CACHE_ROOT_DIR = context.getCacheDir().getPath();
            DATA_ROOT_DIR = context.getFilesDir().getPath();
            DB_ROOT_DIR = context.getFilesDir().getParent();

            String outerPath = getExternalFilesDir(context).getPath();
            DATA_ROOT_DIR_OUTER = outerPath;

            Log.debug("Ville", "DataPath = [" + DATA_ROOT_DIR + "] \n" +
                    "DBPath = [" + DB_ROOT_DIR + "] \n" +
                    "DataPathOuter = [" + DATA_ROOT_DIR_OUTER + "]");
        } else {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                CACHE_ROOT_DIR = getExternalCacheDir(context).getPath();
                DATA_ROOT_DIR = getExternalFilesDir(context).getPath();
                DATA_ROOT_DIR_OUTER = DATA_ROOT_DIR;
                DB_ROOT_DIR = DATA_ROOT_DIR;
            } else {
                CACHE_ROOT_DIR = context.getCacheDir().getPath();
                DATA_ROOT_DIR = context.getFilesDir().getPath();
                DB_ROOT_DIR = context.getFilesDir().getParent();
                DATA_ROOT_DIR_OUTER = DATA_ROOT_DIR;
            }
        }

        DB_DIR = new File(DB_ROOT_DIR, "databases").getPath();
        PKG_DIR = new File(DATA_ROOT_DIR_OUTER, "pkg_data").getPath();


    }

    public static File getExternalFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalFilesDir(null);

            if (path != null) {
                return path;
            }
        }
        final String filesDir = "/Android/data/" + context.getPackageName() + "/files/";
        return new File(Environment.getExternalStorageDirectory().getPath() + filesDir);
    }

    public static File getExternalCacheDir(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalCacheDir();

            if (path != null) {
                return path;
            }
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}
