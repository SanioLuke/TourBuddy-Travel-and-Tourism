package com.example.toursimapp.LocalDB;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.toursimapp.Models.SearchItemModel;

import java.util.ArrayList;

@SuppressWarnings("all")
public class RecentPlaceDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RecentDatabase";
    public static final String RECENTPLACE_TABLE = "RecentPlaceCollection";
    public static final String REC_COL_1 = "collection";
    public static final String REC_COL_2 = "main_place";
    public static final String REC_COL_3 = "sub_place";
    public static final String REC_COL_4 = "place_name";
    public static final String REC_COL_5 = "place_img";
    public static final Integer ID = 0;
    String CREATE_RECENT_TABLE = "CREATE TABLE " + RECENTPLACE_TABLE + "(id INTEGER PRIMARY KEY, " + REC_COL_1 + " VARCHAR, " + REC_COL_2 + " VARCHAR, " + REC_COL_3 + " VARCHAR, " + REC_COL_4 + " VARCHAR, " + REC_COL_5 + " VARCHAR)";

    public RecentPlaceDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RECENTPLACE_TABLE);
        onCreate(db);
    }

    public boolean addRecentPlace(String collection, String main_place, String inner_place, String place_name, String place_img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REC_COL_1, collection);
        contentValues.put(REC_COL_2, main_place);
        contentValues.put(REC_COL_3, inner_place);
        contentValues.put(REC_COL_4, place_name);
        contentValues.put(REC_COL_5, place_img);
        db.insert(RECENTPLACE_TABLE, null, contentValues);
        return true;
    }

    public void deleteRecentPlace() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + RECENTPLACE_TABLE);
        db.close();
    }

    public boolean deleteRecentPlace(String collection, String main_place, String place_name, String place_img) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_1 = "DELETE FROM " + RECENTPLACE_TABLE + " WHERE " + REC_COL_1 + "= '" + collection + "' AND " + REC_COL_2 + "= '" + main_place + "' AND " + REC_COL_4 + "= '" + place_name + "' AND " + REC_COL_5 + "= '" + place_img + "'";
        Log.d(TAG, "deleteName: query: " + query_1);
        db.execSQL(query_1);
        return true;
    }

    public long checkRecentTableExist() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + RECENTPLACE_TABLE + "'", null);
        return cursor.getCount();
    }

    public ArrayList<String> getRecentcolname() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENTPLACE_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_1)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<String> getRecentPlaceName() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENTPLACE_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_4)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<SearchItemModel> getRecentPlacesArrays() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<SearchItemModel> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENTPLACE_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(new SearchItemModel(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_1)), cursor_1.getString(cursor_1.getColumnIndex(REC_COL_2)),
                    cursor_1.getString(cursor_1.getColumnIndex(REC_COL_3)), cursor_1.getString(cursor_1.getColumnIndex(REC_COL_4))
                    , cursor_1.getString(cursor_1.getColumnIndex(REC_COL_5))));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

}
