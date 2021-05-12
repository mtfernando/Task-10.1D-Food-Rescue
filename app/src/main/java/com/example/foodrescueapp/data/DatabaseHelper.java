package com.example.foodrescueapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.UniversalTimeScale;

import androidx.annotation.Nullable;

import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Util;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Util.DATABASE_NAME, factory, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executing SQL from Util class
        db.execSQL(Util.CREATE_USER_TABLE);
        db.execSQL(Util.CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_USER_TABLE = "DROP TABLE IF EXISTS";
        db.execSQL(DROP_USER_TABLE, new String[] {Util.USER_TABLE_NAME, Util.FOOD_TABLE_NAME});

        onCreate(db);
    }

    public long createUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.USERNAME, user.getUsername());
        values.put(Util.NAME, user.getName());
        values.put(Util.PHONE, user.getPhone());
        values.put(Util.ADDRESS, user.getAddress());

        //Inserting row into user table
        long result = db.insert(Util.USER_TABLE_NAME, null, values);

        return result;
    }

    public long createFoodItem(User user, FoodItem foodItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.USERNAME, user.getUsername());
        values.put(Util.FOOD_TITLE, foodItem.getTitle());
        values.put(Util.FOOD_DESCRIPTION, foodItem.getDescription());
        values.put(Util.FOOD_DATE, foodItem.getPickupDate());
        values.put(Util.FOOD_PICKUP_TIME, foodItem.getPickupTime());
        values.put(Util.FOOD_QUANTITY, foodItem.getQuantity());
        values.put(Util.FOOD_IMAGE_RES, foodItem.getImageRes());

        //Inserting row into foodItem table
        long result = db.insert(Util.FOOD_TABLE_NAME, null, values);
        return  result;
    }
}
