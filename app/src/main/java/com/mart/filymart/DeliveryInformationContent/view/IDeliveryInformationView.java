package com.mart.filymart.DeliveryInformationContent.view;

import com.mart.filymart.DeliveryInformationContent.model.OrderSummaryModel;

import java.util.ArrayList;

public interface IDeliveryInformationView {
    void loadDatas(ArrayList<OrderSummaryModel> info, String ordersum, String deliveryC, String Maintotal, String addressInfomation, String distanceInformation,
                   Double tolatitude, Double tolongitude, String city, Double olat, Double olng);
    void StandardDelivery(String deliveryC, String Maintotal, String ordersum);

    void FastDelivery(String deliveryC, String Maintotal, String ordersum);

    void WeekDelivery(String deliveryC, String Maintotal, String ordersum);
}
