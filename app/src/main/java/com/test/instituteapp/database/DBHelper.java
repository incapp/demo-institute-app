package com.test.instituteapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.test.instituteapp.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE notification(" +
            "ndate DATETIME, " +
            "title VARCHAR(100), " +
            "message VARCHAR(200), " +
            "PRIMARY KEY (ndate, title, message)" +
            ");";

    public DBHelper(@NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public List<NotificationModel> saveNotification(List<NotificationModel> list) {
        List<NotificationModel> temp = new ArrayList<>();
        ContentValues cv = new ContentValues();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (sqLiteDatabase != null) {
            for (int i = 0; i < list.size(); i++) {
                cv.put("ndate", list.get(i).getDate().replace("T", " "));
                cv.put("title", list.get(i).getTitle());
                cv.put("message", list.get(i).getMessage());
                try {
                    sqLiteDatabase.insertOrThrow("notification", null, cv);
                    temp.add(list.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sqLiteDatabase.close();
        }
        return temp;
    }

    public List<NotificationModel> getAllNotifications() {
        List<NotificationModel> list = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.query("notification",
                new String[]{"ndate, title, message"},
                null,
                null,
                null,
                null,
                "ndate DESC");

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                NotificationModel notificationModel = new NotificationModel();

                notificationModel.setDate(cursor.getString(cursor.getColumnIndex("ndate")));
                notificationModel.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                notificationModel.setMessage(cursor.getString(cursor.getColumnIndex("message")));

                list.add(notificationModel);
            }
            cursor.close();
        }

        sqLiteDatabase.close();

        return list;
    }
}
