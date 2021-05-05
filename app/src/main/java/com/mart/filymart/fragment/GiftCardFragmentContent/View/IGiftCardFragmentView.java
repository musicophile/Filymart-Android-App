package com.mart.filymart.fragment.GiftCardFragmentContent.View;

import java.util.ArrayList;
import com.mart.filymart.fragment.ShoppingFragmentContent.model.ProductModel;


public interface IGiftCardFragmentView {
    void loadDatas(ArrayList<ProductModel> info, int a);
    public void onSetProgressBarVisibility(int visibility);
    public boolean setClickable(boolean visibility);
}
