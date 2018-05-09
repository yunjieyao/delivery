package com.app.db;

import android.database.sqlite.SQLiteDatabase;

public interface DBVersionManager {
    void onCreate(SQLiteDatabase db,String name,int version);
    
    void onUpgrade(SQLiteDatabase db,String name,int oldVersion ,int newVersion);

}
