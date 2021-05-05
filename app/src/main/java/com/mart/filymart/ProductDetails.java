package com.mart.filymart;

public class ProductDetails {
    private String product_name;
    private String category;
    private int price;
    private String image;
    private String benefits;
    private String about;

    public ProductDetails() {
    }

    public ProductDetails(String product_name, String category, int price, String image, String benefits, String about) {
        this.product_name = product_name;
        this.category = category;
        this.price = price;
        this.image = image;
        this.about = about;
        this.benefits = benefits;
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

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
