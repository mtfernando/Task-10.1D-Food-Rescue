package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodrescueapp.data.DatabaseHelper;

public class ListActivity extends AppCompatActivity {
    String username;
    DatabaseHelper db;
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

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.myListRecyclerView);

        setRecyclerView();
    }

    public void setRecyclerView(){
        recyclerViewAdapter = new RecyclerViewAdapter(db.getAllFoodItems(username), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}