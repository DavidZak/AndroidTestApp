package com.example.mradmin.androidtestapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.mradmin.androidtestapp.entities.User;

import java.util.ArrayList;

/**
 * Created by mrAdmin on 10.08.2017.
 */

public class UserDBHelper {

    DBHelper dbHelper;

    public static final String USERS_TABLE_NAME = "users";

    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_NAME = "full_name";
    public static final String USERS_COLUMN_EMAIL = "email";
    public static final String USERS_COLUMN_PASSWORD = "password";

    public UserDBHelper(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean insertUser (String name, String email, String password) {
        return dbHelper.insertUser(name, email, password);
    }

    public User getUserById(int id) {
        Cursor cursor = dbHelper.getDataById(id);
        cursor.moveToFirst();

        User user = null;

        if (cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_NAME));
            String mail = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_PASSWORD));

            String[] nameParts = name.split(" ");

            user = new User(nameParts[0], nameParts[1], mail, password);
        }

        return user;
    }

    public User getUserByEmail(String email) {
        Cursor cursor = dbHelper.getDataByEmail(email);
        cursor.moveToFirst();

        User user = null;

        if (!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_NAME));
            String mail = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(USERS_COLUMN_PASSWORD));

            String[] nameParts = name.split(" ");

            user = new User(nameParts[0], nameParts[1], mail, password);
            System.out.println(nameParts[0]+" ------------ " + nameParts[1]);
        }

        return user;
    }

    public int numberOfRows(){
        return dbHelper.numberOfRows();
    }

    public boolean updateUser (Integer id, String name, String email, String password) {
        return dbHelper.updateUser(id, name, email, password);
    }

    public Integer deleteUser (Integer id) {
        return dbHelper.deleteUser(id);
    }

    public ArrayList<String> getAllUsers() {
        return dbHelper.getAllUsers();
    }
}
