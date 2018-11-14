package com.example.campusquest;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Base class for implementing material design drawer library
 * Example Code @: https://github.com/mikepenz/MaterialDrawer
 */

public class DrawerUtil {

    public static Drawer drawer;

    public static final int HOME = 3;
    public static final int LEADERBOAD = 4;
    public static final int FRIENDS = 5;
    public static final int STATS = 6;
    public static final int LOGOUT = 8;
    public static final int ABOUT = 7;

    public static void getAccountHeader(Activity activity) {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.ucd)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(R.drawable.avatar_lara)
                ).build();
//                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
//                        return false;
//                    }
//                }
//                )
//                .build();

    }

    /**
     * Static method to create a navigation drawer item.
     * @param activity
     * @param toolbar
     */

    public static Drawer getDrawer(final Activity activity, Toolbar toolbar) {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.ucd)
                .addProfiles(
                        new ProfileDrawerItem().withName(DataManager.getInstance().getCurrentUserName()).withEmail("useremail@gmail.com").withIcon(R.drawable.avatar_lara)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent intent = new Intent(activity, CharacterSheet.class);
                        view.getContext().startActivity(intent);
                        return false;
                    }
                })
                .build();

//        PrimaryDrawerItem drawerEmptyItem = new PrimaryDrawerItem().withIdentifier(0).withName("");
//        drawerEmptyItem.withEnabled(false);
//        PrimaryDrawerItem drawerItemLogo= new PrimaryDrawerItem().withIdentifier(1)
//                .withName("").withIcon(R.drawable.logo);
//        PrimaryDrawerItem drawerItemHeader = new PrimaryDrawerItem().withIdentifier(2)
//                .withName(R.string.nav_header_title);

        SecondaryDrawerItem drawerItemHome = new SecondaryDrawerItem().withIdentifier(HOME)
                .withName(R.string.nav_home).withIcon(R.drawable.ic_home);
        SecondaryDrawerItem drawerItemLeaderboard= new SecondaryDrawerItem().withIdentifier(LEADERBOAD)
                .withName(R.string.nav_leaderboard).withIcon(R.drawable.ic_leader_board);
        SecondaryDrawerItem drawerItemFriends = new SecondaryDrawerItem().withIdentifier(FRIENDS)
                .withName(R.string.nav_friends).withIcon(R.drawable.ic_friends);
        SecondaryDrawerItem drawerItemStats = new SecondaryDrawerItem().withIdentifier(STATS)
                .withName(R.string.nav_stats).withIcon(R.drawable.ic_stats);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(ABOUT)
                .withName(R.string.nav_about).withIcon(R.drawable.ic_stats);

        PrimaryDrawerItem drawerItemLogout = new PrimaryDrawerItem().withIdentifier(LOGOUT)
                .withName(R.string.nav_sign_out).withIcon(R.drawable.ic_sign_out);

        //create the drawer and remember the `Drawer` result object
        drawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
//                        drawerEmptyItem,drawerEmptyItem,
//                        drawerItemLogo,
//                        drawerItemHeader,
                        new DividerDrawerItem(),
                        drawerItemHome,
                        drawerItemLeaderboard,
                        drawerItemFriends,
                        drawerItemStats,
                        drawerItemAbout,
                        new DividerDrawerItem(),
                        drawerItemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch((int) drawerItem.getIdentifier()) {
                            case HOME:
                                if(!(activity instanceof MainActivity)) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            case LEADERBOAD:
                                //Action on click here
                                break;
                            case FRIENDS:
                                //Action on click here
                                break;
                            case STATS:
                                if(!(activity instanceof CharacterSheet)) {
                                    Intent intent = new Intent(activity, CharacterSheet.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            case ABOUT:
                                //Action on click here
                                break;
                            case LOGOUT:
                                navigateToLogin(activity);
                                break;
                        }

                        drawer.closeDrawer();
                        return true;
                    }
                })
                .build();
        return drawer;

    }

    public static void navigateToLogin(Activity activity) {
        Intent intent = new Intent(activity, SignIn.class);
        startActivity(activity, intent, null);
        //finish()
    }
}

//switch (mSelectedQuest.getQuestId()) {
//        case "QU01":
//        navigateTreasureHuntHome(mSelectedQuest);
//        break;
//        case "QU02":
//        Toast.makeText(this, "This quest has not been implemented yet.", Toast.LENGTH_LONG).show();
//        }