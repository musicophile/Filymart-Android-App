package com.example.filymart;



import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.filymart.fragment.ShoppingFragment;

import java.util.List;

public class ProductsAdapterEmf extends RecyclerView.Adapter<ProductsAdapterEmf.MyViewHolder> {

    private EggsMeatFishActivity mContext;
    private List<Product> productList;

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


    public ProductsAdapterEmf(EggsMeatFishActivity mContext, List<Product> productList) {
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
        final Product product = productList.get(position);
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