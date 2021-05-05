package com.example.filymart.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filymart.JSONParser;
import com.example.filymart.NewAddressActivity;
import com.example.filymart.Orders;
import com.example.filymart.OrdersAdapter;
import com.mart.filymart.R;
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


public class BusketsFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<Orders> albumList;
    private Button pProceed;
    private SessionManager session;
    private SQLiteHandler db;
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

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

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



        loadProducts();
        return view;
    }

    /**
     * Adding few albums for testing
     */

    public void loadProducts(){
        HashMap<String, String> users = db.getUserDetails();

        String user_id = users.get("uid");

        final String URL_PRODUCTS = "https://filymart.000webhostapp.com/showMobileorder?user_id=" + user_id;

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

                                albumList.add(new Orders(
                                        product.getInt("id"),
                                        product.getString("product_name"),
                                        product.getInt("price"),
                                        product.getString("image")
                                ));


                            }
                            adapter = new OrdersAdapter(BusketsFragment.this, albumList);
                            recyclerView.setAdapter(adapter);
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
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }







}

