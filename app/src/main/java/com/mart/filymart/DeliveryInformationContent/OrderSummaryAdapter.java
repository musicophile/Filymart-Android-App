package com.mart.filymart.DeliveryInformationContent;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mart.filymart.DeliveryInformationContent.model.OrderSummaryModel;
import com.mart.filymart.R;

import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.MyViewHolder> {

    private DeliveryInformationActivity mContext;
    private List<OrderSummaryModel> orderSummaryModelList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView productPrice , productName;



        public MyViewHolder(View view) {
            super(view);
            productName =  view.findViewById(R.id.tvProductName);
            productPrice =  view.findViewById(R.id.tvPriceProduct);
        }
    }


    public OrderSummaryAdapter(DeliveryInformationActivity mContext, List<OrderSummaryModel> orderSummaryModelList) {
        this.mContext = mContext;
        this.orderSummaryModelList = orderSummaryModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_summary, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderSummaryModel orderSummary = orderSummaryModelList.get(position);
        holder.productName.setText(orderSummary.getProduct_name());
        holder.productPrice.setText(orderSummary.getPrice());


    }

    @Override
    public int getItemCount() {
        return orderSummaryModelList.size();
    }



}