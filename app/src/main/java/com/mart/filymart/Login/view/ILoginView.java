package com.mart.filymart.Login.view;


public interface ILoginView {
    public void onClearText(String me);
    public void onLoginResult(Boolean result, int code);
    public void onSetProgressBarVisibility(int visibility);
}