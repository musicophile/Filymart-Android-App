package com.example.filymart.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


import com.bumptech.glide.Glide;
import com.example.filymart.BeveragesDrinksActivity;
import com.example.filymart.CheckAddressActivity;
import com.example.filymart.EggsMeatFishActivity;
import com.example.filymart.FoodGrainOilActivity;
import com.example.filymart.FruitsVergetablesActivity;
import com.example.filymart.LoginActivity;
import com.example.filymart.OtherProductsActivity;
import com.example.filymart.R;
import com.example.filymart.RegisterActivity;
import com.example.filymart.fragment.HomeFragment;
import com.example.filymart.fragment.BusketsFragment;
import com.example.filymart.fragment.ContactUsFragment;
import com.example.filymart.fragment.ShoppingFragment;
import com.example.filymart.fragment.SettingsFragment;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final int urlNavHeaderBg = R.drawable.f;
    // "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private SQLiteHandler db;
    private SessionManager session;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        //imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PackageManager pm=getPackageManager();
                try {

                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "YOUR TEXT HERE";

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(HomeActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                            .show();
                }


            }
        });
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if(getIntent().getIntExtra("fragment",0)== 4){
            //set the desired fragment as current fragment to fragment pager
            navItemIndex = 0;
            loadHomeFragment();
        }else if(getIntent().getIntExtra("fragmentNumber",0)==1){
            //set the desired fragment as current fragment to fragment pager
            navItemIndex = 1;
            loadHomeFragment();
        }else if (getIntent().getIntExtra("value",0)==3){
            navItemIndex = 1;
            loadHomeFragment();
        }else if (getIntent().getIntExtra("fragment",0)== 2){
            navItemIndex = 2;
            loadHomeFragment();
        }else{

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }}
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {

        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        String email = users.get("email");
        String name = users.get("name");


        if (user_id == null){
            // loading header background image
            Glide.with(this).load(urlNavHeaderBg)
                    .transition(withCrossFade())
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgNavHeaderBg);
        }else{
            // loading header background image
            Glide.with(this).load(urlNavHeaderBg)
                    .transition(withCrossFade())
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgNavHeaderBg);
            // name, website
            txtName.setText(name);
            txtWebsite.setText(email);
        }






        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {


        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                ShoppingFragment shoppingFragment = new ShoppingFragment();
                return shoppingFragment;
            case 2:
                // movies fragment
                BusketsFragment moviesFragment = new BusketsFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                ContactUsFragment notificationsFragment = new ContactUsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            if (user_id == null){
                getMenuInflater().inflate(R.menu.main, menu);
            }else{
                getMenuInflater().inflate(R.menu.main_user, menu);
            }

        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xmljnhukhbnuy hn.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logoutUser();
        }
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        if (id == R.id.action_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    public void proceed(View view) {
        Intent intent = new Intent(this, CheckAddressActivity.class);
        startActivity(intent);
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void fruitsVergetable(View view) {

        Intent intent = new Intent(HomeActivity.this, FruitsVergetablesActivity.class);
        startActivity(intent);
        finish();
    }

    public void foodGrainOil(View view) {
        Intent intent = new Intent(HomeActivity.this, FoodGrainOilActivity.class);
        startActivity(intent);
        finish();
    }

    public void eggsMeatFish(View view) {

        Intent intent = new Intent(HomeActivity.this, EggsMeatFishActivity.class);
        startActivity(intent);
        finish();
    }

    public void beveragesDrinks(View view) {

        Intent intent = new Intent(HomeActivity.this, BeveragesDrinksActivity.class);
        startActivity(intent);
        finish();
    }

    public void otherProducts(View view) {

        Intent intent = new Intent(HomeActivity.this, OtherProductsActivity.class);
        startActivity(intent);
        finish();
    }
}
