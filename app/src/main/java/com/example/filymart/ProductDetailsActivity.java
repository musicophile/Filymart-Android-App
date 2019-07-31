package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class ProductDetailsActivity extends AppCompatActivity {
   private int dd;

    //make sure you are using the correct ip else it will not work


    private TextView product_name, price, about, benefits;
    private ImageView image;
    private ImageButton backPd;
    private Button addToCart;
    private EditText amount;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    String Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


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
        backPd = findViewById(R.id.backPdetails);

        backPd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
                intent1.putExtra("fragment",1);
                startActivity(intent1);
            }
        });

        // Session manager
        session = new SessionManager(getApplicationContext());
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostOrder().execute();
            }
        });
       loadProducts();

    }


    class PostOrder extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductDetailsActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {

            HashMap<String, String> users = db.getUserDetails();


            //String email = user.get("email");

            // Displaying the user details on the screen
            // txtName.setText(name);
            // txtEmail.setText(email);

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


                    // Now store the user in SQLite
                   /* JSONObject user = json.getJSONObject("user");
                    String uid = user.getString("id");
                    String name = user.getString("name");
                    String emaill = user.getString("email");
                    String created_at = user
                            .getString("created_at");

                    // Inserting row in users table
                    db.addUser(name, emaill, uid, created_at);

/*/
                    final String errorMsg = json.getString("msg");
                    // successfully created product
                    // successfully created product
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG)
                                    .show();
                        }

                    });
                    // closing this screen
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


    private void loadProducts() {

        pDialog = new ProgressDialog(ProductDetailsActivity.this);
        pDialog.setMessage("Creating Product..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        
        Intent intent = getIntent();
        dd = intent.getIntExtra("id", dd);
        final String URL_PRODUCTS = "http://www.filymart.com/get_product_details.php?id=" + dd;

        //Toast.makeText(this, "This is the requiured value ;"+dd, Toast.LENGTH_LONG).show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);


                                product_name.setText(product.getString("product_name"));
                                price.setText("Tsh." + product.getString("price"));
                                about.setText(product.getString("about") );
                                benefits.setText(product.getString("benefits"));
                                Glide.with(ProductDetailsActivity.this)
                                        .load(product.getString("image"))
                                        .into(image);
                                 Id = product.getString("id");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("id", id);
                return params;
            }


        }

        ;

        //adding our stringrequest to queue
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        pDialog.dismiss();
    }

}
