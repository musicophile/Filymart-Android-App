package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.filymart.activity.HomeActivity;
import com.example.filymart.app.AppConfig;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<Address> addressList;
    private Button createAddress;

    private SessionManager session;
    private SQLiteHandler db;

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_address);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        createAddress = findViewById(R.id.createAddress);
        recyclerView = findViewById(R.id.recyclerViewAddress);
        LinearLayoutManager linearVertical = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearVertical);


        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        addressList = new ArrayList<>();



        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        createAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckAddressActivity.this, NewAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (isNetworkAvailable()){
            new CheckAddress().execute();
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

    class CheckAddress extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckAddressActivity.this);
            pDialog.setMessage("Address Loading..");
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

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));

            final String CHECKADDRESS = "http://www.filymart.com/mobileCheckAddress";
            JSONObject json = jsonParser.makeHttpRequest(CHECKADDRESS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {
                //uid = json.getString("ordersum");

               JSONObject o = json.getJSONObject("Addresss");
                JSONArray array = o.getJSONArray("Addresss");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    addressList.add(new Address(
                            product.getString("id"),
                            product.getString("address_name"),
                            product.getString("location")
                    ));


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
            adapter = new AddressAdapter(CheckAddressActivity.this, addressList);
            recyclerView.setAdapter(adapter);

        }

    }



}
