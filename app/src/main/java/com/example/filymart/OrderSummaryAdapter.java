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
import com.example.filymart.helper.SQLiteHandler;
import com.example.filymart.helper.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.MyViewHolder> {

    private MapsActivity mContext;
    private List<OrderSummary> ordersummaryList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView productPrice , productName;



        public MyViewHolder(View view) {
            super(view);
            productName =  view.findViewById(R.id.tvProductName);
            productPrice =  view.findViewById(R.id.tvPriceProduct);
        }
    }


    public OrderSummaryAdapter(MapsActivity mContext, List<OrderSummary> ordersummaryList) {
        this.mContext = mContext;
        this.ordersummaryList = ordersummaryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_summary, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderSummary orderSummary = ordersummaryList.get(position);
        holder.productName.setText(orderSummary.getProduct_name());
        holder.productPrice.setText(orderSummary.getPrice());


    }

    @Override
    public int getItemCount() {
        return ordersummaryList.size();
    }



}