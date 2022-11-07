package com.example.blogapp.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.blogapp.models.User;

public class UserDatabaseAdapter {

    private static String dbName = "UserDb";
    private static int dbVersion = 3;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public UserDatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context, dbName, null, dbVersion);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void insert(String name, String email, String password, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("image", image);
        db.insert("user", null, contentValues);
    }

    public User getUserWithId(String userId) {
        User user = new User();
        Cursor cursor = db.query("user", null, "id=?", new String[]{userId}, null, null, null);

        while (cursor.moveToNext()) {
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
        }

        return user;
    }

    public int getUserId(String email) {
        Cursor cursor = db.query("user", null, "email=?", new String[]{email}, null, null, null);
        int id = -1;
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        }
        return id;
    }

    public void updateUserWithId(String id, String name, String email, String password, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("image", image);

        db.update("user", contentValues, "id=?", new String[]{id});
    }

    public boolean getUserEmail(String email) {
        Cursor cursor = db.rawQuery("select * from user where email=?", new String[]{email});
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean login(String email, String password) {
        Cursor cursor = db.rawQuery("select * from user where email=? and password=?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE user( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "password TEXT," +
                    "image BLOB)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE user");
            sqLiteDatabase.execSQL("CREATE TABLE user( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "email TEXT," +
                    "password TEXT," +
                    "image BLOB)");
        }
    }
}
