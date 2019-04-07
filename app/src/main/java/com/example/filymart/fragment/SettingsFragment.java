package com.example.filymart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filymart.Address;
import com.example.filymart.AddressInfoAdapter;
import com.example.filymart.InfoActivity;
import com.example.filymart.JSONParser;
import com.example.filymart.R;
import com.example.filymart.app.AppConfig;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;
import com.google.android.gms.common.util.Strings;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class SettingsFragment extends Fragment {

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private TextView CustomerName, CustomerEmail, CustomerPhone;
    JSONParser jsonParser = new JSONParser();
    private RecyclerView recyclerView;
    private AddressInfoAdapter adapter;
    private List<Address> albumList;
    String name, email, phone;
    private ImageButton editInfo;


    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        CustomerName = view.findViewById(R.id.tvCustomerName);
        CustomerEmail = view.findViewById(R.id.tvCustomerEmail);
        CustomerPhone = view.findViewById(R.id.tvCustomerPhone);
        editInfo = view.findViewById(R.id.btnEditInfo);
        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InfoActivity.class);

                startActivity(intent);
            }
        });

        recyclerView = view.findViewById(R.id.recyclerAddress);


        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        albumList = new ArrayList<>();

        //intialize linear layout manager vertically
        LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

        //LinearLayoutManager
        recyclerView.setLayoutManager(linearVertical);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(getContext());
if (isNetworkAvailable()){
    new SettingsData().execute();
}else{
    Toast.makeText(getContext(),
            "Check Your Network Connection", Toast.LENGTH_LONG)
            .show();
}

        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    class SettingsData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
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

                    name = product.getString("name");
                      email = product.getString("email");
                      phone = product.getString("phone");
                                   }


                JSONObject o = json.getJSONObject("address");
                JSONArray array = o.getJSONArray("address");

                for (int i = 0; i < array.length(); i++) {

                    //getting product object from json array
                    JSONObject product = array.getJSONObject(i);

                    albumList.add(new Address(
                            product.getString("id"),
                            product.getString("address_name"),
                            product.getString("location")
                    ));


                }
                // int success = json.getInt("success");
                //  JSONObject user = json.getJSONObject("user");



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
            adapter = new AddressInfoAdapter(SettingsFragment.this, albumList);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            CustomerName.setText(name);
            CustomerEmail.setText(email);
            CustomerPhone.setText(phone);
        }

    }

    }

