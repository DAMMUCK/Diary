package com.kunsan.ac.kr.sosua;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryPassDBHelper extends SQLiteOpenHelper {

    DiaryPassDBHelper(Context context){
        super(context, "Password", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE password (" +
                "'pass' TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists password";
        db.execSQL(sql);
        onCreate(db);
    }
}
