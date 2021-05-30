package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.FoodItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.RowId;

public class ViewFoodActivity extends AppCompatActivity{
    TextView titleTextView, descTextView, dateTextView, timeTextView, qtyTextView, locationTextView;
    Button cartButton;
    ImageView foodImageView;
    DatabaseHelper db;
    FoodItem foodItem;
    int foodIDfromIntent;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        titleTextView = findViewById(R.id.VtitleTextView);
        descTextView = findViewById(R.id.VdescTextView);
        dateTextView = findViewById(R.id.VdateTextView);
        timeTextView = findViewById(R.id.VtimeTextView);
        qtyTextView = findViewById(R.id.VquantityTextView);
        foodImageView = findViewById(R.id.VfoodImageView);
        locationTextView = findViewById(R.id.VlocationTextView);
        cartButton = findViewById(R.id.cartButton);

        //Initialize DB
        db = new DatabaseHelper(this);

        //Get data from Intent
        Intent intent = getIntent();
        foodIDfromIntent = intent.getIntExtra("foodID", 0);

        //Getting the FoodItem object
        foodItem = db.getFoodItem(foodIDfromIntent);

        //Setup text views with relevant data
        setPage();
    }

    public void setPage(){
        //This method will set the text views and image view using the FoodItem object

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
}