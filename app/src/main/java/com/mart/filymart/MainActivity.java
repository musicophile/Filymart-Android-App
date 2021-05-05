package com.mart.filymart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.mart.filymart.Database.dbFunctions;
import com.mart.filymart.helper.SQLiteHandler;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private ConstraintLayout mDotLayout;

    private TextView[] mDots;
    private Button mNextBtn;
    private Button mBackBtn;
    private Button mFinishBtn;
    private int mCurrentPage;
    private dbFunctions db;
    private SQLiteHandler db2;


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
//        CheckUPdate();
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
        db2 = new SQLiteHandler(getApplicationContext());

        db = new dbFunctions(getApplicationContext());

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

            checkSession();

    }

    public void checkSession(){
        if (!db.checkSession().isEmpty() || !db.checkSession().equals(""))
        if(Integer.parseInt(db.checkSession()) > 0){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
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

    private void CheckUPdate() {
        VersionChecker versionChecker = new VersionChecker();
        try
        {   String appVersionName = BuildConfig.VERSION_NAME;
            String mLatestVersionName = versionChecker.execute().get();
            if(!appVersionName.equals(mLatestVersionName)){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Please update your app");
                alertDialog.setMessage("This app version is already Updated. Please update your app from the Play Store.");
                alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
                alertDialog.show();
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class VersionChecker extends AsyncTask<String, String, String> {
        private String newVersion;
        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+getPackageName())
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;
        }
    }
}
