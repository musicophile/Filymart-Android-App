package com.mart.filymart.EggsMeatFish.model;

public class ProductModel implements IProduct {

    private String id;
    private String product_name;
    private String category;
    private int price;
    private String image;


    public ProductModel() {
    }

    public ProductModel(String id, String product_name, String category, int price, String image) {
        this.id = id;
        this.product_name = product_name;
        this.category = category;
        this.price = price;
        this.image = image;
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
    public String getCategory() {
        return category;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public String getImage() {
        return image;
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
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public void setImage(String image) {
        this.image = image;
    }
}
