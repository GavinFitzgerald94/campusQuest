package com.example.campusquest;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.util.List;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Main activity and home page of the application.
 */

//NavigationView.OnNavigationItemSelectedListener,
public class MainActivity extends AppCompatActivity implements

        AdapterView.OnItemSelectedListener {
    private CampusQuestOpenHelper mDbOpenHelper;
    private Drawer drawer;
    private Spinner mSpinnerQuests;
    private QuestInfo mSelectedQuest;
   private List<QuestInfo> mQuests;

    //cardVariables
    private RecyclerView recyclerView;
    private QuestCardAdapter adapter;
    private List<QuestCard> questCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbOpenHelper = new CampusQuestOpenHelper(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        mSpinnerQuests = findViewById(R.id.spinner_quest);
       DataManager.loadQuests(mDbOpenHelper);
        mQuests = DataManager.getInstance().getQuests();
//        buildSpinner();
//        mSpinnerQuests.setOnItemSelectedListener(this);

//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
// //       navigationView.setNavigationItemSelectedListener(this);

        drawer =  DrawerUtil.getDrawer(this,toolbar);

        initialiseDisplayContent();



        // Set supplemental actions as text
        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        // Set supplemental actions
        TextSupplementalAction t1 = new TextSupplementalAction(this, R.id.text1);
        t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
            @Override
            public void onClick(Card card, View view) {
                String cardId = "QU01";
                QuestInfo quest = null;
                for (QuestInfo i : mQuests){
                    if (i.getQuestId().equals(cardId)){
                        quest = i;
                    }
                }
                Toast.makeText(getApplicationContext()," Click on Start",Toast.LENGTH_SHORT).show();
                navigateTreasureHuntHome(quest);
            }
        });
        actions.add(t1);

        MaterialLargeImageCard card =
                MaterialLargeImageCard.with(this)
                        .useDrawableId(R.drawable.run_icon)
                        .setTitle("Treasure Hunt")
                        .setSubTitle("Description of game goes here.")
                        .setupSupplementalActions(R.layout.native_material_icon,actions )
                        .build();

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getApplicationContext()," Click on ActionArea ",Toast.LENGTH_SHORT).show();
            }
        });

        MaterialLargeImageCard card2 =
                MaterialLargeImageCard.with(this)
                        .useDrawableId(R.drawable.logo)
                        .setTitle("Spy Chase")
                        .setSubTitle("Description of game goes here.")
                        .setupSupplementalActions(R.layout.native_material_icon,actions )
                        .build();

        card2.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card2, View view) {
                Toast.makeText(getApplicationContext()," Click on ActionArea2 ",Toast.LENGTH_SHORT).show();
            }
        });

        CardViewNative cardViewNative = findViewById(R.id.carddemo_largeimage);
        cardViewNative.setCard(card);

        CardViewNative secondCardViewNative = findViewById(R.id.other_game);
        secondCardViewNative.setCard(card2);
    }

//     /**
//      * Build and populate quest selection spinner.
//      */
//     private void buildSpinner() {
//         mQuests = DataManager.getInstance().getQuests();
//         ArrayAdapter adapterQuests = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mQuests);
//         adapterQuests.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//         mSpinnerQuests.setAdapter(adapterQuests);
//     }

    //@Override
    /**
     * Check which side bar navigation item is selected and initiate action.
     */
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                //code to go to main activity goes here.
//                Toast.makeText(this, "Home page clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_friends:
//                //code to switch to friends page goes here.
//                Toast.makeText(this, "Friends page clicked", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_leader_board:
//                break;
//            case R.id.nav_stats:
//                Intent charIntent = new Intent(this, CharacterSheet.class);
//                startActivity(charIntent);
//                break;
//            case R.id.nav_sign_out:
//                //code to sign out goes here
//                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        drawer.closeDrawer(GravityCompat.START);
//
//        return true;

        // code to change fragments
        // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentName()).commit();
    // / }

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
  
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        mSelectedQuest = (QuestInfo) parent.getItemAtPosition(position);
//
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Do nothing - auto-generated stub.
//    }

    /**
     * Navigates to the treasure hunt home page.
     *
     * @param quest a QuestInfo object containing all quest details.
     **/
    private void navigateTreasureHuntHome(QuestInfo quest) {
        Intent intent = new Intent(this, TreasureHuntHome.class);
        intent.putExtra("questInfo", quest);
        startActivity(intent);
    }

//    public void goToQuest(View view) {
//
//        switch (mSelectedQuest.getQuestId()) {
//            case "QU01":
//                navigateTreasureHuntHome(mSelectedQuest);
//                break;
//            case "QU02":
//                Toast.makeText(this, "This quest has not been implemented yet.", Toast.LENGTH_LONG).show();
//        }
//
//    }
}
