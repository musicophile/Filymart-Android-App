package com.mart.filymart;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class DesignGiftCard extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public String event_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_gift_card);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "test fargment", Toast.LENGTH_SHORT).show();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return new DesignCardFragment();
                case 1:
                    return new ReceiverInfoFragment();
                    case 2:
                    return new PreviewCardFragment();
                default:
                    return FragmentViewPager.newInstance("Payment ", R.drawable.barcode);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}