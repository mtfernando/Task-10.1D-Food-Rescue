package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    //Cart Activity
    public static final String TAG = "CartActivity";
    RecyclerView recyclerView;
    CartRecyclerViewAdapter recyclerViewAdapter;
    List<Integer> foodIDList;
    List<FoodItem> foodItemList;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Initialize DB
        db = new DatabaseHelper(this);

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
    }
}