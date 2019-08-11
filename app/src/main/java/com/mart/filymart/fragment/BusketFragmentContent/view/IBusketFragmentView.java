package com.mart.filymart.fragment.BusketFragmentContent.view;

import com.mart.filymart.fragment.BusketFragmentContent.model.OrderModel;

import java.util.ArrayList;

public interface IBusketFragmentView {

    void loadDatas(ArrayList<OrderModel> info, String uid);
}
