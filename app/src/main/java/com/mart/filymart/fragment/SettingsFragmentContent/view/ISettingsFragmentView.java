package com.mart.filymart.fragment.SettingsFragmentContent.view;

import com.mart.filymart.CheckAddressContent.model.AddressModel;

import java.util.ArrayList;

public interface ISettingsFragmentView {
    void loadDatas(ArrayList<AddressModel> info, String name, String email, String phone);

    void loadData();
}
