package com.example.filymart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.filymart.activity.HomeActivity;
import com.example.filymart.app.AppConfig;
import com.example.filymart.fragment.BusketsFragment;
import com.example.filymart.fragment.SettingsFragment;
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressInfoAdapter extends RecyclerView.Adapter<AddressInfoAdapter.MyViewHolder> {

    private SettingsFragment mContext;
    private List<Address> addressList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView addressInfo;
        public ImageButton btnEditA, btnView, btnDelete;



        public MyViewHolder(View view) {
            super(view);
            addressInfo =  view.findViewById(R.id.tvAddress);
            btnEditA =  view.findViewById(R.id.btnEdit);
            btnView =  view.findViewById(R.id.btnView);
            btnDelete =  view.findViewById(R.id.btnDelet);
        }
    }


    public AddressInfoAdapter(SettingsFragment mContext, List<Address> addressList) {
        this.mContext = mContext;
        this.addressList = addressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_information, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Address address = addressList.get(position);
        holder.addressInfo.setText("The name of your address is"+address.getName() +"and it's Location is"+ address.getLocation());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnEditA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }



}