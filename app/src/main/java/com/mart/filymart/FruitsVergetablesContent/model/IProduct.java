package com.mart.filymart.FruitsVergetablesContent.model;

public interface IProduct {

    String getId();

    String getProduct_name();

    String getCategory();

    int getPrice();

    String getImage();

    public void setId(String id);

    public void setProduct_name(String product_name);

    public void setCategory(String category);

    public void setPrice(int price);

    public void setImage(String image);


}
