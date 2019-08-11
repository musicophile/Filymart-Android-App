package com.mart.filymart.DeliveryInformationContent.model;

public class OrderSummaryModel implements IOrderSummary {

    private String id;
    private String product_name;
    private String price;

    public OrderSummaryModel() {
    }

    public OrderSummaryModel(String id, String product_name, String price) {
        this.id = id;
        this.product_name = product_name;
        this.price = price;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProduct_name() {
        return product_name;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    @Override
    public void setPrice(String price) {
        this.price = price;
    }
}
