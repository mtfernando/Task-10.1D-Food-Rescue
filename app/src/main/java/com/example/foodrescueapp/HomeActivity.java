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
import android.util.Log;
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
    public static final String TAG = "HomeActivity";
    //Home Activity
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    DatabaseHelper db;
    FloatingActionButton addFoodItemButton;
    String username;
    List<Integer> cartIDList = new ArrayList<Integer>();

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

                startActivityForResult(addFoodInent, Util.REQUEST_ADD_FOOD);
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
        if(!(resultCode==RESULT_CANCELED)){

            switch(requestCode){
                case Util.REQUEST_ADD_FOOD:
                    if(data.getBooleanExtra("INSERT_OK", false)){
                        Toast.makeText(this, "Food Item was successfully added!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onActivityResult: Food Item added successfully");

                        setRecyclerView();
                    }

                    break;

                case Util.REQUEST_VIEW_FOOD:
                    //Get the foodID
                    Integer foodIDFromResult = data.getIntExtra("foodID", -1);
                    //True if the user used GPay to purchase in the activity without adding to cart
                    Boolean itemPurchased = data.getBooleanExtra("itemPurchased", false);

                    //Remove the foodItem from DB if it has been paid for
                    if(itemPurchased){
                        
                        Log.i(TAG, "GPay Successful. Deleting FoodItem from DB.");
                        //Returns the number of rows deleted
                        int rowDeleteResult = db.deleteFoodItem(foodIDFromResult);

                        //Log success of deletion
                        if(rowDeleteResult>0) Log.i(TAG, "foodItem deleted. foodID: " + foodIDFromResult);
                        else Log.i(TAG, "No foodItems deleted. Provided foodID: " + foodIDFromResult);

                        //Update recycler view
                        setRecyclerView();
                    }

                    else{
                        //If no errors in result, add to cart.
                        if(foodIDFromResult>-1){

                            //Check if FoodID is already in the cart
                            if(cartIDList.contains(foodIDFromResult)){
                                Toast.makeText(this, "Item already in cart!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Item already exists in cart. FoodID = " + foodIDFromResult);
                            }
                            //Add item to cart, if it hasn't already been added
                            else{
                                cartIDList.add(foodIDFromResult);
                                Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Added to cartIDList, foodID = " + foodIDFromResult);
                            }

                        }
                        else{
                            //If foodID is negative, which it cannot be, then Log the error and provide toast to user
                            Toast.makeText(this, "Error! couldn't add item to cart", Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Item was not added to cart. Returned foodID from REQUEST_VIEW_FOOD: " + foodIDFromResult);
                        }
                    }

                    break;

                case Util.REQUEST_CART_VIEW:
                    //Handle return from CartActivity. If purhcased update DB.
                    if(data.getBooleanExtra("itemPurchased", false)) setRecyclerView();
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
                cartIntent.putIntegerArrayListExtra("foodIDList", (ArrayList<Integer>) cartIDList);

                //Start cart activity
                startActivityForResult(cartIntent, Util.REQUEST_CART_VIEW);
            }
        }
        return true;
    }


}