package com.mart.filymart.fragment.ShoppingFragmentContent.view;

import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;

import java.util.ArrayList;

public interface IShoppingFragmentView {

    void loadDatas(ArrayList<ProductModel> info, int a);
    public void onSetProgressBarVisibility(int visibility);
    public boolean setClickable(boolean visibility);
}