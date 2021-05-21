package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.FoodItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    DatabaseHelper db;
    FloatingActionButton addFoodItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addFoodItemButton = findViewById(R.id.fab);

        //Setting up toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Getting intent from MainActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("user");

        db = new DatabaseHelper(this);

        //Dummy values for testing
        //FoodItems
        db.createFoodItem(db.getUser("admin"), new FoodItem("Fried Rice", "Yummy egg fried rice", "22nd of May", "Evening", "Rowville", "4 pax", "food_sample"));
        db.createFoodItem(db.getUser("admin"), new FoodItem("Sushi", "Yummy Sushi", "25th of May", "Morning", "Cranbourne", "4 pax", "food_sample"));
        db.createFoodItem(db.getUser("admin"), new FoodItem("Sandwiches", "Yummy egg sandwiches", "22nd of May", "Evening", "Rowville", "4 pax", "food_sample"));
        db.createFoodItem(db.getUser("admin"), new FoodItem("Chicken Buriyani", "Yummy chicken buriyani", "22nd of May", "Night", "Narre Warren", "4 pax", "food_sample"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(db.getAllFoodItems(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);

        //Onclick for Floating action button
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting AddFoodActivity. Sending Username.
                Intent addFoodInent = new Intent(HomeActivity.this, AddFoodActivity.class);
                addFoodInent.putExtra("user", username);

                startActivity(addFoodInent);
            }
        });
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
                //TODO WHEN OPTION 3 IS SELECTED
                break;
            }
        }
        return true;
    }


}