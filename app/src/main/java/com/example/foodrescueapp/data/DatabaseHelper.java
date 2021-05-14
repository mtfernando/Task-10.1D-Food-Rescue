package com.example.foodrescueapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.UniversalTimeScale;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Util;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Executing SQL from Util class
        db.execSQL(Util.CREATE_USER_TABLE);
        db.execSQL(Util.CREATE_FOOD_TABLE);
        db.execSQL(Util.CREATE_USER_FOOD_TABLE);
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
        values.put(Util.PASSWORD, user.getPassword());

        //Inserting row into user table
        long result = db.insert(Util.USER_TABLE_NAME, null, values);

        return result;
    }

    //Creating new FoodItem. Entering into food table and users_food table
    public long createFoodItem(User user, FoodItem foodItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.FOOD_TITLE, foodItem.getTitle());
        values.put(Util.FOOD_DESCRIPTION, foodItem.getDescription());
        values.put(Util.FOOD_DATE, foodItem.getPickupDate());
        values.put(Util.FOOD_PICKUP_TIME, foodItem.getPickupTime());
        values.put(Util.FOOD_QUANTITY, foodItem.getQuantity());
        values.put(Util.FOOD_IMAGE_RES, foodItem.getImageRes());

        //Inserting row into foodItem table
        long result = db.insert(Util.FOOD_TABLE_NAME, null, values);

        //Inserting row into linking table users_food
        long linkingResult = createUserFoodEntry(user, foodItem);

        return  result;
    }

    //Inserts a row in the linking table USER_FOOD_TABLE
    public long createUserFoodEntry(User user, FoodItem foodItem){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.USERNAME, user.getUsername());
        values.put(Util.FOOD_ID, foodItem.getFoodID());

        //Inserting row into users_food table
        long result = db.insert(Util.USER_FOOD_TABLE_NAME, null, values);
        return result;
    }

    //Returns a list of foodID for a given username from user_food table
    public String[] getFoodIDList(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        //Fetching from USER_FOOD_TABLE
        String FETCH_FOOD_ID = "SELECT * FROM" + Util.USER_FOOD_TABLE_NAME + " WHERE "
                + Util.USERNAME + " = \"" + username + "\"";

        Cursor c = db.rawQuery(FETCH_FOOD_ID, null);

        String[] foodIDArray = new String[c.getCount()];
        Integer i = 0;

        c.moveToFirst();
        while (!c.isAfterLast()) {
            foodIDArray[i] = c.getString(c.getColumnIndex(Util.FOOD_ID));
            i++;
            c.moveToNext();
        }

        return foodIDArray;
    }
    //Returns the User object for a given username
    public User getUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        //Fetching from USER_TABLE_NAME for a given USERNAME
        String FETCH_USER = "SELECT * FROM " + Util.USER_TABLE_NAME +
                " WHERE " + Util.USERNAME + " = \"" + username + "\"";

        Log.e(String.valueOf(LOG), FETCH_USER);

        Cursor c = db.rawQuery(FETCH_USER, null);

        if (c.moveToFirst()){
            //Creating new user object from cursor
            User user = new User();
            user.setUsername(c.getString(c.getColumnIndex(Util.USERNAME)));
            user.setName(c.getString(c.getColumnIndex(Util.NAME)));
            user.setPhone(c.getString(c.getColumnIndex(Util.PHONE)));
            user.setAddress(c.getString(c.getColumnIndex(Util.ADDRESS)));
            user.setPassword(c.getString(c.getColumnIndex(Util.PASSWORD)));

            return user;
        }

        //-1 will represent an error
        else return new User("-1", "-1","-1","-1","-1");
    }

    //Returns the foodItem for a given foodID
    public FoodItem getFoodItem(Integer foodID){
        SQLiteDatabase db = getWritableDatabase();

        String FETCH_FOOD_ITEM = "SELECT * FROM" + Util.FOOD_TABLE_NAME + " WHERE " + Util.FOOD_ID + " = \"" + foodID + "\"";

        Cursor c = db.rawQuery(FETCH_FOOD_ITEM, null);

        if(c!=null) c.moveToFirst();

        //Creating FoodItem object from cursor
        FoodItem foodItem = new FoodItem();
        foodItem.setFoodID(c.getInt(c.getColumnIndex(Util.FOOD_ID)));
        foodItem.setTitle(c.getString(c.getColumnIndex(Util.FOOD_TITLE)));
        foodItem.setDescription(c.getString(c.getColumnIndex(Util.FOOD_DESCRIPTION)));
        foodItem.setImageRes(c.getString(c.getColumnIndex(Util.FOOD_IMAGE_RES)));
        foodItem.setLocation(c.getString(c.getColumnIndex(Util.FOOD_LOCATION)));
        foodItem.setPickupDate(c.getString(c.getColumnIndex(Util.FOOD_DATE)));
        foodItem.setPickupTime(c.getString(c.getColumnIndex(Util.FOOD_PICKUP_TIME)));
        foodItem.setQuantity(c.getString(c.getColumnIndex(Util.FOOD_QUANTITY)));

        return foodItem;
    }

    //Auxiliary Functions
    //Checks username and password
    public boolean login(String username, String password){

        SQLiteDatabase db = getReadableDatabase();
        User user = getUser(username);

        System.out.println(user.getName());
        System.out.println(user.getPassword());
        if (user.getPassword().equals(password)){
            return true;
        }

        else{
            return false;
        }
    }
}
