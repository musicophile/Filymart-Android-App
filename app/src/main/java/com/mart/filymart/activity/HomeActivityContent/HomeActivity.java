package com.mart.filymart.activity.HomeActivityContent;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.mart.filymart.BeveragesDrinksContent.BeveragesDrinksActivity;
import com.mart.filymart.CheckAddressContent.CheckAddressActivity;
import com.mart.filymart.EggsMeatFish.EggsMeatFishActivity;
import com.mart.filymart.FoodGrainOilContent.FoodGrainOilActivity;
import com.mart.filymart.FruitsVergetablesContent.FruitsVergetablesActivity;
import com.mart.filymart.LoginActivity;
import com.mart.filymart.OtherProductContent.OtherProductsActivity;
import com.mart.filymart.R;
import com.mart.filymart.RegisterActivity;
import com.mart.filymart.activity.AboutUsActivity;
import com.mart.filymart.activity.HomeActivityContent.presenter.HomeActivityPresenter;
import com.mart.filymart.activity.HomeActivityContent.presenter.IHomeActivityPresenter;
import com.mart.filymart.activity.HomeActivityContent.view.IHomeActivityView;
import com.mart.filymart.activity.PrivacyPolicyActivity;
import com.mart.filymart.fragment.GiftCardFragmentContent.GiftCardFragment;
import com.mart.filymart.fragment.HomeFragmentContent.HomeFragment;
import com.mart.filymart.fragment.BusketFragmentContent.BusketsFragment;
import com.mart.filymart.fragment.ContactUsFragment;
import com.mart.filymart.fragment.ShoppingFragment;
import com.mart.filymart.fragment.SettingsFragmentContent.SettingsFragment;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements IHomeActivityView {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private static final int urlNavHeaderBg = R.drawable.f;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_GIFTCARD = "giftcard";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private SQLiteHandler db;
    private SessionManager session;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private IHomeActivityPresenter iHomeActivityPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

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
                    String text = "Hi friends i am using. https://play.google.com/store/apps/details?id=com.mart.filymart&hl=en APP. Enjoy it";

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

        iHomeActivityPresenter = new HomeActivityPresenter(this);
        iHomeActivityPresenter.loadNavHeader();
        // load nav menu header data
       // loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if(getIntent().getIntExtra("fragment",0)== 4){
            //set the desired fragment as current fragment to fragment pager
            navItemIndex = 0;
            iHomeActivityPresenter.loadHomeFragment();
        }else if(getIntent().getIntExtra("fragment",0)==6){
            //set the desired fragment as current fragment to fragment pager
            navItemIndex = 5;
            iHomeActivityPresenter.loadHomeFragment();
        }else if(getIntent().getIntExtra("fragmentNumber",0)==1){
            //set the desired fragment as current fragment to fragment pager
            navItemIndex = 1;
            iHomeActivityPresenter.loadHomeFragment();
        }else if (getIntent().getIntExtra("value",0)==3){
            navItemIndex = 1;
            iHomeActivityPresenter.loadHomeFragment();
        }else if (getIntent().getIntExtra("value",0)==4){
            navItemIndex = 2;
            iHomeActivityPresenter.loadHomeFragment();
        }else if (getIntent().getIntExtra("fragment",0)==5){
            navItemIndex = 1;
            iHomeActivityPresenter.loadHomeFragment();
        }else if (getIntent().getIntExtra("fragment",0)== 2){
            navItemIndex = 3;
            iHomeActivityPresenter.loadHomeFragment();
        }else if (getIntent().getIntExtra("fragment",0)== 3){
            navItemIndex = 2;
            iHomeActivityPresenter.loadHomeFragment();
        }else{

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            iHomeActivityPresenter.loadHomeFragment();
        }}
    }



    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    @Override
    public void loadHomeFragment() {
        iHomeActivityPresenter.selectNavMenu();

        // set toolbar title
        iHomeActivityPresenter.setToolbarTitle();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            iHomeActivityPresenter.toggleFab();
            return;
        }

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
        iHomeActivityPresenter.toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    @Override
    public void getHomeFragmentCall(){

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
                // settings fragment
                GiftCardFragment giftCardFragment = new GiftCardFragment();
                return giftCardFragment;
            case 3:
                // movies fragment
                BusketsFragment moviesFragment = new BusketsFragment();
                return moviesFragment;
            case 4:
                // notifications fragment
                ContactUsFragment notificationsFragment = new ContactUsFragment();
                return notificationsFragment;

            case 5:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;


            default:
                return new HomeFragment();
        }
    }

    @Override
    public void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    @Override
    public void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

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
                    case R.id.nav_gift_card:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_GIFTCARD;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 5;
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

                iHomeActivityPresenter.loadHomeFragment();

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
                iHomeActivityPresenter.loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            iHomeActivityPresenter.logoutUser();
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
    @Override
    public void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }


    @Override
    public void logoutUser() {
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

    @Override
    public void loadNavHeader() {
            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            String email = users.get("email");
            String name = users.get("name");


            if (user_id == null){
                // loading header background image
                Glide.with(this).load(urlNavHeaderBg)
                        .transition(withCrossFade())
                        .into(imgNavHeaderBg);
            }else {
                // loading header background image
                Glide.with(this).load(urlNavHeaderBg)
                        .transition(withCrossFade())
                        .into(imgNavHeaderBg);
                // name, website
                txtName.setText(name);
                txtWebsite.setText(email);
            }
            // showing dot next to notifications label
            navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);

    }
}
