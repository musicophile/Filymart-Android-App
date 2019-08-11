package com.mart.filymart;

public class OrderSummary{
    private String id;
    private String product_name;
    private String price;

    public OrderSummary() {
    }

    public OrderSummary(String id, String product_name, String price) {
        this.id = id;
        this.product_name = product_name;
        this.price = price;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}

