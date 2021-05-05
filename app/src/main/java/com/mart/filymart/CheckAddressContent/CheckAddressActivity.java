package com.mart.filymart.CheckAddressContent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.mart.filymart.CheckAddressContent.model.AddressModel;
import com.mart.filymart.CheckAddressContent.presenter.CheckAddressPresenter;
import com.mart.filymart.CheckAddressContent.presenter.ICheckAddressPresenter;
import com.mart.filymart.CheckAddressContent.view.ICheckAddressView;
import com.mart.filymart.NewAddressActivity;
import com.mart.filymart.R;
import com.mart.filymart.TransparentProgressCheckAddress;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckAddressActivity extends AppCompatActivity implements ICheckAddressView {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private Button createAddress;
    private SQLiteHandler db;
    private TransparentProgressCheckAddress pd;
    private Handler h;
    private Runnable r;
    private ICheckAddressPresenter iCheckAddressPresenter;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_address);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        createAddress = findViewById(R.id.createAddress);
        recyclerView = findViewById(R.id.recyclerViewAddress);
        LinearLayoutManager linearVertical = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearVertical);


        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        iCheckAddressPresenter = new CheckAddressPresenter(this);
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        h = new Handler();
        pd = new TransparentProgressCheckAddress(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };



        createAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckAddressActivity.this, NewAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (isNetworkAvailable()){
            pd.show();
            h.postDelayed(r,5000);
            iCheckAddressPresenter.loadData(user_id);
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }

       // new CheckAddress().execute();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void CheckAddressBack(View view) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("fragment",2);
        startActivity(intent);
    }

    @Override
    public void loadDatas(ArrayList<AddressModel> info) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        adapter = new AddressAdapter(CheckAddressActivity.this, info);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }


}
