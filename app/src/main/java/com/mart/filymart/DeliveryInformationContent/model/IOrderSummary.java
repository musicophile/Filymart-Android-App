package com.mart.filymart.DeliveryInformationContent.model;

public interface IOrderSummary {

    String getId();

    String getProduct_name();

    String getPrice();

    public void setId(String id);

    public void setProduct_name(String product_name);

    public void setPrice(String price);
}
