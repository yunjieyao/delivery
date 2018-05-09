package com.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AssetDBVersionManager implements DBVersionManager {
	private static final String TAG = "MIBRIDGE.DB";
	private static final String DB_CREATE_FILE_NAME = "create.sql";
	private static final String DB_UPGRADE_FILE_NAME = "upgrade.sql";
	private static final boolean DEBUG = true;

	private Context context;

	public AssetDBVersionManager(Context context) {
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db, String name, int version) {
		if (DEBUG) {
			Log.d(TAG, "AssetDBVersionManager.onCreate()");
		}
		try {
			// 执行创建sql文件
			String sqlfile = name + '/' + DB_CREATE_FILE_NAME;
			if (DEBUG) {
				Log.d(TAG, "createFilename==" + sqlfile);
			}
			SQLFile sqlFile = new SQLFile(context, db, sqlfile);
			sqlFile.execute();
		} catch (Exception e) {
			Log.e(TAG, "Database onCreate failed!", e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, String name, int oldVersion, int newVersion) {
		if (DEBUG) {
			Log.d(TAG, "DBHelper.onUpgrade()," + oldVersion + "-->" + newVersion);
		}
		try {
			String upgradeFile = DB_UPGRADE_FILE_NAME + '.' + oldVersion + '_' + newVersion;
			if (DEBUG) {
				Log.d(TAG, "check upgradeFilename[" + upgradeFile + "] exists or not...");
			}
			// 先找找从指定oldVersion升级到newVersion的文件有没有
			String[] list = context.getAssets().list(name);
			boolean isFind = false;
			for (String filename : list) {
				if (upgradeFile.equals(filename)) {
					isFind = true;
				}
			}

			String upgradeFilename = null;
			if (isFind) {
				// 找到了就用找到的那个文件
				upgradeFilename = name + '/' + upgradeFile;
				if (DEBUG) {
					Log.d(TAG, "exists,upgradeFilename==" + upgradeFilename);
				}
			} else {
				// 没有，用缺省的
				upgradeFilename = name + '/' + DB_UPGRADE_FILE_NAME;
				if (DEBUG) {
					Log.d(TAG, "NOT exists,upgradeFilename==" + upgradeFilename);
				}
			}

			SQLFile sqlFile = new SQLFile(context, db, upgradeFilename);
			sqlFile.execute();
		} catch (Exception e) {
			Log.e(TAG, "Database onUpgrade failed!", e);
		}
	}

}
