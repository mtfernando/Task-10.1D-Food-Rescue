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
    DatabaseHelper db;
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
        db = new DatabaseHelper(this);

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
                PaymentsUtil.requestPayment(getApplicationContext(), foodPrice, v);
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
            case Util.REQUEST_PAYMENT:
                switch(resultCode){
                    case RESULT_OK:
                        Toast.makeText(this, "Payment success!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment successful");
                        break;

                    case RESULT_CANCELED:
                        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Payment cancelled");
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onActivityResult: Payment error!");
                }
        }
    }
}