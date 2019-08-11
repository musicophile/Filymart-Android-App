package com.mart.filymart.fragment.BusketFragmentContent.model;

public interface IOrder {

    String getId();

    String getName();

    int getNumOfSongs();

    String getQuantity();

    String getThumbnail();

    public void setId(String id);

    public void setName(String name);

    public void setNumOfSongs(int numOfSongs);

    public void setQuantity(String quantity);

    public void setThumbnail(String thumbnail);
}
