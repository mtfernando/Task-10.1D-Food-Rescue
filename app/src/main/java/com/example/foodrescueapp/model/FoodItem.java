package com.example.foodrescueapp.model;

public class FoodItem {
    private String title, description, pickupDate, pickupTime, location, quantity;

    public FoodItem(String title, String description, String pickupDate, String pickupTime, String location, String quantity) {
        this.title = title;
        this.description = description;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.location = location;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
