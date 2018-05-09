package com.wuli.delivery.portal.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.db.DBHelper;
import com.wuli.delivery.AppConstants;
import com.wuli.delivery.portal.bean.Expressage;

import java.util.ArrayList;
import java.util.List;

public class ExpressageDao {


    private ExpressageDao() {

    }

    public static void save(Expressage expressage) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("expressage_id", expressage.getExpressageID());
        contentValues.put("expressage_lead_num", expressage.getExpressageLeadNum());
        contentValues.put("expressage_delivery_time", expressage.getDeliveryTime());
        contentValues.put("expressage_delivery_site", expressage.getDeliverySite());
        contentValues.put("expressage_release_time", expressage.getReleaseTime());
        contentValues.put("expressage_desc", expressage.getExpressageDesc());
        contentValues.put("expressage_lead_reward", expressage.getExpressageLeadReward());
        contentValues.put("expressage_lead_type", expressage.getExpressageLeadType());
        contentValues.put("expressage_delivery_user_name", expressage.getDeliveryUserName());
        contentValues.put("expressage_delivery_phone_number", expressage.getDeliveryPhoneNumber());
        contentValues.put("expressage_lead_remark", expressage.getExpressageLeadRemark());
        contentValues.put("expressage_release_status", expressage.getExpressageReleaseStatus());
        contentValues.put("expressage_receive_status", expressage.getExpressageReceiveStatus());
        contentValues.put("expressage_type", expressage.getExpressageType());

        SQLiteDatabase db = DBHelper.getInstance().getDB(AppConstants.DBNAME_COMMON);

        db.insert("express_info", null, contentValues);
    }

    public static List<Expressage> getAllExpressageList() {
        SQLiteDatabase db = DBHelper.getInstance().getDB(AppConstants.DBNAME_COMMON);
        String sql = "select * from express_info";
        List<Expressage> expressageList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                expressageList.add(buildExpressage(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return expressageList;
    }

    /**
     * 更具leadType查找包裹列表
     *
     * @param leadType
     * @return
     */
    public static List<Expressage> getExpressageListByLeadType(String leadType) {

        SQLiteDatabase db = DBHelper.getInstance().getDB(AppConstants.DBNAME_COMMON);
        String sql = "select * from express_info where expressage_lead_type = ?";
        List<Expressage> expressageList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{leadType});
            while (cursor.moveToNext()) {
                expressageList.add(buildExpressage(cursor));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return expressageList;

    }

    public static void updateExpressageReceiveStatusByExpressageID(String expressageID) {
        SQLiteDatabase db = DBHelper.getInstance().getDB(AppConstants.DBNAME_COMMON);
        String sql = "update express_info set expressage_receive_status = ? where expressage_id = ? ";
        db.execSQL(sql, new String[]{"1", expressageID});
    }


    private static Expressage buildExpressage(Cursor cursor) {

        String expressageID = cursor.getString(cursor.getColumnIndex("expressage_id"));
        String expressageLeadNum = cursor.getString(cursor.getColumnIndex("expressage_lead_num"));
        String deliveryTime = cursor.getString(cursor.getColumnIndex("expressage_delivery_time"));
        String delivertSite = cursor.getString(cursor.getColumnIndex("expressage_delivery_site"));
        String releaseTime = cursor.getString(cursor.getColumnIndex("expressage_release_time"));
        String expressageDesc = cursor.getString(cursor.getColumnIndex("expressage_desc"));
        String expressageLeadReward = cursor.getString(cursor.getColumnIndex("expressage_lead_reward"));
        String expressageLeadType = cursor.getString(cursor.getColumnIndex("expressage_lead_type"));
        String deliveryUserName = cursor.getString(cursor.getColumnIndex("expressage_delivery_user_name"));
        String deliveryPhoneNumber = cursor.getString(cursor.getColumnIndex("expressage_delivery_phone_number"));
        String expressageLeadRemark = cursor.getString(cursor.getColumnIndex("expressage_lead_remark"));
        String expressageReleaseStatus = cursor.getString(cursor.getColumnIndex("expressage_release_status"));
        String expressageReceiveStatus = cursor.getString(cursor.getColumnIndex("expressage_receive_status"));
        String expressageType = cursor.getString(cursor.getColumnIndex("expressage_type"));

        return new Expressage.Builder()
                .expressageID(expressageID)
                .expressageLeadNum(expressageLeadNum)
                .deliveryTime(deliveryTime)
                .deliverySite(delivertSite)
                .releaseTime(releaseTime)
                .expressageDesc(expressageDesc)
                .expressageLeadReward(expressageLeadReward)
                .expressageLeadType(expressageLeadType)
                .deliveryUserName(deliveryUserName)
                .deliveryPhoneNumber(deliveryPhoneNumber)
                .expressageLeadRemark(expressageLeadRemark)
                .expressageReleaseStatus(expressageReleaseStatus)
                .expressageReceiveStatus(expressageReceiveStatus)
                .expressageType(expressageType)
                .build();
    }

}
