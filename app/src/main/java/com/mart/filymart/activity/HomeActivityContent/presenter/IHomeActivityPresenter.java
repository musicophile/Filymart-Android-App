package com.mart.filymart.activity.HomeActivityContent.presenter;

import android.support.v4.app.Fragment;

public interface IHomeActivityPresenter {

    public void loadNavHeader();

    public void loadHomeFragment();

    public Fragment getHomeFragmentCall();

    public void setToolbarTitle();

    public void selectNavMenu();

    public void setUpNavigationView();

    public void toggleFab();

    public void logoutUser();
}


