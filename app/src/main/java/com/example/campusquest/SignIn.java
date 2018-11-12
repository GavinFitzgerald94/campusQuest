package com.example.campusquest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


public class SignIn extends AppCompatActivity {

    private Button loginButton, signupButton;
    private SQLiteDatabase db;
    private static final String ACTIVITY_TAG="TAG";
    private static final int  toastDuration = Toast.LENGTH_SHORT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        loginButton = findViewById(R.id.signin);
        signupButton = findViewById(R.id.signup);

        // bind login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check username and password
                TextView username = findViewById(R.id.username);
                TextView password = findViewById(R.id.password);
                String user = username.getText().toString();
                String pwd = password.getText().toString();
                // Check if it is blank
                if ( user.trim().length() > 0 &&  pwd.trim().length() > 0) {
                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    // Check the username and password
                    Log.d(SignIn.ACTIVITY_TAG, "Check Successful!");
                    startActivity(intent);
                } else {
                    Log.d(SignIn.ACTIVITY_TAG, "Check failed!");
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Please enter your username or password!",toastDuration).show();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }


    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
