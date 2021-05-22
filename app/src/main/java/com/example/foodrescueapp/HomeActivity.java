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
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    DatabaseHelper db;
    FloatingActionButton addFoodItemButton;
    String username;

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

        if(requestCode==2){
            if(data.getBooleanExtra("INSERT_OK", false)){
                Toast.makeText(this, "Food Item was successfully added!", Toast.LENGTH_SHORT).show();
                setRecyclerView();
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
                //My List is selected from aciton overflow
                Intent listIntent = new Intent(HomeActivity.this, ListActivity.class);
                listIntent.putExtra("username", username);
                startActivity(listIntent);

                break;
            }
        }
        return true;
    }


}