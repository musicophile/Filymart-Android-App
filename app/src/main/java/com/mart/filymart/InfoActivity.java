package com.mart.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.filymart.activity.HomeActivityContent.HomeActivity;
import com.mart.filymart.app.AppConfig;
import com.mart.filymart.helper.SQLiteHandler;
import com.mart.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private EditText  phone, name;
    private TextView email;
    String name_, email_, phone_;
    private Button submit;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();
    private InfoProgressDialog pd;
    private Handler h;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        email = findViewById(R.id.tvemail);
        phone = findViewById(R.id.tvphone);
        name = findViewById(R.id.tvfullname);
        submit = findViewById(R.id.btnEditData);
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setCancelable(false);
        pDialog = new ProgressDialog(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        h = new Handler();
        pd = new InfoProgressDialog(this, R.mipmap.ic_launcher);
        r =new Runnable() {
            @Override
            public void run() {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };

        if (isNetworkAvailable()){
            pd.show();
            h.postDelayed(r,5000);
            new SettingsDataToEdit().execute();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }

               HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString().trim();
                String Phone = phone.getText().toString().trim();

                if(!Name.isEmpty() & !Phone.isEmpty()){
                    pd.show();
                    h.postDelayed(r,5000);
                    new ChangeUserInfo().execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Fill all field's required", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        h.removeCallbacks(r);
        if (pd.isShowing() ) {
            pd.dismiss();
        }
        super.onDestroy();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class SettingsDataToEdit extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            // getting JSON Object
            // Note that create product url accepts POST method
            final String URL_PRDUCTS = "http://www.filymart.com/mobileUserInformation";
            JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                    "GET", params);

            // check log cat fro response


            Log.d("Create Response", json.toString());

            try {

                JSONObject oo = json.getJSONObject("users");
                JSONArray Array = oo.getJSONArray("users");

                for (int i = 0; i < Array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = Array.getJSONObject(i);

                    name_ = product.getString("name");
                    email_ = product.getString("email");
                    phone_= product.getString("phone");
                    String uid = product.getString("id");
                    String created_at = product
                            .getString("created_at");

                    // Inserting row in users table
                    db.deleteUsers();
                    db.addUser(name_, email_, uid, created_at);
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
            h.removeCallbacks(r);
            if (pd.isShowing() ) {
                pd.dismiss();
            }
            email.setText(email_);
            phone.setText(phone_);
            name.setText(name_);
        }

    }

    class ChangeUserInfo extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {


            String Phone = phone.getText().toString().trim();
            String Name = name.getText().toString().trim();
            String Email = email.getText().toString().trim();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phone", Phone));
            params.add(new BasicNameValuePair("name", Name));
            params.add(new BasicNameValuePair("email", Email));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.URL_CHANGEUSERINFO,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            try {
                // check for success tag
                int success = json.getInt("success");
                //  int success = 1;

                if (success == 1) {



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
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            h.removeCallbacks(r);
            if (pd.isShowing() ) {
                pd.dismiss();
            }
            Intent intent1 = new Intent(getApplicationContext(), HomeActivity.class);
            intent1.putExtra("fragment",6);
            startActivity(intent1);
            finish();
        }

    }

}
