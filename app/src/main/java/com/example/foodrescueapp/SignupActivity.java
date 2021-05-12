package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.User;

public class SignupActivity extends AppCompatActivity {

    EditText fullNameEditText, phoneEditText, emailEditText, addressEditText, passwordEditText, confirmPasswordEditText;
    Button saveBtn;
    DatabaseHelper db;
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

        db = new DatabaseHelper(this);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Getting all the entries from the EditTexts
                String name = fullNameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String username = emailEditText.getText().toString(); //The email address will be the username

                //Checking if password and confirm password match
                if(password.equals(confirmPassword)){
                    long result  = db.createUser(new User(name, username, phone, address, password));

                    if(result>0){
                        Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}