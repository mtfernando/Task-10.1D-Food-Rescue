package com.example.foodrescueapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    DatabaseHelper db;
    TextView totalPriceTextView;
    ImageButton googlePayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Initialize DB
        db = new DatabaseHelper(this);

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
                PaymentsUtil.requestPayment(CartActivity.this, cartTotalPrice, v);
            }
        });

    }

    //Calculate total price of the items added to the cart
    public Integer getTotalPrice(List<Integer> foodIDList){
        Integer totalPrice = -1;

        //Get the price of each foodItem using the foodID. Append to Total.
        for(Integer foodID  : foodIDList){
            totalPrice += db.getFoodItem(foodID).getPrice();
        }

        //Error handling to ensure proper total price was returned from the cart.
        if(totalPrice<0){
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