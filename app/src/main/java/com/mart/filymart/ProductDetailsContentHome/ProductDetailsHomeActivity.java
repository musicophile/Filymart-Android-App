package com.mart.filymart.ProductDetailsContentHome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mart.filymart.JSONParser;
import com.mart.filymart.ProductDetailsContentHome.presenter.IProductDetailsHomePresenter;
import com.mart.filymart.ProductDetailsContentHome.presenter.ProductDetailsHomePresenter;
import com.mart.filymart.ProductDetailsContentHome.view.IProductDetailsView;
import com.mart.filymart.R;
import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ProductDetailsHomeActivity extends AppCompatActivity implements IProductDetailsView {
    private String dd; private TextView product_name, price, aboutt, benefits, Count;
    private ImageView imagee;
    private ImageButton backPd;
    private Button addToCart;
    private EditText amount;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String Id, pname, productPrice, productAbout, productBenefits, productImage;
    private IProductDetailsHomePresenter iProductDetailsHomePresenter;
    private ProgressDialogHome pd;
    private Handler h;
    private Runnable r;
    String id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_home);


        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        product_name = findViewById(R.id.product_name);
        price = findViewById(R.id.price);
        aboutt = findViewById(R.id.about);
        imagee = findViewById(R.id.image);
        benefits = findViewById(R.id.benefits);
        addToCart = findViewById(R.id.addBtn);
        amount = findViewById(R.id.amount);
        backPd = findViewById(R.id.backPdetailsHome);
        Count = findViewById(R.id.count);

        h = new Handler();
        pd = new ProgressDialogHome(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };
        backPd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                intent1.putExtra("fragment",4);
                startActivity(intent1);
                finish();
            }
        });

        // Session manager
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    String qnty = amount.getText().toString().trim();
                    if (!qnty.isEmpty()){
                        if(user_id == null){
                            Toast.makeText(getApplicationContext(), "Please loginFirst!", Toast.LENGTH_LONG).show();
                        }else{
                            new PostOrder().execute();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Enter Amount or Quantity!", Toast.LENGTH_LONG)
                                .show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Check Your Network Connection", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        if (isNetworkAvailable()){
            Intent in = getIntent();
            id = in.getStringExtra("id");
            iProductDetailsHomePresenter = new ProductDetailsHomePresenter(this);
            pd.show();
            h.postDelayed(r,5000);

            iProductDetailsHomePresenter.doLogin(id);
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClearText(String me, String meme, String image, String about, String benefit, String unit, String single) {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        // super.onDestroy();
        product_name.setText(me);
        price.setText(String.format("Tsh"+meme+"/"+single + unit));
        Glide.with(getApplicationContext())
                .load(image)
                .into(imagee);
        aboutt.setText(about);
        benefits.setText(benefit);
    }

    @Override
    public void onLoginResult(Boolean result, int code) {

    }

    @Override
    public void onSetProgressBarVisibility(int visibility) {

    }


    class PostOrder extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductDetailsHomeActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String productName = product_name.getText().toString().trim();
            String priceP = price.getText().toString().trim();
            String idd = Id;
            String Amount = amount.getText().toString().trim();
            String user_id = users.get("uid");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("product_name", productName));
            params.add(new BasicNameValuePair("price", priceP));
            params.add(new BasicNameValuePair("Id", idd));
            params.add(new BasicNameValuePair("Amount", Amount));
            params.add(new BasicNameValuePair("user_id", user_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_ORDER,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {
                    // successfully created product
                    final String errorMsg = json.getString("msg");
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });
                    //   finish();
                }
                if (success == 2) {
                    final String errorMsg = json.getString("error_msg");
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            amount.setText("");
        }

    }





}
