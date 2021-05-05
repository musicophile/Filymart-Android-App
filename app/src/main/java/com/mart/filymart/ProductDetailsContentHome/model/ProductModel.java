package com.mart.filymart.ProductDetailsContentHome.model;

public class ProductModel implements IProduct {

    String id;
    String productName;
    String image;
    String about;
    String price;
    String benefit;
    String unit;
    String single;

    public ProductModel(String productName, String price, String about, String benefit, String image, String unit, String single) {
        this.productName = productName;
        this.price = price;
        this.image = image;
        this.about = about;
        this.benefit = benefit;
        this.unit = unit;
        this.single = single;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String getAbout() {
        return about;
    }

    @Override
    public String getBenefit() {
        return benefit;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getSingle() {
        return single;
    }
}
