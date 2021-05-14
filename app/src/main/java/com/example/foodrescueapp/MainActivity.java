package com.example.foodrescueapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginBtn, signupBtn;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginButton);
        signupBtn = findViewById(R.id.signupButton);
        db = new DatabaseHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginResult = db.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());

                if (loginResult){
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(MainActivity.this, "Error! Incorrect Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);

                //Autofill username if the user already tried to login but doesn't have an account.
                if(usernameEditText.getText().toString()!=null){
                    signupIntent.putExtra("username", usernameEditText.getText().toString());
                }

                //Starting SignupActivty for Result
                //The result wiill return and autofill the username
                startActivityForResult(signupIntent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2){
            //Getting the username registered in the signup page and updating the login page
            // for the ease of the user.
            String returnedUsername = data.getStringExtra("username");
            usernameEditText.setText(returnedUsername);
            passwordEditText.setText("");
        }
    }
}