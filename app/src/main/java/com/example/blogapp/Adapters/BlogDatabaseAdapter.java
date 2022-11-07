package com.example.blogapp.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.blogapp.models.Blog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BlogDatabaseAdapter {
    private static String dbName = "BlogDb";
    private static int dbVersion = 3;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BlogDatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context, dbName, null, dbVersion);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void insert(int userId, String title, String description, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("image", image);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        contentValues.put("time", strDate);
        db.insert("blog", null, contentValues);
    }

    public ArrayList<Blog> getBlogs() {
        ArrayList<Blog> blogs = new ArrayList<Blog>();
        Cursor cursor = db.query("blog", null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            Blog blog = new Blog();
            blog.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            blog.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
            blog.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            blog.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            blog.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
            blog.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
//            Log.d("BlogDatabaseAdapter", cursor.getString(cursor.getColumnIndexOrThrow("time")));
            blogs.add(blog);
        }
        return blogs;
    }

    public ArrayList<Blog> getBlogsWithUserId(String userId) {
        ArrayList<Blog> blogs = new ArrayList<>();
        Cursor cursor = db.query("blog", null, "userId=?", new String[]{userId}, null, null, null);

        while (cursor.moveToNext()) {
            Blog blog = new Blog();
            blog.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            blog.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
            blog.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            blog.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            blog.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
            blog.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
            Log.d("BlogDatabaseAdapter", cursor.getString(cursor.getColumnIndexOrThrow("time")));
            blogs.add(blog);
        }
        return blogs;
    }

    public Blog getBlogWithId(String id) {
        Blog blog = new Blog();
        Cursor cursor = db.query("blog", null, "id=?", new String[]{id}, null, null, null);

        while (cursor.moveToNext()) {
            blog.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            blog.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("userId")));
            blog.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            blog.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            blog.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
            blog.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
//            Log.d("BlogDatabaseAdapter", cursor.getString(cursor.getColumnIndexOrThrow("time")));
        }
        return blog;
    }

    public void updateBlogWithId(String id, String title, String description, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("image", image);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        contentValues.put("time", strDate);

        db.update("blog", contentValues, "id=?", new String[]{id});
    }

    public void deleteBlogWithId(String id) {
        db.delete("blog", "id=?", new String[]{id});
    }

    public void delete(int id) {
        db.delete("blog", "id=" + id, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE blog( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER," +
                    "title TEXT," +
                    "description TEXT," +
                    "image BLOB," +
                    "time TEXT DEFAULT CURRENT_TIMESTAMP)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE blog");
            sqLiteDatabase.execSQL("CREATE TABLE blog( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "userId INTEGER," +
                    "title TEXT," +
                    "description TEXT," +
                    "image BLOB," +
                    "time TEXT DEFAULT CURRENT_TIMESTAMP)");
        }
    }
}


