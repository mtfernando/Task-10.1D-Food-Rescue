package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.FoodItem;
import com.example.foodrescueapp.util.PaymentsUtil;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private PaymentsClient paymentsClient;
    ImageButton gPayButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Initialize DB
        db = new DatabaseHelper(this);

        //Get views
        totalPriceTextView = findViewById(R.id.cartTotalPriceTextView);
        gPayButton = findViewById(R.id.gPayButton);

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

        //Setting up the PaymentsClient
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();

        paymentsClient = PaymentsUtil.createPaymentsClient(this);

        //IsReadyToPayRequest API was not added since this is a Test environment

        gPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject paymentRequest;
                try{
                    paymentRequest = PaymentsUtil.getBaseRequest();
                }
                catch (JSONException e){
                    Log.e(TAG, "onClick: JSON Exception");
                }
            }
        });

    }

    public Integer getTotalPrice(List<Integer> foodIDList){
        Integer totalPrice = -1;

        //Get the price of each foodItem using the foodID. Append to Total.
        for(Integer foodID  : foodIDList){
            totalPrice += db.getFoodItem(foodID).getPrice();
        }

        //Error handling to ensure proper total price was returned from the cart.
        if(totalPrice<0) Log.e(TAG, "getTotalPrice: Returned -1. Check function body.");
        else Log.i(TAG, "Total price of cart = " + totalPrice);
        return totalPrice;
    }
}