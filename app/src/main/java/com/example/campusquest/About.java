package com.example.campusquest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mikepenz.materialdrawer.Drawer;

import mehdi.sakout.aboutpage.AboutPage;

public class About extends AppCompatActivity {
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer =  DrawerUtil.getDrawer(this,toolbar);

        //String description = "CHANGE";
        //.setDescription(description)

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.campus_quest_icon)
                .addGroup("Connect with us")
                .addEmail("campus.quest@gmail.com")
                .addWebsite("http://medyo.github.io/")
                .addFacebook("the.medy")
                .addTwitter("medyo80")
                .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("com.ideashower.readitlater.pro")
                .addGitHub("medyo")
                .addInstagram("medyo80")
                .create();

        LinearLayout aboutPageContainer = findViewById(R.id.aboutpageLayout);
        aboutPageContainer.addView(aboutPage);
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
