package com.example.campusquest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/*
* This class is the activity of signup page, which is used to handle user
* sinup opertaions.
* */

public class SignUp extends AppCompatActivity {

    private Button signUpButton;
    private CampusQuestOpenHelper mDbOpenHelper;

    private void sweetAlert(String title, String text, int type) {
        /*
        * This is to call sweet alert function to alert some message
        * to user.
        * */
        new SweetAlertDialog(SignUp.this, type)
                .setTitleText(title)
                .setContentText(text)
                .show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Get the object of signup button
        signUpButton  = findViewById(R.id.signup);

        // Bind the button with click event
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get all text component, including username and password
                TextView username = findViewById(R.id.username);
                TextView password = findViewById(R.id.password);
                TextView password_confirm = findViewById(R.id.password_confirm);
                final String user = username.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                String pwd_confirm = password_confirm.getText().toString().trim();

                // Check the user name is blank or not
                if (user.length() > 0 && pwd.length() > 0) {
                    if (pwd.equals(pwd_confirm)) {
                        // If two password are the same
                        // Write the new username and password into database;
                        mDbOpenHelper = new CampusQuestOpenHelper(SignUp.this);
                        // Connect to the database
                        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                        String selection = CampusQuestDatabaseContract.UserInfoEntry.COLUMN_USERNAME + "  = ?";
                        String[] selectionArgs = {user};
                        String[] userInfoColumns = {
                                CampusQuestDatabaseContract.UserInfoEntry.COLUMN_USERNAME,
                        };
                        Cursor result = db.query(CampusQuestDatabaseContract.UserInfoEntry.TABLE_NAME, userInfoColumns,
                                selection, selectionArgs, null, null,  null);

                        if (result.getCount() == 0) {
                            // If the username doesn't exist
                            // Write the username and password into database
                            DatabaseDataWorker worker = new DatabaseDataWorker(db);
                            worker.insertUser(user, pwd, "UCD", 24, 70, null);
                            // Pop a new sweet alert  bllock
                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Perfect")
                                    .setContentText("Sign up successful!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            // Add cilck event listener to the button
                                            Intent intent = new Intent(SignUp.this, SignIn.class);
                                            // Starting SignIn Activity
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        } else
                            // If the username has been registered, pop up a new alert meaage
                            sweetAlert("Oops","Username already exist!", SweetAlertDialog.ERROR_TYPE);
                    } else
                        // Alert the user that the passwords are inconsistent
                        sweetAlert("Oops","Password inconsistent!", SweetAlertDialog.ERROR_TYPE);
                } else
                    // If the user hasn't input the username and password
                    sweetAlert("Oops","Please input username \n or password!", SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

}
