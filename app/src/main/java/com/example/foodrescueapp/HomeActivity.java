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
    //Home Activity
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    DatabaseHelper db;
    FloatingActionButton addFoodItemButton;
    String username;
    List<Integer> foodIDList = new ArrayList<Integer>();

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

        db = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        setRecyclerView();

        //Onclick for Floating action button
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting AddFoodActivity. Sending Username.
                Intent addFoodInent = new Intent(HomeActivity.this, AddFoodActivity.class);
                addFoodInent.putExtra("user", username);

                startActivityForResult(addFoodInent, 2);
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
        if(resultCode==RESULT_OK){

            switch(requestCode){
                case Util.REQUEST_ADD_FOOD:
                    if(data.getBooleanExtra("INSERT_OK", false)){
                        Toast.makeText(this, "Food Item was successfully added!", Toast.LENGTH_SHORT).show();
                        setRecyclerView();
                    }

                    break;

                case Util.REQUEST_VIEW_FOOD:
                    //TODO: Handle result from viewing food item. Result might include to add to cart.
                    foodIDList.add(data.getIntExtra("foodID", 0));
                    break;
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
                cartIntent.putIntegerArrayListExtra("foodIDList", (ArrayList<Integer>) foodIDList);

                startActivity(cartIntent);
            }
        }
        return true;
    }


}