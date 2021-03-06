package com.mart.filymart;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mart.filymart.BrandActivity.BrandsActivity;
import com.mart.filymart.ProductDetailsContent.ProductDetailsContents;
import com.mart.filymart.fragment.BusketFragmentContent.BusketsFragment;
import com.mart.filymart.fragment.GiftCardFragmentContent.GiftCardFragment;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;

import java.util.List;

public class GiftCardAdapter extends RecyclerView.Adapter<GiftCardAdapter.MyViewHolder> {
    private GiftCardFragment mContext;
    private BusketsFragment mcontext;
    private List<ProductModel> productList;
    private int a;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            count =  view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
        }
    }


    public GiftCardAdapter(GiftCardFragment mContext, List<ProductModel> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    public GiftCardAdapter(GiftCardFragment mContext, List<ProductModel> productList, int a) {
        this.mContext = mContext;
        this.productList = productList;
        this.a = a;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ProductModel product = productList.get(position);
        holder.title.setText(product.getProduct_name());
        if(a == 2){
            holder.count.setText("Brand");
        }else{
            holder.count.setText(product.getPrice() + " Tsh");
        }


        // loading product cover using Glide library
        Glide.with(mContext).load(product.getImage()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (a == 2){
                    Intent intent=new Intent(v.getContext(), BrandsActivity.class);
                    intent.putExtra("id", productList.get(position).getId());
                    // intent.putExtra("id", product.getId());
                    mContext. startActivity(intent);
                }else{
                    Intent intent=new Intent(v.getContext(), DesignGiftCard.class);
                    intent.putExtra("id", productList.get(position).getId());
                    // intent.putExtra("id", product.getId());
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();    }
}
