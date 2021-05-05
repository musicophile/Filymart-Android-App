package com.mart.filymart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;

    }

    //Arrays
    public int[] slide_images = {
            R.drawable.filymart,
            R.drawable.longpaperbag,
            R.drawable.benefits
    };

    public String[] slide_headings = {
            "FILYMART",
            "PRODUCTS",
            "BENEFITS"
    };

    public String[] slide_descs = {
            "Filymart is the newest online Supermarket for all your daily needs. You can now order different products from markert, Food products and groceries and we will deliver to your doorstep on time.",
            "We have all products that you need from freshest vergetables and fruits. Top quality products and food grains, dairy products drinks beverages. All groceries you can get in filymart. All are Natural",
            "Filymart is very simple to use, It will help to save your time with the chipest price of product. You can place your order from anywhere and we will do home doorstep delivery on time."


    };
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (ConstraintLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
