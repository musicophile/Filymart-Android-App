package com.mart.filymart;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

/**
 * Created by Kevin Omyonga on 12/15/2015.
 */
public class PortalActivity extends AppCompatActivity {

    String fname, lname, email, phone, desc, amount, webViewValue;

    TabLayout tabLayout;
    ViewPager viewPager;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesapal);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        webView = findViewById(R.id.webView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            webViewValue = webView.getTextClassifier().toString().trim();
        }


        if (getSupportActionBar() != null) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle args = getIntent().getExtras();
        fname = args.getString("fname");
        lname = args.getString("lname");
        email = args.getString("email");
        phone = args.getString("phone");
        desc = args.getString("desc");
        amount = args.getString("amount");

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Initialize the ViewPager and set an adapter
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final String[] TITLES = { "Payment Portal", "Payment Portal " };

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }

            @Override
            public Fragment getItem(int position) {

                Fragment fragment;

                if (position == 1) {
                    fragment = DemoPortalXmlFragment.newInstance(fname, lname, email, phone, desc, amount);
                } else {
                    fragment = DemoPortalJavaFragment.newInstance(fname, lname, email, phone, desc, amount);
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return TITLES.length;
            }
        };
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        // Bind the tabs to the ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

