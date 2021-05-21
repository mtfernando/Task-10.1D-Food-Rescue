package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.FoodItem;

public class AddFoodActivity extends AppCompatActivity {

    DatabaseHelper db;
    String username;
    String date;
    Button saveButton, addDateButton;
    ImageButton addImageButton;
    EditText titleEditText, descEditText, timeEditText, quantityEditText, locationEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        saveButton = findViewById(R.id.saveButton);
        addDateButton = findViewById(R.id.addDateButton);
        addImageButton = findViewById(R.id.addImageButton);

        titleEditText = findViewById(R.id.titleEditText);
        descEditText = findViewById(R.id.descEditText);
        timeEditText = findViewById(R.id.timeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        locationEditText = findViewById(R.id.locationEditText);

        db = new DatabaseHelper(this);

        //Get the username of the user that is adding a new FoodItem
        Intent intent = getIntent();
        username = intent.getStringExtra("user");

        //TODO: addImageButton OnClickListener

        addDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(AddFoodActivity.this, CalendarActivity.class);
                startActivityForResult(calendarIntent, 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving EditText values to variables for easier use
                String title = titleEditText.getText().toString();
                String desc = descEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String quantity = quantityEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String imageRes = username+title;

                //Create and insert FoodItem to DB
                FoodItem foodItem = new FoodItem(title, desc, date, time, location, quantity, imageRes);
                long result = db.createFoodItem(db.getUser(username), foodItem);
                db.close();

                //Checking if FoodItem was inserted properly into DB
                Boolean INSERT_OK;

                if(result!=-1){
                    INSERT_OK = true;
                } else INSERT_OK = false;

                Intent intent = new Intent();
                intent.putExtra("INSERT_OK", INSERT_OK);
                setResult(2, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Getting the date result from CalendarActivity
        if(requestCode==1){
            date = data.getStringExtra("date");
            Toast.makeText(AddFoodActivity.this, "Chosen date is " + date, Toast.LENGTH_LONG).show();
        }
    }
}