package com.app.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.log.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class DBHelper {

	private static boolean mainTmpDirSet = false;
	private static final String TAG = "MIBRIDGE.DB";

	private HashMap<String, SQLiteDatabase> dbMap;
	private DBVersionManager versionManager;
	private Context mContext;

	private static DBHelper instance = new DBHelper();
	private static final boolean DEBUG = true;

	private DBHelper() {
		dbMap = new HashMap<String, SQLiteDatabase>();
	}

	public static DBHelper getInstance() {
		return instance;
	}

	public void init(Context context, DBVersionManager versionManager) {
		this.mContext = context;
		this.versionManager = versionManager;
	}

	public void initDB(String dbName, int dbVersion, DBPathBuilder builder) {
		if (builder == null) {
			// 不指定build，用android自带的机制来管理
			MySQLiteOpenHelper realHelper = new MySQLiteOpenHelper(dbName, dbVersion);
			SQLiteDatabase db = realHelper.getWritableDatabase();
			db.setLocale(Locale.CHINA);
			dbMap.put(dbName, db);
		} else {
			String dbFilePath = builder.getDBPath(dbName);
			// 打开数据库-->dbFilePath
			// SQLiteDatabase.NO_LOCALIZED_COLLATORS |
			// SQLiteDatabase.OPEN_READWRITE
			// SQLiteDatabase db =
//			SQLiteDatabase.openOrCreateDatabase(dbFilePath, null);
			SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFilePath, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			// 检查是否内部初始化过了。内部没有初始化过了，就是新的数据库。
			if (checkDatabaseHasInitInternal(db)) {
				// 有，则检查对比版本号
				int oldVersion = this.getCurrDBVersion(db);
				if (dbVersion != oldVersion) {
					// 版本号不一样，需要升级，调用onUpgrade
					versionManager.onUpgrade(db, dbName, oldVersion, dbVersion);
					// 然后内部更新版本信息
					this.upgradeDatabaseInternal(db, dbVersion);
				}
			} else {
				// 新数据库，调用onCreate，让外部去初始化一下
				versionManager.onCreate(db, dbName, dbVersion);
				// 然后，内部再初始化一下
				this.initDatabaseInternal(db, dbVersion);
			}
			db.setLocale(Locale.CHINA);
			dbMap.put(dbName, db);
		}
	}

	private boolean checkDatabaseHasInitInternal(SQLiteDatabase db) {
		Cursor c = db.rawQuery(
				" select count(*) from sqlite_master where type='table' and name = 'dbVersion'",
				null);
		if (c != null && c.moveToFirst()) {
			int count = c.getInt(0);
			c.close();
			if (count > 0) {
				return true;
			}
		}
		return false;
	}

	private int getCurrDBVersion(SQLiteDatabase db) {
		// 获取数据库中当前的数据库版本号
		// 从dbVersion表读取即可
		Cursor c = db.rawQuery(" select curr_db_version from dbVersion", null);
		if (c != null && c.moveToFirst()) {
			int version = c.getInt(0);
			c.close();
			return version;
		}
		return 0;
	}

	private void initDatabaseInternal(SQLiteDatabase db, int version) {
		// 需要创建dbVersion表
		String sql = "create table dbVersion(curr_db_version integer not null,curr_app_version integer not null)";
		db.execSQL(sql);
		// 插入一条数据
		sql = "insert into dbVersion(curr_db_version,curr_app_version) values(" + version + ",0)";
		db.execSQL(sql);
	}

	private void upgradeDatabaseInternal(SQLiteDatabase db, int version) {
		// 需要更新dbVersion表中的版本号
		String sql = "update dbVersion set curr_db_version=" + version;
		db.execSQL(sql);
	}

	public SQLiteDatabase getDB(String dbName) {
		return this.dbMap.get(dbName);
	}

	public void closeDB(String dbName) {
		SQLiteDatabase db = this.dbMap.remove(dbName);
		if (db != null) {
			db.close();
		}
	}

	private class MySQLiteOpenHelper extends SQLiteOpenHelper {

		private String dbName;
		private int dbVersion;

		MySQLiteOpenHelper(String dbName, int dbVersion) {
			super(mContext, dbName, null, dbVersion);
			this.dbName = dbName;
			this.dbVersion = dbVersion;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (DEBUG) {
				Log.debug(TAG, "MySQLiteOpenHelper.onCreate()");
			}
			versionManager.onCreate(db, dbName, dbVersion);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (DEBUG) {
				Log.debug(TAG, "MySQLiteOpenHelper.onUpgrade()," + oldVersion + "-->" + newVersion);
			}
			versionManager.onUpgrade(db, dbName, oldVersion, newVersion);
		}
		
		// 第一次建立sqliite连接时创建临时文件（sqlite3会在不需要这个临时文件时自动删除它）
		@Override
		public synchronized SQLiteDatabase getReadableDatabase() {
			if (!mainTmpDirSet) {
				try {
					String path = "/data/data/" + mContext.getPackageName() + "/databases/main";
					boolean rs = new File(path).mkdir();
					Log.debug(TAG, "getReadableDatabase " + rs);
					super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '"+ path + "'");
					mainTmpDirSet = true;
				} catch (SQLException e) {
				}
			}
			return super.getReadableDatabase();
		}

		// 第一次建立sqliite连接时创建临时文件（sqlite3会在不需要这个临时文件时自动删除它）
		@Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			if (!mainTmpDirSet) {
				try {
					String path = "/data/data/" + mContext.getPackageName() + "/databases/main";
					boolean rs = new File(path).mkdir();
					Log.debug(TAG, rs + "");
					super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '" + path + "'");
					mainTmpDirSet = true;
				} catch (SQLException e) {
				}
			}
			return super.getReadableDatabase();
		}
	}

	// 数据库查询
	public static Cursor rawQuery(SQLiteDatabase db, String sql, String[] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}

	// 数据库执行
	public void execSQL(SQLiteDatabase db, String sql, Object[] bindArgs) {
		try {
			db.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
