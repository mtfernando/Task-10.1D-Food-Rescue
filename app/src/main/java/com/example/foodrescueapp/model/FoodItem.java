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
    private String locationID, locationAddress;
    private double locationLongitude, locationLatitude;
    private String quantity;
    private Bitmap imageRes;
    private Integer foodID;

    public FoodItem(String title, String description, String pickupDate, String pickupTime,
                    String locationID, String locationAddress, double locationLatitude,
                    double locationLongitude, String quantity, Bitmap imageRes) {
        this.title = title;
        this.description = description;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.locationID = locationID;
        this.locationAddress = locationAddress;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.quantity = quantity;
        this.imageRes = imageRes;
    }

    public FoodItem(){}

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

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public byte[] getImageRes() {
        return getBitmapAsByteArray(imageRes);
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

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public String getDetails(){
        String details = this.getDescription() + ", " + this.getQuantity() + ", " + this.getPickupDate() + ", "
                + this.getPickupTime() + ", " + this.getLocationAddress();



}
