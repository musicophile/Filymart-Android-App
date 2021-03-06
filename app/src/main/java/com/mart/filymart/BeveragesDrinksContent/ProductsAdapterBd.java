package com.mart.filymart.BeveragesDrinksContent;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mart.filymart.BeveragesDrinksContent.model.ProductModel;
import com.mart.filymart.ProductDetailsContentHome.ProductDetailsHomeActivity;
import com.mart.filymart.R;

import java.util.List;

public class ProductsAdapterBd extends RecyclerView.Adapter<ProductsAdapterBd.MyViewHolder> {

    private BeveragesDrinksActivity mContext;
    private List<ProductModel> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);


        }
    }


    public ProductsAdapterBd(BeveragesDrinksActivity mContext, List<ProductModel> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductModel product = productList.get(position);
        holder.title.setText(product.getProduct_name());
        holder.count.setText(product.getPrice() + " Tsh");

        // loading product cover using Glide library
        Glide.with(mContext).load(product.getImage()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), ProductDetailsHomeActivity.class);
                intent.putExtra("id", productList.get(position).getId());
                // intent.putExtra("id", product.getId());
                mContext. startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}