package com.app.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 从asserts目录下打开一个文件，边解析边执行里面的sql语句 碰到了错误就抛出来。也就是说，只要有一个sql执行错误，后续的都不会被执行。
 * 文件里面以--开始的行为注释行，2个注释行之间的为代码行。
 */
public class SQLFile {
    private static final String TAG = "MIBRIDGE.DB";
    private static final String COMMENT_PREFIX = "--";
    private static final String SQL_END = ";";
    private Context context;
    private String filename;
    private SQLiteDatabase db;

    public SQLFile(Context context, SQLiteDatabase db, String filename) {
        this.context = context;
        this.filename = filename;
        this.db = db;
    }

    public void execute() throws Exception {
        // 打开文件
        BufferedReader reader = null;
        try {
            InputStream is = this.context.getAssets().open(this.filename);
            reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            String sql = "";
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    // 读到了null，表示文件已经读完了

                    // 如果sql不为空，则需要执行下
                    if (!"".equals(sql)) {
                        executeSQL(sql);
                    }
                    // 解析完毕，退出。
                    break;
                }

                if (line.startsWith(COMMENT_PREFIX)) {
                    // 发现了一个注释行
                    // 执行已经解析的sql
                    if (!"".equals(sql)) {
                        executeSQL(sql);
                    }
                    sql = "";
                    // 解析下一行
                    continue;
                }

                // 把前后的空格去掉
                line = line.trim();
                if ("".equals(line)) {
                    // 是空行，啥也不用做
                } else {
                    // 不是空行，那就是代码行
                    sql = sql + line;
                    if (line.endsWith(SQL_END)) {
                        // 分号结束，表明一个sql结束了，执行已经解析的sql
                        if (!"".equals(sql)) {
                            executeSQL(sql);
                        }
                        sql = "";
                        // 解析下一行
                        continue;
                    }
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {

                }
            }
        }

    }

    private void executeSQL(String sql) {
        if (true) {
            Log.d(TAG, "execute a sql:\n" + sql);
        }
        try
        {
            this.db.execSQL(sql); 
        }
        catch (Exception e)
        {
           Log.e(TAG, "",e);
        }

    }

}
