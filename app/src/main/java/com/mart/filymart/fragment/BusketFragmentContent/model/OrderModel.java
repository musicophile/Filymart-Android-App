package com.mart.filymart.fragment.BusketFragmentContent.model;

public class OrderModel implements IOrder {

    private String id;
    private String name;
    private int numOfSongs;
    private String quantity;
    private String thumbnail;

    public OrderModel() {
    }

    public OrderModel(String id, String name, int numOfSongs, String quantity, String thumbnail) {
        this.id = id;
        this.name = name;
        this.numOfSongs = numOfSongs;
        this.quantity = quantity;
        this.thumbnail = thumbnail;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumOfSongs() {
        return numOfSongs;
    }

    @Override
    public String getQuantity() {
        return quantity;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void setId(String id) {
        this.id = id;

    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs=numOfSongs;

    }

    @Override
    public void setQuantity(String quantity) {
        this.quantity = quantity;

    }

    @Override
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;

    }
}
