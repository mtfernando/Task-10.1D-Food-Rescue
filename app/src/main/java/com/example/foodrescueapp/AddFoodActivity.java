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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddFoodActivity extends AppCompatActivity {

    private static final String TAG = "image";
    DatabaseHelper db;
    String username,date, locationID, locationAddress;
    double locationLat, locationLon;
    Button saveButton, addDateButton;
    ImageButton addImageButton;
    TextView locationTextView;
    EditText titleEditText, descEditText, timeEditText, quantityEditText;
    Bitmap imageRes;
    Place selectedPlace;
    Boolean isLocationSelected=false; //True if the user has selected a location

    public static final int RESULT_CALENDAR = 1;
    private final static int RESULT_LOAD_IMAGE = 2;
    private final static int PERMISSION_REQUEST = 3;
    
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
        locationTextView = findViewById(R.id.locationTextView);

        //Initialize DB
        db = new DatabaseHelper(this);

        //Get the username of the user that is adding a new FoodItem
        Intent intent = getIntent();
        username = intent.getStringExtra("user");

        //Places API Fields
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = new ArrayList<>();
        placeFields.add(Place.Field.NAME);
        placeFields.add(Place.Field.ADDRESS);
        placeFields.add(Place.Field.ID);
        placeFields.add(Place.Field.LAT_LNG);

        //Get location using Places Autocomplete
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checking permissions before requesting location updates
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddFoodActivity.this, new String[]{ACCESS_FINE_LOCATION}, 1);
                }

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
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
                }

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(AddFoodActivity.this, CalendarActivity.class);
                startActivityForResult(calendarIntent, 1);
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

                long result=-1;
                //Variables relating to the location are set in OnActivityResult using the data given by Places Autocomplete

                //Check if the user has selected a location
                if(isLocationSelected){
                    //Create and insert FoodItem to DB
                    FoodItem foodItem = new FoodItem(title, desc, date, time, locationID, locationAddress, locationLat, locationLon, quantity, imageRes);
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
                setResult(2, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Getting the date result from CalendarActivity
        switch (requestCode) {

            case RESULT_CALENDAR:
                date = data.getStringExtra("date");
                Toast.makeText(AddFoodActivity.this, "Chosen date is " + date, Toast.LENGTH_LONG).show();
                break;

            //Handle the return of the selected food image
            case RESULT_LOAD_IMAGE:
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
            case 100:
                if(resultCode==RESULT_OK){
                    //Place object of the user selected location
                    selectedPlace = Autocomplete.getPlaceFromIntent(data);

                    //Set TextView to display address of the selected location
                    locationTextView.setText(selectedPlace.getAddress());

                    //Set globabl vars
                    locationID = selectedPlace.getId();
                    locationAddress = selectedPlace.getAddress();
                    locationLat = selectedPlace.getLatLng().latitude;
                    locationLon = selectedPlace.getLatLng().longitude;

                    isLocationSelected = true;
                }
        }

    }
}
