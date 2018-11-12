package com.example.campusquest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

/*
* This java class is to handle sign in activity of the page. Get the
* username and password and check that.
* */

public class SignIn extends AppCompatActivity {

    private Button loginButton, signupButton;
    private static final String ACTIVITY_TAG="TAG";
    private static final int  toastDuration = Toast.LENGTH_SHORT;
    private CampusQuestOpenHelper mDbOpenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
         * This is function is to initialize the login in page
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        // Get the button object
        loginButton = findViewById(R.id.signin);
        signupButton = findViewById(R.id.signup);

        // Binding click function, check username and password
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
                    // Check the username and password
                    if (checkUser(user, pwd)) {
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        Log.d(SignIn.ACTIVITY_TAG, "Check Successful!");
                        intent.putExtra("username", user);
                        // Starting Home Page
                        startActivity(intent);
                    } else {
                        // Alert user message
                        //Toast.makeText(context, "Wrong Username or Password!",toastDuration).show();
                        new SweetAlertDialog(SignIn.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Wrong Username \n or Password!")
                                .show();
                    }
                } else {
                    Log.d(SignIn.ACTIVITY_TAG, "Check failed!");
//                    Toast.makeText(context, "Please enter your username or password!",toastDuration).show();
                    new SweetAlertDialog(SignIn.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please enter your \n username or password!")
                            .show();
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



    private boolean checkUser(String username, String password) {
        /*
        * This function is to check the username and password.
        * Actually, in real development, this should be validated by a back-end server
        * Here we can just use hard code for simulation
        * */
        Log.d(SignIn.ACTIVITY_TAG, username);
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String selection = CampusQuestDatabaseContract.UserInfoEntry.COLUMN_USERNAME + "  = ? "  +
                 "AND " + CampusQuestDatabaseContract.UserInfoEntry.COLUMN_PASSWORD +  "=?"  ;
        String[] selectionArgs = {username, password};
        String[] userInfoColumns = {
                CampusQuestDatabaseContract.UserInfoEntry.COLUMN_USERNAME,
                CampusQuestDatabaseContract.UserInfoEntry.COLUMN_PASSWORD
        };
       Cursor result = db.query(CampusQuestDatabaseContract.UserInfoEntry.TABLE_NAME, userInfoColumns,
                selection, selectionArgs, null, null,  null);

        return result.getCount() > 0;
    }


    public void goToMainActivity(View view) {
        /*
        * This function is to switch to mainAcicity
        * */
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
