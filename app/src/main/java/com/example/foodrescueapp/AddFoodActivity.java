package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddFoodActivity extends AppCompatActivity {

    String username;
    String date;
    Button saveButton, addDateButton;
    ImageButton addImageButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        saveButton = findViewById(R.id.saveButton);
        addDateButton = findViewById(R.id.addDateButton);
        addImageButton = findViewById(R.id.addImageButton);

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