package com.mart.filymart.fragment.GiftCardFragmentContent.presenter;

import android.support.v4.app.Fragment;

public interface IGiftCardFragmentPresenter {

    void clear();
    void loadData();
    void loadAData();
    void loadBData();
    void loadEData();
    void loadBeveData();
    void loadFoodData();

    void loadADataKids();
    void loadBDataWomens();
    void loadEDataMens();
    void loadBeveDataAllBrands();
    void loadFoodDataAccessoriesElectronics();
    void setProgressBarVisiblity(int visiblity);
    boolean setClickable(boolean visiblity);

}
