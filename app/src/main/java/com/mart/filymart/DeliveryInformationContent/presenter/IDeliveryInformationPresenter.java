package com.mart.filymart.DeliveryInformationContent.presenter;

public interface IDeliveryInformationPresenter {
    void loadData(String user_id, String addressId);

    void loadDataStandard(String user_id, String delivery, String name, String cost, String deliverytime);

    void loadDataFast(String user_id, String delivery, String name, String cost, String deliverytime);

    void loadDataWeek(String user_id, String delivery, String name, String cost, String deliverytime);
}
