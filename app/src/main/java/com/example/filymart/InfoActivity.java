package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filymart.fragment.SettingsFragment;
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

public class InfoActivity extends AppCompatActivity {
    private EditText email, phone, name;
    String name_, email_, phone_;
    private Button submit;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    JSONParser jsonParser = new JSONParser();

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

        if (isNetworkAvailable()){
            new SettingsDataToEdit().execute();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Check Your Network Connection", Toast.LENGTH_LONG)
                    .show();
        }

               HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");


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
            pDialog = new ProgressDialog(InfoActivity.this);
            pDialog.setMessage("Info Loading..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
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
            email.setText(email_);
            phone.setText(phone_);
            name.setText(name_);
        }

    }
}
