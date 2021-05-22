package com.example.foodrescueapp.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.foodrescueapp.util.Util;

import java.io.ByteArrayOutputStream;

public class FoodItem {
    private String title;
    private String description;
    private String pickupDate;
    private String pickupTime;
    private String location;
    private String quantity;
    private Bitmap imageRes;
    private Integer foodID;

    public FoodItem(String title, String description, String pickupDate, String pickupTime, String location, String quantity, Bitmap imageRes) {
        this.title = title;
        this.description = description;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.location = location;
        this.quantity = quantity;
        this.imageRes = imageRes;
    }

    public FoodItem(){

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

    public String getLocation() { return location; }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public byte[] getImageRes() {
        return Util.getBitmapAsByteArray(imageRes);
    }

    public void setImageRes(Bitmap imageRes) {
        this.imageRes = imageRes;
    }

    public Integer getFoodID() {
        return foodID;
    }

    public void setFoodID(Integer foodID) {
        this.foodID = foodID;
    }

    //Returns all details excluding Title in String
    public String getDetails(){
        String details = this.getDescription() + ", " + this.getQuantity() + ", " + this.getPickupDate() + ", "
                + this.getPickupTime() + ", " + this.getLocation();

        return details;
    }
}
