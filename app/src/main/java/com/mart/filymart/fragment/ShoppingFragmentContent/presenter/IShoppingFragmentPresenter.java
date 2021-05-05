package com.mart.filymart.fragment.ShoppingFragmentContent.presenter;

public interface IShoppingFragmentPresenter {

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
