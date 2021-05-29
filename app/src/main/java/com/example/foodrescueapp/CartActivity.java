package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodrescueapp.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    List<Integer> foodIDList;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Get Food ID List from HomeActivity
        Intent intent = getIntent();
        foodIDList = intent.getIntegerArrayListExtra("foodIDList");
    }
}