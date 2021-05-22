package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendar;
    TextView dateTextView;
    Button selectDateButton;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dateTextView = findViewById(R.id.dateDisplayTextView);
        selectDateButton = findViewById(R.id.selectDateButton);

        initializeCalendar();

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sending the selected date back to AddFoodActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("date", date);
                setResult(1, resultIntent);
                finish();
            }
        });
    }

    public void initializeCalendar() {
        calendar = findViewById(R.id.calendarView);

        //Set Monday as the first day of the week
        calendar.setFirstDayOfWeek(2);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(CalendarActivity.this, dayOfMonth + "/" + month+1 + "/" + year, Toast.LENGTH_SHORT).show();
                date = String.valueOf(dayOfMonth) + "/" + String.valueOf(month) + "/" + String.valueOf(year);

                dateTextView.setText(date);
            }
        });
    }
}