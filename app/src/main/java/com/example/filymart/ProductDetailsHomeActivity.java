package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filymart.activity.HomeActivity;
import com.example.filymart.app.AppConfig;
import com.example.filymart.fragment.ShoppingFragment;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailsHomeActivity extends AppCompatActivity {
    private String dd; private TextView product_name, price, about, benefits, Count;
    private ImageView image;
    private ImageButton backPd;
    private Button addToCart;
    private EditText amount;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String Id, pname, productPrice, productAbout, productBenefits, productImage;


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
        about = findViewById(R.id.about);
        image = findViewById(R.id.image);
        benefits = findViewById(R.id.benefits);
        addToCart = findViewById(R.id.addBtn);
        amount = findViewById(R.id.amount);
        backPd = findViewById(R.id.backPdetailsHome);
        Count = findViewById(R.id.count);

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
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()){
                    String qnty = amount.getText().toString().trim();
                    if (!qnty.isEmpty()){
                        new PostOrder().execute();
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
            new ProductDetails().execute();
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
        }

    }




    class ProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductDetailsHomeActivity.this);
            pDialog.setMessage("Product Loading..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();

            String user_id = users.get("uid");
            Intent intent = getIntent();
            dd = (String) intent.getStringExtra("id");

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", dd));

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/mobileProductDetails";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {


                JSONObject o = json.getJSONObject("order");
                JSONArray array = o.getJSONArray("order");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    pname = product.getString("product_name");
                    productPrice = String.format("Tsh.%s", product.getString("price"));
                    productAbout = product.getString("about") ;
                    productImage = product.getString("image");
                    Id = product.getString("id");


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
            product_name.setText(pname);
            price.setText(productPrice);
            about.setText(productAbout);
            benefits.setText(productBenefits);
            Glide.with(ProductDetailsHomeActivity.this)
                    .load(productImage)
                    .into(image);
        }

    }

}
