package com.example.filymart;

import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filymart.activity.HomeActivity;


public class MainActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private ConstraintLayout mDotLayout;

    private TextView[] mDots;
    private Button mNextBtn;
    private Button mBackBtn;
    private Button mFinishBtn;
    private int mCurrentPage;

    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mSlideViewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.linearLayout);
        mNextBtn = findViewById(R.id.nextButton);
        mBackBtn = findViewById(R.id.prevButton);
        mFinishBtn = findViewById(R.id.finishBtn);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        //onClick Listeners

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage -1);
            }
        });

            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSlideViewPager.setCurrentItem(mCurrentPage +1);
                }
            });



    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
    }
        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }

}


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

                addDotsIndicator(i);

                mCurrentPage = i;
                if (i == 0){
                    mNextBtn.setEnabled(true);
                    mBackBtn.setEnabled(false);
                    mFinishBtn.setEnabled(false);
                    mBackBtn.setVisibility(View.INVISIBLE);
                    mNextBtn.setVisibility(View.VISIBLE);
                    mFinishBtn.setVisibility(View.INVISIBLE);


                    mNextBtn.setText("Next");
                    mBackBtn.setText("");
                } else if (i == mDots.length-1 ){
                    mNextBtn.setEnabled(false);
                    mBackBtn.setEnabled(true);
                    mFinishBtn.setEnabled(true);
                    mBackBtn.setVisibility(View.VISIBLE);
                    mFinishBtn.setVisibility(View.VISIBLE);
                    mNextBtn.setVisibility(View.INVISIBLE);

                    mFinishBtn.setText("Finish");
                    mBackBtn.setText("Back");
                    mNextBtn.setText("");
                }else{
                    mNextBtn.setEnabled(true);
                    mBackBtn.setEnabled(true);
                    mFinishBtn.setEnabled(false);
                    mBackBtn.setVisibility(View.VISIBLE);
                    mNextBtn.setVisibility(View.VISIBLE);
                    mFinishBtn.setVisibility(View.INVISIBLE);

                    mNextBtn.setText("Next");
                    mBackBtn.setText("Back");
                }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
