package com.peter.android.softexpertapplication.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CarModel implements Serializable {

    @SerializedName("id")
    private int productId;
    @SerializedName("brand")
    private String brand;
    @SerializedName("constractionYear")
    private String constractionYear;
    @SerializedName("isUsed")
    private boolean isUsed;
    @SerializedName("imageUrl")
    private String image;


    public CarModel() {
    }

    public CarModel(int id, String brand, String constractionYear, String image, boolean isUsed) {
        this.brand = brand;
        this.constractionYear = constractionYear;
        this.image = image;
        this.productId = id;
        this.isUsed = isUsed;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getBrand() {
        return brand == null ? "Unknown Brand" : brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getConstractionYear() {
        return constractionYear == null ? "Unknown Year" : constractionYear;
    }

    public void setConstractionYear(String constractionYear) {
        this.constractionYear = constractionYear;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
