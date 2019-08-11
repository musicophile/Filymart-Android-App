package com.mart.filymart.Login.presenter;

import android.os.Handler;
import android.os.Looper;

import com.mart.filymart.JSONParser;
import com.mart.filymart.Login.model.IUser;
import com.mart.filymart.Login.model.UserModel;
import com.mart.filymart.Login.view.ILoginView;

public class LoginPresenterCompl implements ILoginPresenter {
    ILoginView iLoginView;
    IUser user;
    Handler handler;
    JSONParser jsonParser = new JSONParser();

    public LoginPresenterCompl(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        initUser();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void clear() {
        String me = user.getPasswd();
        iLoginView.onClearText(me);
    }

    @Override
    public void doLogin(String name, String passwd) {
        Boolean isLoginSuccess = true;
        final int code = user.checkUserValidity(name,passwd);
        if (code!=9) isLoginSuccess = false;
        final Boolean result = isLoginSuccess;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iLoginView.onLoginResult(result, code);
            }
        }, 5000);
    }

    @Override
    public void setProgressBarVisiblity(int visiblity){
        iLoginView.onSetProgressBarVisibility(visiblity);
    }

    private void initUser(){
        user = new UserModel("a","a");
        String test = "Testing";
        user = new UserModel(test);

    }
}