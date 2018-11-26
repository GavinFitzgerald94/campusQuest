package com.example.campusquest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Main activity and home page of the application.
 */

//NavigationView.OnNavigationItemSelectedListener,
public class MainActivity extends AppCompatActivity {
    private CampusQuestOpenHelper mDbOpenHelper;
    private Drawer drawer;
    private Spinner mSpinnerQuests;
    private QuestInfo mSelectedQuest;
    private List<QuestInfo> mQuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer =  DrawerUtil.getDrawer(this,toolbar);

        DataManager.loadQuests(mDbOpenHelper);
        mQuests = DataManager.getInstance().getQuests();



        initialiseDisplayContent();


        /** API taken from "cardslib" library by Gabriele Mariotti can be found at - https://github.com/gabrielemariotti/cardslib */
        //Initialize card object
        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        .useDrawableId(R.drawable.treasure_map)
                        .setTitle("Treasure Hunt")
                        .setSubTitle("Put your problem solving skills to the test as you try and decipher our cryptic messages, each clue represents a certain location on UCD campus follow all the clues to find the treasure! Good Luck...")
//                        .setupSupplementalActions(R.layout.native_material_icon,actions )
                        .build();

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                String cardId = "QU01";
                QuestInfo quest = null;
                for (QuestInfo i : mQuests){
                    if (i.getQuestId().equals(cardId)){
                        quest = i;
                    }
                }
                navigateTreasureHuntHome(quest);
            }
        });

        //Initialize card object
        MaterialLargeImageCard card2 =
                MaterialLargeImageCard.with(this)
                        .useDrawableId(R.drawable.spy_chase)
                        .setTitle("Spy Chase")
                        .setSubTitle("Your are deep undercover working as a covert operative in UCD the intel you hold could help stop a major attack. But the enemy is on to you now RUN!!")
                        .build();

        card2.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card2, View view) {
                Toast.makeText(getApplicationContext()," Game not implemented yet ",Toast.LENGTH_SHORT).show();
            }
        });

        //Bind cards to xml
        CardViewNative cardViewNative = findViewById(R.id.carddemo_largeimage);
        cardViewNative.setCard(card);

        CardViewNative secondCardViewNative = findViewById(R.id.other_game);
        secondCardViewNative.setCard(card2);
    }

    //@Override
    /**
     * Check which side bar navigation item is selected and initiate action.

    @Override
    /**
     * Override back button pressed to close side navigation drawer first before exiting activity.
     */
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void initialiseDisplayContent() {
        // TODO load values for character sheet here.

    }

    @Override
    protected void onDestroy() {
        //mQuests.clear();
        mDbOpenHelper.close();
        super.onDestroy();
    }

//    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedQuest = (QuestInfo) parent.getItemAtPosition(position);

    }

//    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing - auto-generated stub.
    }

    /**
     * Navigates to the treasure hunt home page.
     *
     * @param quest a QuestInfo object containing all quest details.
     **/
    private void navigateTreasureHuntHome(QuestInfo quest) {
        Intent intent = new Intent(this, TreasureHuntHome.class);
        //intent.putExtra("questInfo", quest);
        startActivity(intent);
    }

}