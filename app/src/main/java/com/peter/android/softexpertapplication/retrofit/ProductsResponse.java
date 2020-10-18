package com.peter.android.softexpertapplication.retrofit;

import com.google.gson.annotations.SerializedName;
import com.peter.android.softexpertapplication.models.CarModel;

import java.util.List;

public class ProductsResponse {

    @SerializedName("status")
    private int status;
    public int getStatus() {
        return status;
    }

    @SerializedName("data")
    private List<CarModel> productsList;

    public List<CarModel> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<CarModel> productsList) {
        this.productsList = productsList;
    }
}
