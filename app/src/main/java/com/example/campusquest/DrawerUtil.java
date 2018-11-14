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

    // Set constants for use in drawer item selection.
    public static final int HOME = 3;
    public static final int LEADERBOARD = 4;
    public static final int FRIENDS = 5;
    public static final int STATS = 6;
    public static final int LOGOUT = 8;
    public static final int ABOUT = 7;


    /**
     * Static method to create a navigation drawer and populate it with drawer items.
     * Implements a listener method which responds to clicks on navigation items in drawer.
     *
     * @param activity
     * @param toolbar
     */

    public static Drawer getDrawer(final Activity activity, Toolbar toolbar) {

        // Create account header - header item of nav drawer.
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

        // Create drawer items
        SecondaryDrawerItem drawerItemHome = new SecondaryDrawerItem().withIdentifier(HOME)
                .withName(R.string.nav_home).withIcon(R.drawable.ic_home)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));
        SecondaryDrawerItem drawerItemLeaderboard = new SecondaryDrawerItem().withIdentifier(LEADERBOARD)
                .withName(R.string.nav_leaderboard).withIcon(R.drawable.ic_leader_board)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));
        SecondaryDrawerItem drawerItemFriends = new SecondaryDrawerItem().withIdentifier(FRIENDS)
                .withName(R.string.nav_friends).withIcon(R.drawable.ic_friends)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));
        SecondaryDrawerItem drawerItemStats = new SecondaryDrawerItem().withIdentifier(STATS)
                .withName(R.string.nav_stats).withIcon(R.drawable.ic_stats)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(ABOUT)
                .withName(R.string.nav_about).withIcon(R.drawable.ic_stats)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));

        PrimaryDrawerItem drawerItemLogout = new PrimaryDrawerItem().withIdentifier(LOGOUT)
                .withName(R.string.nav_sign_out).withIcon(R.drawable.ic_sign_out)
                .withTextColor(activity.getResources().getColor(R.color.drawer_text));

        // Create the drawer object
        drawer = new DrawerBuilder()
                .withSliderBackgroundColorRes(R.color.drawer_background)
                .withAccountHeader(headerResult)
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
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
                        switch ((int) drawerItem.getIdentifier()) {
                            case HOME:
                                if (!(activity instanceof MainActivity)) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;
                            case LEADERBOARD:
                                //Action on click here
                                break;
                            case FRIENDS:
                                //Action on click here
                                break;
                            case STATS:
                                if (!(activity instanceof CharacterSheet)) {
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
        activity.finish();
    }
}