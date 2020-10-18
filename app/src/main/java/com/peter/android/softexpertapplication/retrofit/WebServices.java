package com.peter.android.softexpertapplication.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WebServices {
    @Headers({"Accept: application/json"})
    @GET("cars")
    Call<ProductsResponse> getProducts(@Query("page") int page);
}
