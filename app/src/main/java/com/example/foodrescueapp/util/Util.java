package com.example.foodrescueapp.util;

import android.content.Intent;

public class Util {

    //All database constants defined here

    //The email address will be the username for a given user. This username
    // will link a user to food items listed in the food table.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user_db";
    public static final String USER_TABLE_NAME = "users";
    public static final String FOOD_TABLE_NAME = "food";
    public static final String USER_FOOD_TABLE_NAME = "users_food";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String NAME = "name";

    public static final String FOOD_ID = "food_id";
    public static final String FOOD_TITLE = "food_title";
    public static final String FOOD_DESCRIPTION = "food_description";
    public static final String FOOD_DATE = "food_pickup_date";
    public static final String FOOD_PICKUP_TIME = "food_pickup_time";
    public static final String FOOD_QUANTITY = "food_quantity";
    public static final String FOOD_LOCATION = "food_location";
    public static final String  FOOD_IMAGE_RES = "food_image_resource";

    // All QUERIES HERE

    //Creating the users table
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "("
            + USERNAME + " TEXT PRIMARY KEY," + NAME + " TEXT," + PASSWORD + " TEXT,"
            + PHONE + " TEXT," + ADDRESS + " TEXT)";

    //Creating the foodItem table
    public static final String CREATE_FOOD_TABLE = "CREATE TABLE " + FOOD_TABLE_NAME + "("
            + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FOOD_TITLE + " TEXT," + FOOD_DESCRIPTION + "TEXT," + FOOD_IMAGE_RES + " TEXT,"+ FOOD_DATE + " TEXT"
            + FOOD_PICKUP_TIME + " TEXT," + FOOD_QUANTITY + " TEXT," + FOOD_LOCATION + " TEXT)";

    //Creating the users_food table
    public static final String CREATE_USER_FOOD_TABLE = "CREATE TABLE " + USER_FOOD_TABLE_NAME + "(" + USERNAME + " TEXT,"
            + FOOD_ID + " TEXT," + " PRIMARY KEY(" + USERNAME + "," + FOOD_ID + "))";
}