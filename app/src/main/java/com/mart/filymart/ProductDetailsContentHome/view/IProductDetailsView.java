package com.mart.filymart.ProductDetailsContentHome.view;

public interface IProductDetailsView {

    public void onClearText(String me, String meme, String image, String about, String benefit, String unit, String single);
    public void onLoginResult(Boolean result, int code);
    public void onSetProgressBarVisibility(int visibility);
}
