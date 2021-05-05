package com.mart.filymart.CheckAddressContent.model;

public class AddressModel implements IAddress {

    private String id;
    private String name;
    private String location;

    public AddressModel() {
    }

    public AddressModel(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
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
    public String getLocation() {
        return location;
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
    public void setLocation(String location) {
        this.location = location;
    }
}
