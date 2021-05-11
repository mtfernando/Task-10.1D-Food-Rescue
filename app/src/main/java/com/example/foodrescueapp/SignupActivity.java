package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {

    EditText fullNameEditText, phoneEditText, emailEditText, addressEditText, passwordEditText, confirmPasswordEditText;
    Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullNameEditText = findViewById(R.id.sNameEditText);
        phoneEditText = findViewById(R.id.sPhoneEditText);
        addressEditText = findViewById(R.id.sAddressEditText);
        passwordEditText = findViewById(R.id.sPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.sConfirmPasswordEditText);
        emailEditText = findViewById(R.id.sEmailEditText);
        saveBtn = findViewById(R.id.sSaveButton);
    }
}