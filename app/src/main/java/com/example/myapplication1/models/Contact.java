package com.example.myapplication1.models;

public class Contact {
    private String name;
    private String phone;
    private String cost;

    public Contact(String name, String cost, String phone) {
        this.name = name;
        this.phone = phone;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }
    public String getCost() {
        return cost;
    }

    public String getPhone() {
        return phone;
    }
}
