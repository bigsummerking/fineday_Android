package com.example.finedayapp.MyMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MapDatabaseHelper extends SQLiteOpenHelper {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String MyMap = "MyMap";

    public MapDatabaseHelper(@Nullable Context context) {
        super(context, "MyMap", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists MyMap(" +
                "id integer primary key, " +
                "latitude TEXT, " +
                "longitude TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertCost(LocationBean locationBean) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LONGITUDE, locationBean.getLongitude());
        cv.put(LATITUDE, locationBean.getLatitude());
        database.insert(MyMap, null, cv);
        return true;
    }

    public Cursor getAllLocationData() {
        SQLiteDatabase database = getWritableDatabase();
        return database.query(MyMap, null, null, null, null, null, null);
    }

    public void deleteAllLocationData() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MyMap, null, null);
    }


}
