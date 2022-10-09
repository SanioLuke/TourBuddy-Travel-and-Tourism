package com.example.toursimapp.LocalDB;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.toursimapp.Models.SearchItemModel;

import java.util.ArrayList;

@SuppressWarnings("all")
public class AnalyticDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Analysis";
    public static final String TABLE_NAME = "AnalyseCollection";
    public static final String COL_1 = "collection";
    public static final String COL_2 = "main_place";
    public static final String COL_3 = "sub_place";
    public static final String COL_4 = "place_name";
    public static final Integer ID = 0;
    public static final String RECENT_TABLE = "RecentSearchCollection";
    public static final String REC_COL_1 = "collection";
    public static final String REC_COL_2 = "main_place";
    public static final String REC_COL_3 = "sub_place";
    public static final String REC_COL_4 = "place_name";
    public static final String REC_COL_5 = "place_img";
    private static final String[] all_columns = {REC_COL_1, REC_COL_2, REC_COL_3, REC_COL_4, REC_COL_5};
    String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY, " + COL_1 + " VARCHAR)";
    String CREATE_RECENT_TABLE = "CREATE TABLE " + RECENT_TABLE + "(id INTEGER PRIMARY KEY, " + REC_COL_1 + " VARCHAR, " + REC_COL_2 + " VARCHAR, " + REC_COL_3 + " VARCHAR, " + REC_COL_4 + " VARCHAR, " + REC_COL_5 + " VARCHAR)";


    public AnalyticDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_RECENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldi, int newi) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_TABLE);
        onCreate(db);
    }

    public boolean addPlace(String collection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d("collection", "" + collection);
        contentValues.put(COL_1, collection);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean addRecentPlace(String collection, String main_place, String inner_place, String place_name, String place_img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REC_COL_1, collection);
        contentValues.put(REC_COL_2, main_place);
        contentValues.put(REC_COL_3, inner_place);
        contentValues.put(REC_COL_4, place_name);
        contentValues.put(REC_COL_5, place_img);
        db.insert(RECENT_TABLE, null, contentValues);
        return true;
    }

    public void deletePlace() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public boolean deleteRecentPlace(String collection, String main_place, String inner_place, String place_name, String place_img) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query_1 = "DELETE FROM " + RECENT_TABLE + " WHERE " + REC_COL_1 + "= '" + collection + "' AND " + REC_COL_2 + "= '" + main_place + "' AND " + REC_COL_4 + "= '" + place_name + "' AND " + REC_COL_5 + "= '" + place_img + "'";
        Log.d(TAG, "deleteName: query: " + query_1);
        db.execSQL(query_1);
        return true;
    }

    public void deleteAllRecentPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + RECENT_TABLE);
        db.close();
    }

    public long getdbcount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    public long getrecentdatacount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, RECENT_TABLE);
        db.close();
        return count;
    }

    public long getall_db(String db_name) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(sqLiteDatabase, "SELECT COUNT (*) FROM " + TABLE_NAME + " WHERE " + COL_1 + "=?",
                new String[]{db_name});
    }

    public ArrayList<String> getRecentcolname() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_1)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<String> getRecentMainPlace() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_2)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<String> getRecentInnerPlace() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_3)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<String> getRecentPlaceName() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_4)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<String> getRecentPlaceImg() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_5)));
            cursor_1.moveToNext();
        }
        return arrayList;
    }

    public ArrayList<ArrayList<String>> getRecentArrayData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<ArrayList<String>> main_array = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        for (String all_column : all_columns) {

            if (arrayList.size() > 0) {
                arrayList.clear();
            }

            while (!cursor_1.isAfterLast()) {
                arrayList.add(cursor_1.getString(cursor_1.getColumnIndex(all_column)));
                cursor_1.moveToNext();
            }
            main_array.add(arrayList);
        }
        return main_array;
    }

    public ArrayList<SearchItemModel> getRecentArrayData1() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<SearchItemModel> main_array = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor_1 = sqLiteDatabase.rawQuery("select * from " + RECENT_TABLE, null);
        cursor_1.moveToFirst();
        while (!cursor_1.isAfterLast()) {
            SearchItemModel arrayList = new SearchItemModel(cursor_1.getString(cursor_1.getColumnIndex(REC_COL_1)),
                    cursor_1.getString(cursor_1.getColumnIndex(REC_COL_2)),
                    cursor_1.getString(cursor_1.getColumnIndex(REC_COL_3)),
                    cursor_1.getString(cursor_1.getColumnIndex(REC_COL_4)),
                    cursor_1.getString(cursor_1.getColumnIndex(REC_COL_5)));
            main_array.add(arrayList);
            cursor_1.moveToNext();
        }
        return main_array;
    }
}
