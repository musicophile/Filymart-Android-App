package com.example.filymart;

public class Product {
    private String id;
    private String product_name;
    private String category;
    private int price;
    private String image;

    public Product() {
    }

    public Product(String id, String product_name, String category, int price, String image) {
        this.id = id;
        this.product_name = product_name;
        this.category = category;
        this.price = price;
        this.image = image;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }



}
