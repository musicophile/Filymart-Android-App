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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filymart.JSONParser;
import com.example.filymart.LoginActivity;
import com.example.filymart.NewAddressActivity;
import com.example.filymart.Orders;
import com.example.filymart.OrdersAdapter;
import com.example.filymart.R;
import com.example.filymart.RegisterActivity;
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
import java.util.Map;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;


public class BusketsFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private ConstraintLayout MConstraint, Guest;
    private List<Orders> albumList;
    private Button pProceed, btnLogin, btnRegister;
    private TextView PriceSum;
    private SessionManager session;
    private SQLiteHandler db;
    String uid;
    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;


    public BusketsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_busket, container, false);

        MConstraint = view.findViewById(R.id.ConstraintLayoutForOrder);
        Guest = view.findViewById(R.id.ConstraintLayoutForGuest);
        btnLogin = view.findViewById(R.id.btnLoginGuest);
        btnRegister = view.findViewById(R.id.btnRegisterGuest);
        PriceSum = view.findViewById(R.id.tvPriceSum);
        recyclerView = view.findViewById(R.id.recycler_view);


        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        albumList = new ArrayList<>();

        //intialize linear layout manager vertically
        LinearLayoutManager linearVertical = new LinearLayoutManager(getContext());

        //LinearLayoutManager
        recyclerView.setLayoutManager(linearVertical);

        pProceed = view.findViewById(R.id.proceed);
        db = new SQLiteHandler(getContext());
        session = new SessionManager(getContext());

        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        if (user_id == null){
            MConstraint.setVisibility(View.INVISIBLE);
            Guest.setVisibility(View.VISIBLE);
        }else{
            MConstraint.setVisibility(View.VISIBLE);
            Guest.setVisibility(View.INVISIBLE);
            if (isNetworkAvailable()){
                new Ordersum().execute();
            }else{
                Toast.makeText(getContext(),
                        "Check Your Network Connection", Toast.LENGTH_LONG)
                        .show();
            }



        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Adding few albums for testing
     */



        class Ordersum extends AsyncTask<String, String, String> {

            /**
             * Before starting background thread Show Progress Dialog
             * */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Orders Loading..");
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
                final String URL_PRDUCTS = "http://www.filymart.com/showMobileorder";
                JSONObject json = jsonParser.makeHttpRequest(URL_PRDUCTS,
                        "GET", params);

                // check log cat fro response


                Log.d("Create Response", json.toString());

                try {
                    uid = json.getString("ordersum");

                    JSONObject o = json.getJSONObject("order");
                    JSONArray array = o.getJSONArray("order");

                    for (int i = 0; i < array.length(); i++) {

                        //getting product object from json array
                        JSONObject product = array.getJSONObject(i);

                        albumList.add(new Orders(
                                product.getString("id"),
                                product.getString("product_name"),
                                product.getInt("total"),
                                product.getString("quantity"),
                                product.getString("image")
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
                adapter = new OrdersAdapter(BusketsFragment.this, albumList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                PriceSum.setText(uid);
            }

        }

}