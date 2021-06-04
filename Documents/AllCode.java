package com.example.foodrescueapp;

package com.example.foodrescueapp.data;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
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
package com.example.foodrescueapp.util;

        import android.app.Activity;
        import android.content.Context;
        import android.os.Build;
        import android.util.Log;
        import android.view.View;

        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.foodrescueapp.Constants;
        import com.google.android.gms.wallet.AutoResolveHelper;
        import com.google.android.gms.wallet.PaymentDataRequest;
        import com.google.android.gms.wallet.PaymentsClient;
        import com.google.android.gms.wallet.Wallet;
        import com.google.android.gms.wallet.WalletConstants;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.Optional;

public class PaymentsUtil {
    public static final String TAG = "PaymentsUtil";
    //All utilities requires for Google Pay

    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }


    //Creating a new PaymentsClient
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    //All functions relating to creating the Payment Request are below

    //Returns the allowed authentication methods
    private static JSONArray getAllowedCardAuthMethods() {
        JSONObject authMethods = new JSONObject();
        return new JSONArray(Constants.SUPPORTED_METHODS);
    }

    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(Constants.SUPPORTED_NETWORKS);
    }

    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {{
                put("gateway", "example");
                put("gatewayMerchantId", "exampleGatewayMerchantId");
            }});
        }};
    }

    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        cardPaymentMethod.put("parameters", parameters);
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Test Merchant");
    }

    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE);
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    //Creating a enw payments request
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<JSONObject> getPaymentDataRequest(Integer priceInteger) {

        //PaymentDataRequest requires four parameters apiVersion and apiVersionMinor (provided from getBaseRequest),
        //merchantInfo, allowedPaymentMethods
        //See docs: https://developers.google.com/pay/api/web/reference/request-objects
        final String price = priceInteger.toString();

        try {
            JSONObject paymentDataRequest = com.example.foodrescueapp.util.PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(com.example.foodrescueapp.util.PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", com.example.foodrescueapp.util.PaymentsUtil.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", com.example.foodrescueapp.util.PaymentsUtil.getMerchantInfo());

            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            Log.i(TAG, "getPaymentDataRequest: getPaymentDataRequest is returning Optional.empty()");
            return Optional.empty();
        }
    }

    //Requesting a payment from the user displaying overlay with transaction information
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void requestPayment(Activity activity, Integer price, View view) {

        //Get activity from context that is using the payment utility
        PaymentsClient paymentsClient = com.example.foodrescueapp.util.PaymentsUtil.createPaymentsClient(activity);

        //Get PaymentRequest and make sure a JSON object was returned
        Optional<JSONObject> paymentDataRequestJson = com.example.foodrescueapp.util.PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            Log.e(TAG, "requestPayment: getPaymentDataRequest is not present");
            return;
        }

        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    activity, Util.REQUEST_PAYMENT);
        }

    }
}
package com.example.foodrescueapp.util;

        import android.content.Intent;
        import android.graphics.Bitmap;

        import java.io.ByteArrayOutputStream;

public class Util {

    public static final String PLACES_API_KEY = "AIzaSyAuuYM85VQZh7Fs1Yw6mcwd2CmjFH8VVf4";

    //Request codes for Activities
    public static final int REQUEST_CALENDAR = 1;
    public final static int REQUEST_LOAD_IMAGE = 2;
    public final static int REQUEST_PERMISSION = 3;
    public static final int REQUEST_VIEW_FOOD = 4;
    public static final int REQUEST_ADD_FOOD = 5;
    public static final int REQUEST_CART_VIEW = 6;
    public static final int REQUEST_PLACES = 100;
    public static final int REQUEST_PAYMENT = 200;

    //All database constants defined here

    //The email address will be the username for a given user. This username
    // will link a user to food items listed in the food table.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user_db";
    public static final String USER_TABLE_NAME = "users";
    public static final String FOOD_TABLE_NAME = "food";

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
    public static final String FOOD_LOCATION_ID = "food_location_id";
    public static final String FOOD_LOCATION = "food_location_address";
    public static final String FOOD_LOCATION_LAT = "food_location_latitude";
    public static final String FOOD_LOCATION_LON = "food_location_longitude";
    public static final String FOOD_IMAGE_RES = "food_image_resource";
    public static final String FOOD_PRICE = "food_price";

    // All QUERIES HERE

    //Creating the users table
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + "("
            + USERNAME + " TEXT PRIMARY KEY," + NAME + " TEXT," + PASSWORD + " TEXT,"
            + PHONE + " TEXT," + ADDRESS + " TEXT)";

    //Creating the foodItem table
    public static final String CREATE_FOOD_TABLE = "CREATE TABLE " + FOOD_TABLE_NAME + "(" + FOOD_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + FOOD_TITLE + " TEXT," + FOOD_DESCRIPTION
            + " TEXT," + FOOD_IMAGE_RES + " TEXT," + FOOD_DATE + " TEXT," + FOOD_PICKUP_TIME
            + " TEXT," + FOOD_QUANTITY + " TEXT," + FOOD_LOCATION_ID + " TEXT,"
            + FOOD_LOCATION + " TEXT," + FOOD_LOCATION_LAT + " REAL,"+ FOOD_LOCATION_LON
            + " REAL," + FOOD_PRICE + " INTEGER," + USERNAME + " TEXT)";

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
package com.example.foodrescueapp;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CalendarView;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.Util;
        import com.google.android.libraries.places.api.Places;
        import com.google.android.libraries.places.api.model.Place;
        import com.google.android.libraries.places.api.net.PlacesClient;
        import com.google.android.libraries.places.widget.Autocomplete;
        import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

        import java.util.ArrayList;
        import java.util.List;

        import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddFoodActivity extends AppCompatActivity {
    //Activity for a User to add a new food item to the App

    private static final String TAG = "image";
    com.example.foodrescueapp.data.DatabaseHelper db;
    String username,date, locationID, locationAddress;
    double locationLat, locationLon;
    Button saveButton, addDateButton;
    ImageButton addImageButton;
    TextView locationSelectTextView;
    EditText titleEditText, descEditText, timeEditText, quantityEditText, priceEditText;
    Bitmap imageRes;
    Place selectedPlace;
    Boolean isLocationSelected=false; //True if the user has selected a location



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        //Initialize Views
        saveButton = findViewById(R.id.saveButton);
        addDateButton = findViewById(R.id.addDateButton);
        addImageButton = findViewById(R.id.addImageButton);

        titleEditText = findViewById(R.id.titleEditText);
        descEditText = findViewById(R.id.descEditText);
        timeEditText = findViewById(R.id.timeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        locationSelectTextView = findViewById(R.id.locationSelectTextView);
        priceEditText = findViewById(R.id.priceEditText);

        //Initialize DB
        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        //Get the username of the user that is adding a new FoodItem
        Intent intent = getIntent();
        username = intent.getStringExtra("user");

        //Initialize Places API
        Places.initialize(getApplicationContext(), com.example.foodrescueapp.util.Util.PLACES_API_KEY);
        PlacesClient placesClient = Places.createClient(getApplicationContext());

        //Places API Fields
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = new ArrayList<>();
        placeFields.add(Place.Field.NAME);
        placeFields.add(Place.Field.ADDRESS);
        placeFields.add(Place.Field.ID);
        placeFields.add(Place.Field.LAT_LNG);

        //Checking permissions before requesting location updates
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddFoodActivity.this, new String[]{ACCESS_FINE_LOCATION}, 1);
        }

        //Get location using Places Autocomplete
        locationSelectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("TextView onClick working");
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields).build(AddFoodActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get Internal storage permissions from user
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, com.example.foodrescueapp.util.Util.REQUEST_PERMISSION);
                }

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, com.example.foodrescueapp.util.Util.REQUEST_LOAD_IMAGE);
            }
        });

        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(AddFoodActivity.this, CalendarActivity.class);
                startActivityForResult(calendarIntent, com.example.foodrescueapp.util.Util.REQUEST_CALENDAR);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If the user doesn't choose and image, the default image will be added.
                if(imageRes==null){
                    imageRes = BitmapFactory.decodeResource(getResources(),R.drawable.food_sample);
                }
                //Saving EditText values to variables for easier use
                String title = titleEditText.getText().toString();
                String desc = descEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();
                String priceTemp = priceEditText.getText().toString();

                Integer price = 0;
                //Using the price if the user has provided it
                if(!"".equals(priceTemp)){
                    price = Integer.parseInt(priceTemp);
                }

                long result=-1;
                //Variables relating to the location are set in OnActivityResult using the data given by Places Autocomplete

                //Check if the user has selected a location
                if(isLocationSelected){
                    //Create and insert FoodItem to DB
                    FoodItem foodItem = new FoodItem(title, desc, date, time, locationID, locationAddress, locationLat, locationLon, quantity, imageRes, price);
                    result = db.createFoodItem(db.getUser(username), foodItem);
                    db.close();
                } else Toast.makeText(AddFoodActivity.this, "Please select a location", Toast.LENGTH_SHORT).show();


                //Checking if FoodItem was inserted properly into DB
                Boolean INSERT_OK;

                if(result!=-1){
                    INSERT_OK = true;
                } else INSERT_OK = false;

                Intent intent = new Intent();
                intent.putExtra("INSERT_OK", INSERT_OK);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Getting the date result from CalendarActivity
        switch (requestCode) {

            case com.example.foodrescueapp.util.Util.REQUEST_CALENDAR:
                date = data.getStringExtra("date");
                Toast.makeText(AddFoodActivity.this, "Chosen date is " + date, Toast.LENGTH_LONG).show();
                break;

            //Handle the return of the selected food image
            case com.example.foodrescueapp.util.Util.REQUEST_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {

                    try {
                        Log.d(TAG, "onActivityResult: " + data.getData().getPath());
                        imageRes = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        Log.d(TAG, "onActivityResult: " + imageRes);
                        addImageButton.setImageBitmap(imageRes);
                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            //Result from Places Autocomplete
            case com.example.foodrescueapp.util.Util.REQUEST_PLACES:
                if(resultCode==RESULT_OK){
                    //Place object of the user selected location
                    selectedPlace = Autocomplete.getPlaceFromIntent(data);

                    //Set TextView to display address of the selected location
                    locationSelectTextView.setText(selectedPlace.getAddress());

                    //Set global vars
                    locationID = selectedPlace.getId();
                    locationAddress = selectedPlace.getAddress();
                    locationLat = selectedPlace.getLatLng().latitude;
                    locationLon = selectedPlace.getLatLng().longitude;

                    //Identifies that the user has selected a location
                    isLocationSelected = true;
                }
        }

    }
}
package com.example.foodrescueapp;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CalendarView;
        import android.widget.TextView;
        import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity {
    //Calendar Activity for selecting the date when adding a food item
    CalendarView calendar;
    TextView dateTextView;
    Button selectDateButton;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dateTextView = findViewById(R.id.dateDisplayTextView);
        selectDateButton = findViewById(R.id.selectDateButton);

        initializeCalendar();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sending the selected date back to AddFoodActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("date", date);
                setResult(1, resultIntent);
                finish();
            }
        });
    }

    public void initializeCalendar() {
        calendar = findViewById(R.id.calendarView);

        //Set Monday as the first day of the week
        calendar.setFirstDayOfWeek(2);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(CalendarActivity.this, dayOfMonth + "/" + (month+1) + "/" + year, Toast.LENGTH_SHORT).show();
                date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);

                dateTextView.setText(date);
            }
        });
    }
}
package com.example.foodrescueapp;

        import androidx.annotation.Nullable;
        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Build;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.PaymentsUtil;
        import com.example.foodrescueapp.util.Util;
        import com.google.android.gms.wallet.AutoResolveHelper;
        import com.google.android.gms.wallet.PaymentDataRequest;
        import com.google.android.gms.wallet.PaymentsClient;
        import com.google.android.gms.wallet.Wallet;
        import com.google.android.gms.wallet.WalletConstants;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Optional;

public class CartActivity extends AppCompatActivity {
    //Cart Activity
    public static final String TAG = "CartActivity";
    RecyclerView recyclerView;
    CartRecyclerViewAdapter recyclerViewAdapter;
    List<Integer> foodIDList;
    List<FoodItem> foodItemList;
    Integer cartTotalPrice;
    com.example.foodrescueapp.data.DatabaseHelper db;
    TextView totalPriceTextView;
    ImageButton googlePayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Initialize DB
        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        //Get views
        totalPriceTextView = findViewById(R.id.cartTotalPriceTextView);
        googlePayButton = findViewById(R.id.gPayButton);

        //Get Food ID List from HomeActivity
        Intent intent = getIntent();
        foodIDList = intent.getIntegerArrayListExtra("foodIDList");

        //Get FoodItem object list from foodIDList using DBHelper
        foodItemList = db.getFoodItems(foodIDList);

        //Setting up the RecyclerView
        recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerViewAdapter = new CartRecyclerViewAdapter(foodItemList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        //Set the total text view
        cartTotalPrice = getTotalPrice(foodIDList);
        totalPriceTextView.setText("$" + cartTotalPrice.toString());

        //IsReadyToPayRequest API was not added since this is a Test environment

        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //Display GPay overlay with transaction details
                Log.i(TAG, "gPay button pressed");
                com.example.foodrescueapp.util.PaymentsUtil.requestPayment(CartActivity.this, cartTotalPrice, v);
            }
        });

    }

    //Calculate total price of the items added to the cart
    public Integer getTotalPrice(List<Integer> foodIDList){
        Integer totalPrice = 0;

        //Get the price of each foodItem using the foodID. Append to Total.
        for(Integer foodID  : foodIDList){
            totalPrice += db.getFoodItem(foodID).getPrice();
        }

        //Error handling to ensure proper total price was returned from the cart.
        if(totalPrice<1){
            Log.i(TAG, "getTotalPrice: Returned less than 0. Check function body. Cart may be empty.");
            totalPrice = 0;
        }
        else Log.i(TAG, "Total price of cart = " + totalPrice);
        return totalPrice;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case com.example.foodrescueapp.util.Util.REQUEST_PAYMENT:
                switch(resultCode){
                    case RESULT_OK:
                        //Remove items purchased from the DB since they have been purchased
                        for(Integer foodID : foodIDList){
                            //Returns the number of rows deleted
                            int rowDeleteResult = db.deleteFoodItem(foodID);

                            //Log success of deletion
                            if(rowDeleteResult>0) Log.i(TAG, "foodItem deleted. foodID: " + foodID);
                            else Log.i(TAG, "No foodItems deleted. Provided foodID: " + foodID);

                            //Return to HomeActivity whilst indicating the purchase has been completed.
                            Intent intent = new Intent();
                            intent.putExtra("itemPurchased", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        //Toast success to user and log it.
                        Toast.makeText(this, "Payment success!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment successful");

                        //End the cart activity and return to HomeActivity
                        setResult(RESULT_OK);
                        finish();
                        break;

                    case RESULT_CANCELED:
                        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment cancelled");
                        setResult(RESULT_OK);
                        finish();
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onActivityResult: Payment error!");
                }
        }
    }
}
package com.example.foodrescueapp;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.BitmapFactory;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.GridLayout;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.Util;

        import java.util.List;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {
    public static final String TAG = "RecyclerViewAdapter";
    private List<FoodItem> foodItemList;
    private Context context;

    public CartRecyclerViewAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewAdapter.ViewHolder holder, int position) {


        if(this.getItemCount()>0){

            //Setting up each Food Item's viewholder in the cart
            holder.foodHeaderTextView.setText(foodItemList.get(position).getTitle());
            holder.itemNoTextView.setText(String.valueOf(holder.getAdapterPosition()+1));
            holder.priceTextView.setText(String.valueOf(foodItemList.get(holder.getAdapterPosition()).getPrice()));
        }
        else{
            Toast.makeText(context, "No item added to cart", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "No items in cart. getItemCount returns = " + this.getItemCount());
        }
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView foodHeaderTextView, itemNoTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assigning views from itemView(destination_vertical.xml) to local variables.
            foodHeaderTextView = itemView.findViewById(R.id.foodItemTextView);
            itemNoTextView = itemView.findViewById(R.id.cartNumberTextView);
            priceTextView = itemView.findViewById(R.id.cartPriceTextView);

        }
    }
}
/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.foodrescueapp;

        import com.google.android.gms.wallet.WalletConstants;

        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;

public class Constants {


    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;
    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "JCB",
            "MASTERCARD",
            "VISA");


    public static final List<String> SUPPORTED_METHODS = Arrays.asList(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS");

    public static final String COUNTRY_CODE = "US";

    public static final String CURRENCY_CODE = "USD";

    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");

    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "example";

    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                put("gatewayMerchantId", "exampleGatewayMerchantId");
                // Your processor may require additional parameters.
            }};

    public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME";

    public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("protocolVersion", "ECv2");
                put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
            }};
}
package com.example.foodrescueapp;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.provider.ContactsContract;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.Util;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;

        import java.util.ArrayList;
        import java.util.List;


public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    //Home Activity
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    com.example.foodrescueapp.data.DatabaseHelper db;
    FloatingActionButton addFoodItemButton;
    String username;
    List<Integer> cartIDList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addFoodItemButton = findViewById(R.id.fab);

        //Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);

        //Getting intent from MainActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("user");

        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        setRecyclerView();

        //Onclick for Floating action button
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting AddFoodActivity. Sending Username.
                Intent addFoodInent = new Intent(HomeActivity.this, AddFoodActivity.class);
                addFoodInent.putExtra("user", username);

                startActivityForResult(addFoodInent, com.example.foodrescueapp.util.Util.REQUEST_ADD_FOOD);
            }
        });
    }

    public void setRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapter(db.getAllFoodItems(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Proceeding only if result if there is a result
        if(!(resultCode==RESULT_CANCELED)){

            switch(requestCode){
                case com.example.foodrescueapp.util.Util.REQUEST_ADD_FOOD:
                    if(data.getBooleanExtra("INSERT_OK", false)){
                        Toast.makeText(this, "Food Item was successfully added!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Food Item added successfully");

                        setRecyclerView();
                    }

                    break;

                case com.example.foodrescueapp.util.Util.REQUEST_VIEW_FOOD:
                    //Get the foodID
                    Integer foodIDFromResult = data.getIntExtra("foodID", -1);
                    //True if the user used GPay to purchase in the activity without adding to cart
                    Boolean itemPurchased = data.getBooleanExtra("itemPurchased", false);

                    //Remove the foodItem from DB if it has been paid for
                    if(itemPurchased){

                        Log.i(TAG, "GPay Successful. Deleting FoodItem from DB.");
                        //Returns the number of rows deleted
                        int rowDeleteResult = db.deleteFoodItem(foodIDFromResult);

                        //Log success of deletion
                        if(rowDeleteResult>0) Log.i(TAG, "foodItem deleted. foodID: " + foodIDFromResult);
                        else Log.i(TAG, "No foodItems deleted. Provided foodID: " + foodIDFromResult);

                        //Update recycler view
                        setRecyclerView();
                    }

                    else{
                        //If no errors in result, add to cart.
                        if(foodIDFromResult>-1){

                            //Check if FoodID is already in the cart
                            if(cartIDList.contains(foodIDFromResult)){
                                Toast.makeText(this, "Item already in cart!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Item already exists in cart. FoodID = " + foodIDFromResult);
                            }
                            //Add item to cart, if it hasn't already been added
                            else{
                                cartIDList.add(foodIDFromResult);
                                Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Added to cartIDList, foodID = " + foodIDFromResult);
                            }

                        }
                        else{
                            //If foodID is negative, which it cannot be, then Log the error and provide toast to user
                            Toast.makeText(this, "Error! couldn't add item to cart", Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Item was not added to cart. Returned foodID from REQUEST_VIEW_FOOD: " + foodIDFromResult);
                        }
                    }

                    break;

                case com.example.foodrescueapp.util.Util.REQUEST_CART_VIEW:
                    //Handle return from CartActivity. If purhcased update DB.
                    if(data.getBooleanExtra("itemPurchased", false)) setRecyclerView();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    //Managing selection in Action Overflow of the Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_opt1: {
                //TODO WHEN OPTION 1 IS SELECTED
                break;
            }
            case R.id.toolbar_opt2: {
                //TODO WHEN OPTION 2 IS SELECTED
                break;
            }

            case R.id.toolbar_opt3: {
                //My List is selected from action overflow
                Intent listIntent = new Intent(HomeActivity.this, ListActivity.class);
                listIntent.putExtra("username", username);
                startActivity(listIntent);

                break;
            }

            case R.id.toolbar_opt4: {
                //My Cart is selected from action overflow
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                cartIntent.putIntegerArrayListExtra("foodIDList", (ArrayList<Integer>) cartIDList);

                //Start cart activity
                startActivityForResult(cartIntent, com.example.foodrescueapp.util.Util.REQUEST_CART_VIEW);
            }
        }
        return true;
    }


}

package com.example.foodrescueapp;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Intent;
        import android.os.Bundle;

        import com.example.foodrescueapp.data.DatabaseHelper;

public class ListActivity extends AppCompatActivity {
    //My List Activity
    String username;
    com.example.foodrescueapp.data.DatabaseHelper db;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(myToolbar);

        //Get intent data from HomeActivity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        recyclerView = findViewById(R.id.myListRecyclerView);

        setRecyclerView();
    }

    public void setRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapter(db.getAllFoodItems(username), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}

package com.example.foodrescueapp;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.provider.ContactsContract;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    //Login page activity

    EditText usernameEditText, passwordEditText;
    Button loginBtn, signupBtn;
    com.example.foodrescueapp.data.DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);
        signupBtn = findViewById(R.id.signupButton);
        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginResult = db.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());

                if (loginResult){
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent  = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("user", usernameEditText.getText().toString());
                    startActivity(intent);
                }

                else{
                    Toast.makeText(MainActivity.this, "Error! Incorrect Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);

                //Autofill username if the user already tried to login but doesn't have an account.
                if(usernameEditText.getText().toString()!=null){
                    signupIntent.putExtra("username", usernameEditText.getText().toString());
                }

                //Starting SignupActivty for Result
                //The result wiill return and autofill the username
                startActivityForResult(signupIntent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2){
            //Getting the username registered in the signup page and updating the login page
            // for the ease of the user.
            String returnedUsername = data.getStringExtra("username");
            usernameEditText.setText(returnedUsername);
            passwordEditText.setText("");
        }
    }
}
package com.example.foodrescueapp;

        import android.os.Bundle;

        import androidx.fragment.app.Fragment;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = "MapFragment";
    com.example.foodrescueapp.data.DatabaseHelper db;
    GoogleMap mMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Initialize Map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //Async map
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //When map is loaded
        mMap = googleMap;

        if(getArguments()!=null){
            //Latitude and Longitude from Arguments
            double lat = getArguments().getDouble("latitude");
            double lon = getArguments().getDouble("longitude");

            LatLng locationLatLng = new LatLng(lat, lon);
            //Info Log - Longitude and Latitude of the food_location
            Log.i(TAG, "Latitude: " + lat);
            Log.i(TAG, "Longitude: " + lon);

            mMap.addMarker(new MarkerOptions()
                    .position(locationLatLng)
                    .title("Pick up location"));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 12.0f));
        }
    }
}

package com.example.foodrescueapp;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.BitmapFactory;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.GridLayout;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.Util;

        import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    public static final String TAG = "RecyclerViewAdapter";
    private List<FoodItem> foodItemList;
    private Context context;

    public RecyclerViewAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.food_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {


        if(this.getItemCount()>0){

            //Setting up each Food Item's viewholder
            holder.foodHeader.setText(foodItemList.get(position).getTitle());
            holder.foodDesc.setText(foodItemList.get(position).getDetails());
            //holder.foodImage.setImageResource(context.getResources().getIdentifier("drawable/" + foodItemList.get(position).getImageRes(), null, context.getPackageName()));
            byte[] bitmapData = foodItemList.get(position).getImageRes();
            holder.foodImage.setImageBitmap(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));
        }
        else{
            Toast.makeText(context, "No food items to show!", Toast.LENGTH_SHORT).show();
        }

        //When an item in the RecyclerView is clicked it will show a full page activity containing the details of the Food Item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting the FoodID of the selected item
                int foodID = foodItemList.get(holder.getAdapterPosition()).getFoodID();
                Log.i(TAG, "FoodID from selected item = " + String.valueOf(foodID));
                Intent viewFoodIntent  = new Intent(context, ViewFoodActivity.class);
                viewFoodIntent.putExtra("foodID", foodID);

                //Starting activity in parent context and getting result. Result will be handled in the parent context
                ((AppCompatActivity) context).startActivityForResult(viewFoodIntent, com.example.foodrescueapp.util.Util.REQUEST_VIEW_FOOD);
            }
        });


    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView foodImage;
        TextView foodHeader, foodDesc;
        ImageButton shareButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assigning views from itemView(destination_vertical.xml) to local variables.
            foodImage = itemView.findViewById(R.id.cardImageView);
            foodHeader = itemView.findViewById(R.id.HeaderTextView);
            foodDesc = itemView.findViewById(R.id.descTextView);
            shareButton = itemView.findViewById(R.id.shareImgButton);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Share button clicked!");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubject = foodItemList.get(getAdapterPosition()).getTitle();
                    String shareBody = foodItemList.get(getAdapterPosition()).getDetails();
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(shareIntent, "Share using:"));
                }
            });
        }
    }
}
package com.example.foodrescueapp;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.example.foodrescueapp.model.User;

public class SignupActivity extends AppCompatActivity {
    //Sign up page for new Users to be added to the App
    EditText fullNameEditText, phoneEditText, emailEditText, addressEditText, passwordEditText, confirmPasswordEditText;
    Button saveBtn;
    com.example.foodrescueapp.data.DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullNameEditText = findViewById(R.id.sNameEditText);
        phoneEditText = findViewById(R.id.sPhoneEditText);
        addressEditText = findViewById(R.id.sAddressEditText);
        passwordEditText = findViewById(R.id.sPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.sConfirmPasswordEditText);
        emailEditText = findViewById(R.id.sEmailEditText);
        saveBtn = findViewById(R.id.sSaveButton);

        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting all the entries from the EditTexts
                String name = fullNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String username = emailEditText.getText().toString(); //The email address will be the username

                //Checking if password and confirm password match
                if(password.equals(confirmPassword)){
                    long result  = db.createUser(new User(name, username, phone, address, password));

                    //If password match AND entry is successfully entered into the DB
                    if(result>0){
                        Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();


                        //StartActivityForResult from the MainActivity will capture the following information
                        Intent intent = new Intent();
                        intent.putExtra("username", username);
                        setResult(2, intent);
                        finish();
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
package com.example.foodrescueapp;

        import androidx.annotation.Nullable;
        import androidx.annotation.RequiresApi;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;

        import android.content.Intent;
        import android.graphics.BitmapFactory;
        import android.os.Build;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.foodrescueapp.data.DatabaseHelper;
        import com.example.foodrescueapp.model.FoodItem;
        import com.example.foodrescueapp.util.PaymentsUtil;
        import com.example.foodrescueapp.util.Util;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.wallet.AutoResolveHelper;

        import java.sql.RowId;

public class ViewFoodActivity extends AppCompatActivity{
    public static final String TAG = "ViewFoodActivity";
    TextView titleTextView, descTextView, dateTextView, timeTextView, qtyTextView, locationTextView, priceTextView;
    Button cartButton;
    ImageView foodImageView;
    com.example.foodrescueapp.data.DatabaseHelper db;
    Integer foodPrice;
    FoodItem foodItem;
    int foodIDfromIntent;
    private GoogleMap mMap;
    ImageButton googlePayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        //Initialize DB
        db = new com.example.foodrescueapp.data.DatabaseHelper(this);

        //Get data from Intent
        Intent intent = getIntent();
        foodIDfromIntent = intent.getIntExtra("foodID", 0);

        //Getting the FoodItem object
        foodItem = db.getFoodItem(foodIDfromIntent);
        foodPrice = foodItem.getPrice();

        //Setup text views with relevant data
        setPage();

        //When add to cart is clicked
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("foodID", foodIDfromIntent);
                intent.putExtra("itemPurchased", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //Buy with GPay button
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //Display GPay overlay with transaction details
                Log.i(TAG, "gPay button pressed");
                com.example.foodrescueapp.util.PaymentsUtil.requestPayment(ViewFoodActivity.this, foodPrice, v);
            }
        });
    }

    public void setPage(){
        //This method will set the text views and image view using the FoodItem object

        //Assigning all variables to their corresponding views
        titleTextView = findViewById(R.id.VtitleTextView);
        descTextView = findViewById(R.id.VdescTextView);
        dateTextView = findViewById(R.id.VdateTextView);
        timeTextView = findViewById(R.id.VtimeTextView);
        qtyTextView = findViewById(R.id.VquantityTextView);
        foodImageView = findViewById(R.id.VfoodImageView);
        locationTextView = findViewById(R.id.VlocationTextView);
        cartButton = findViewById(R.id.cartButton);
        priceTextView = findViewById(R.id.VpriceTextView);
        googlePayButton = findViewById(R.id.VgPayButton);

        //Setting Image
        byte[] bitmapData = foodItem.getImageRes();
        foodImageView.setImageBitmap(BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length));

        //Setting text views
        titleTextView.setText(foodItem.getTitle());
        descTextView.setText(foodItem.getDescription());
        dateTextView.setText(foodItem.getPickupDate());
        timeTextView.setText(foodItem.getPickupTime());
        qtyTextView.setText(foodItem.getQuantity());
        locationTextView.setText("Location: " + foodItem.getLocationAddress());
        priceTextView.setText("$" + foodPrice);

        //Initialize Map fragment
        Fragment fragment = new MapFragment();

        //Bundle for LatLng
        Bundle mapBundle = new Bundle();
        mapBundle.putDouble("latitude", foodItem.getLocationLatitude());
        mapBundle.putDouble("longitude", foodItem.getLocationLongitude());

        fragment.setArguments(mapBundle);
        //Open fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.empty_frame_layout, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case com.example.foodrescueapp.util.Util.REQUEST_PAYMENT:
                switch(resultCode){
                    case RESULT_OK:
                        //Returns the number of rows deleted
                        int rowDeleteResult = db.deleteFoodItem(foodIDfromIntent);

                        //Log success of deletion
                        if(rowDeleteResult>0) Log.i(TAG, "foodItem deleted. foodID: " + foodIDfromIntent);
                        else Log.i(TAG, "No foodItems deleted. Provided foodID: " + foodIDfromIntent);

                        //Toast success to user and log it.
                        Toast.makeText(this, "Payment success!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment successful");


                        //Return to HomeActivity whilst indicating the purchase has been completed.
                        Intent intent = new Intent();
                        intent.putExtra("foodID", foodIDfromIntent);
                        intent.putExtra("itemPurchased", true);
                        setResult(RESULT_OK, intent);
                        finish();
                        break;

                    case RESULT_CANCELED:
                        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment cancelled");
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onActivityResult: Payment error!");

                        //Log error status code
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.e("loadPaymentData failed", String.format("Error code: %d", status.getStatusCode()));
                        Log.e("loadPaymentData failed", String.format("Error messaage: %d", status.getStatusMessage()));
                }
        }
    }
}