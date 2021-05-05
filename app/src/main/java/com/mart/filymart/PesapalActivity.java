package com.mart.filymart;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by Kevin Omyonga on 12/1/2015.
 */
public class PesapalActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Initialize the ViewPager and set an adapter
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final String[] TITLES = { "Sample Form", "About the Project" };

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }

            @Override
            public Fragment getItem(int position) {

                Fragment fragment = null;

                switch (position) {
                    case 0:
                        fragment = DemoFormFragment.newInstance();
                        break;
                    case 1:
                        fragment = DemoInfoFragment.newInstance();
                        break;
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
}
