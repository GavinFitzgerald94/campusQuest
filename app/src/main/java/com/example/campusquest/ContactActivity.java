package com.example.campusquest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ContactAdapter mAdapter;
    private RecyclerView mContactList;
    private List<Contact> dataset = new ArrayList<>();
    StringBuilder sb=null;
    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    private Toast toast;

    String[] permissons = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        mContactList = findViewById(R.id.my_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mContactList.setLayoutManager(layoutManager);
        mContactList.setHasFixedSize(true);

        if (ContextCompat.checkSelfPermission(ContactActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ContactActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        } else {
            // Permission has already been granted
            Log.d("Contact", "onCreate: Get the contacts");
            getContacts();
            Log.d("err", "onCreate: "+ dataset);

            Contact[] contactArray = new Contact[dataset.size()];
            for(int i =0; i< dataset.size(); i++){
                contactArray[i] = dataset.get(i);
            }
            mAdapter = new ContactAdapter(this, contactArray);
            mContactList.setAdapter(mAdapter);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sb =new StringBuilder();
                    int i=0;
                    String[] recipients = new String[mAdapter.checkedContact.size()];
                    do {
                        Contact contact= mAdapter.checkedContact.get(i);
                        recipients[i] = contact.getEmail();
                        Log.d("recipients", "onClick: email" + recipients[i]);
                        sb.append(contact.getName());
                        if(i != mAdapter.checkedContact.size()-1){
                            sb.append("\n");
                        }
                        i++;
                    }while (i < mAdapter.checkedContact.size());

                    if(mAdapter.checkedContact.size()>0)
                    {

                        Toast.makeText(ContactActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
                        sendMail(recipients);
                    }else
                    {
                        Toast.makeText(ContactActivity.this,"Please Check An Item First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void getContacts() {
        Contact contact;
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d("contact", "getContacts: "+ name);
                    contact = new Contact();
                    contact.setName(name);

                    Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact.setNumber(phoneNumber);
                        Log.d("contact", "getContacts: "+ phoneNumber);
                    }
                    phoneCursor.close();

                    Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        contact.setEmail(email);
                        Log.d("contact", "getContacts: "+ email);
                    }
                    emailCursor.close();
                    dataset.add(contact);
                }
            }
        } else {
            String err = "No contacts found.";
            Toast.makeText(ContactActivity.this,err,Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMail(String[] recipients){
        String message = "Hi! Come play CampusQuest with me!";
        String subject = "Invitation";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose a Email"));

    }
}