package com.example.foodrescueapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Util;

import java.util.ArrayList;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";
    public DatabaseHelper(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
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
        values.put(Util.FOOD_LOCATION_ID, foodItem.getLocationID());
        values.put(Util.FOOD_LOCATION, foodItem.getLocationAddress());
        values.put(Util.FOOD_LOCATION_LAT, foodItem.getLocationLatitude());
        values.put(Util.FOOD_LOCATION_LON, foodItem.getLocationLongitude());
        values.put(Util.FOOD_QUANTITY, foodItem.getQuantity());
        values.put(Util.FOOD_IMAGE_RES, foodItem.getImageRes());
        values.put(Util.FOOD_PRICE, foodItem.getPrice());
        values.put(Util.USERNAME, user.getUsername());

        //Inserting row into foodItem table
        long result = db.insert(Util.FOOD_TABLE_NAME, null, values);

        //Inserting row into linking table users_food
        //long linkingResult = createUserFoodEntry(user, foodItem);

        return  result;
    }

    //Delete foodItem for a given foodID
    public int deleteFoodItem(Integer foodID){
        SQLiteDatabase db = this.getReadableDatabase();

        //Returns the number of Rows deleted. Should be 1 at max since each foodItem has a unique foodID.
        return db.delete(Util.FOOD_TABLE_NAME, Util.FOOD_ID + "=" + foodID, null);
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
        SQLiteDatabase db = getReadableDatabase();

        String FETCH_FOOD_ITEM = "SELECT * FROM " + Util.FOOD_TABLE_NAME + " WHERE " + Util.FOOD_ID + " = " + foodID;

        Cursor c = db.rawQuery(FETCH_FOOD_ITEM, null);

        if(c!=null)
            if(c.moveToFirst()){

                //Creating FoodItem object from cursor
                FoodItem foodItem = new FoodItem();

                foodItem.setFoodID(c.getInt(c.getColumnIndex(Util.FOOD_ID)));
                foodItem.setTitle(c.getString(c.getColumnIndex(Util.FOOD_TITLE)));
                foodItem.setDescription(c.getString(c.getColumnIndex(Util.FOOD_DESCRIPTION)));

                byte[] bitmapData = c.getBlob(c.getColumnIndex(Util.FOOD_IMAGE_RES));
                foodItem.setImageRes(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));

                foodItem.setLocationID(c.getString(c.getColumnIndex(Util.FOOD_LOCATION_ID)));
                foodItem.setLocationAddress(c.getString(c.getColumnIndex(Util.FOOD_LOCATION)));
                foodItem.setLocationLatitude(c.getDouble(c.getColumnIndex(Util.FOOD_LOCATION_LAT)));
                foodItem.setLocationLongitude(c.getDouble(c.getColumnIndex(Util.FOOD_LOCATION_LON)));
                foodItem.setPickupDate(c.getString(c.getColumnIndex(Util.FOOD_DATE)));
                foodItem.setPickupTime(c.getString(c.getColumnIndex(Util.FOOD_PICKUP_TIME)));
                foodItem.setQuantity(c.getString(c.getColumnIndex(Util.FOOD_QUANTITY)));
                foodItem.setPrice(c.getInt(c.getColumnIndex(Util.FOOD_PRICE)));

                return foodItem;
            } else Log.e(TAG, "Cursor is empty.");

        return null;
    }

    //Returns a list of FoodItem objects for a given Food ID List
    public List<FoodItem> getFoodItems(List<Integer> foodIDList){
        List<FoodItem> foodItemList = new ArrayList<FoodItem>();

        //Loop through the provided foodIDList to create a new list of FoodItems.
        for(Integer foodID : foodIDList){
            foodItemList.add(getFoodItem(foodID));
        }

        return foodItemList;
    }

    //Returns all foodItem objects from the Database
    public List<FoodItem> getAllFoodItems(){
        SQLiteDatabase db = getWritableDatabase();
        String FETCH_ALL_FOOD = "SELECT * FROM " + Util.FOOD_TABLE_NAME;

        Cursor c = db.rawQuery(FETCH_ALL_FOOD, null);

        //Index of each attribute
        final int idIndex = c.getColumnIndex(Util.FOOD_ID);
        final int titleIndex = c.getColumnIndex(Util.FOOD_TITLE);
        final int descIndex = c.getColumnIndex(Util.FOOD_DESCRIPTION);
        final int dateIndex = c.getColumnIndex(Util.FOOD_DATE);
        final int timeIndex = c.getColumnIndex(Util.FOOD_PICKUP_TIME);
        final int quantityIndex = c.getColumnIndex(Util.FOOD_QUANTITY);
        final int locationIDIndex = c.getColumnIndex(Util.FOOD_LOCATION_ID);
        final int locationAddressIndex = c.getColumnIndex(Util.FOOD_LOCATION);
        final int locationLatIndex = c.getColumnIndex(Util.FOOD_LOCATION_LAT);
        final int locationLonIndex = c.getColumnIndex(Util.FOOD_LOCATION_LON);
        final int imageIndex = c.getColumnIndex(Util.FOOD_IMAGE_RES);
        final int priceIndex = c.getColumnIndex(Util.FOOD_PRICE);

        try {

            // Checking if cursor is empty
            if (!c.moveToFirst()) {
                return new ArrayList<>();
            }

            final List<FoodItem> foodItemList = new ArrayList<>();

            do {

                // Read the values of a row in the table using the indexes acquired above
                final int id = c.getInt(idIndex);
                final String title = c.getString(titleIndex);
                final String description = c.getString(descIndex);
                final String date = c.getString(dateIndex);
                final String quantity = c.getString(quantityIndex);
                final String time = c.getString(timeIndex);
                final String locationID = c.getString(locationIDIndex);
                final String locationAddress = c.getString(locationAddressIndex);
                final double locationLat = c.getDouble(locationLatIndex);
                final double locationLon = c.getDouble(locationLonIndex);

                byte[] bitmapData = c.getBlob(imageIndex);
                final Bitmap imageRes = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

                final int price = c.getInt(priceIndex);

                foodItemList.add(new FoodItem(id, title, description, date, time, locationID, locationAddress, locationLat, locationLon, quantity, imageRes, price));

            } while (c.moveToNext());

            return foodItemList;

        } finally {
            c.close();

            // close the database
            db.close();
        }
    }

    //Overloading getAllFoodItems with username
    public List<FoodItem> getAllFoodItems(String username){
        SQLiteDatabase db = getWritableDatabase();
        String FETCH_ALL_FOOD = "SELECT * FROM " + Util.FOOD_TABLE_NAME + " WHERE " + Util.USERNAME + " = \"" + username + "\"";

        Cursor c = db.rawQuery(FETCH_ALL_FOOD, null);

        //Index of each attribute
        final int idIndex = c.getColumnIndex(Util.FOOD_ID);
        final int titleIndex = c.getColumnIndex(Util.FOOD_TITLE);
        final int descIndex = c.getColumnIndex(Util.FOOD_DESCRIPTION);
        final int dateIndex = c.getColumnIndex(Util.FOOD_DATE);
        final int timeIndex = c.getColumnIndex(Util.FOOD_PICKUP_TIME);
        final int quantityIndex = c.getColumnIndex(Util.FOOD_QUANTITY);
        final int locationIDIndex = c.getColumnIndex(Util.FOOD_LOCATION_ID);
        final int locationAddressIndex = c.getColumnIndex(Util.FOOD_LOCATION);
        final int locationLatIndex = c.getColumnIndex(Util.FOOD_LOCATION_LAT);
        final int locationLonIndex = c.getColumnIndex(Util.FOOD_LOCATION_LON);
        final int imageIndex = c.getColumnIndex(Util.FOOD_IMAGE_RES);
        final int priceIndex = c.getColumnIndex(Util.FOOD_PRICE);

        try {

            // Checking if cursor is empty
            if (!c.moveToFirst()) {
                return new ArrayList<>();
            }

            final List<FoodItem> foodItemList = new ArrayList<>();

            do {

                // Read the values of a row in the table using the indexes acquired above
                final String id = c.getString(idIndex);
                final String title = c.getString(titleIndex);
                final String description = c.getString(descIndex);
                final String date = c.getString(dateIndex);
                final String quantity = c.getString(quantityIndex);
                final String time = c.getString(timeIndex);
                final String locationID = c.getString(locationIDIndex);
                final String locationAddress = c.getString(locationAddressIndex);
                final double locationLat = c.getDouble(locationLatIndex);
                final double locationLon = c.getDouble(locationLonIndex);
                final int price = c.getInt(priceIndex);

                byte[] bitmapData = c.getBlob(imageIndex);
                Bitmap imageRes = (BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));

                foodItemList.add(new FoodItem(title, description, date, time, locationID, locationAddress, locationLat, locationLon, quantity, imageRes, price));

            } while (c.moveToNext());

            return foodItemList;

        } finally {
            c.close();

            // close the database
            db.close();
        }
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
