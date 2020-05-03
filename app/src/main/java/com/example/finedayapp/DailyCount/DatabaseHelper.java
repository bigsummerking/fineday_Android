package com.example.finedayapp.DailyCount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String COST_MONEY = "cost_money";
    public static final String COST_DATE = "cost_date";
    public static final String COST_TITLE = "cost_title";
    public static final String DAILY = "Daily";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "DailyApp", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Daily(" +
                "id integer primary key, " +
                "cost_title varchar, " +
                "cost_date varchar, " +
                "cost_money varchar)");
    }

    public void insertCost(CostBean costBean) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COST_TITLE, costBean.getCostTitle());
        cv.put(COST_DATE, costBean.getCostDate());
        cv.put(COST_MONEY, costBean.getCostMoney());
        database.insert(DAILY, null, cv);
    }

    public Cursor getAllCostData() {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(DAILY, null, null, null, null, null, COST_DATE + " DESC");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void removeCost(CostBean costBean) {
        SQLiteDatabase database = getWritableDatabase();
        String deleteSQL = "delete from " + DAILY + " where " +
                COST_MONEY + "=" + "'" + costBean.getCostMoney() + "'" + " and " +
                COST_DATE + "=" + "'" + costBean.getCostDate() + "'" + " and " +
                COST_TITLE + "=" + "'" + costBean.getCostTitle() + "'";
        getWritableDatabase().execSQL(deleteSQL);
    }
}
