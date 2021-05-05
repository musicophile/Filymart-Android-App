package com.mart.filymart.activity.HomeActivityContent.presenter;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.mart.filymart.activity.HomeActivityContent.view.IHomeActivityView;

public class HomeActivityPresenter implements IHomeActivityPresenter {

    IHomeActivityView iHomeActivityView;
    Handler handler;
    public static int navItemIndex = 0;

    public HomeActivityPresenter (IHomeActivityView iHomeActivityView){
        this.iHomeActivityView = iHomeActivityView;
        //    initUser();
        handler = new Handler(Looper.getMainLooper());

    }
    @Override
    public void loadNavHeader() {
        iHomeActivityView.loadNavHeader();
    }

    @Override
    public void loadHomeFragment() {
        iHomeActivityView.loadHomeFragment();
    }

    @Override
    public Fragment getHomeFragmentCall() {
        iHomeActivityView.getHomeFragmentCall();
        return null;
    }

    @Override
    public void setToolbarTitle() {
        iHomeActivityView.setToolbarTitle();
    }

    @Override
    public void selectNavMenu() {
        iHomeActivityView.selectNavMenu();
    }

    @Override
    public void setUpNavigationView() {
        iHomeActivityView.setUpNavigationView();
    }

    @Override
    public void toggleFab() {
        iHomeActivityView.toggleFab();
    }

    @Override
    public void logoutUser() {
        iHomeActivityView.logoutUser();
    }
}
