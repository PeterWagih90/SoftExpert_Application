package com.peter.android.softexpertapplication.fragments;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.peter.android.softexpertapplication.R;
import com.peter.android.softexpertapplication.adapters.CarRvAdapter;
import com.peter.android.softexpertapplication.models.CarModel;
import com.peter.android.softexpertapplication.retrofit.ProductsResponse;
import com.peter.android.softexpertapplication.retrofit.RetrofitFactory;
import com.peter.android.softexpertapplication.retrofit.WebServices;
import com.peter.android.softexpertapplication.utility.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView productRv;
    private List<CarModel> productsList = new ArrayList<>();
    private CarRvAdapter productsAdapter;

    private WebServices webServices;
    private ProgressDialog dialog;
    private int page = 1;
    private SwipeRefreshLayout swipeContainer;
    private ImageView connectionIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        productRv = view.findViewById(R.id.product_rv);
        connectionIv = view.findViewById(R.id.status_image);
        connectionIv.setVisibility(View.INVISIBLE);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 1;// reset
                productsList.clear();
                productsAdapter.notifyDataSetChanged();
                getAllProducts(page);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
        getAllProducts(page);// first run!
    }

    private void getAllProducts(int page) {
        connectionIv.setVisibility(View.INVISIBLE);
        webServices = RetrofitFactory.getRetrofit().create(WebServices.class);

        Call<ProductsResponse> getProducts = webServices.getProducts(page);
        //http://demo1585915.mockable.io/api/v1/cars?page=%7Bpage%7D
        getProducts.enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                try {
                    List<CarModel> cars = response.body().getProductsList();
                    if (cars == null || cars.isEmpty()) {
                        HomeFragment.this.page = 1;
                    } else {
                        productsList.addAll(cars);
                        productsAdapter.notifyDataSetChanged();
                    }

                    connectionIv.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    HomeFragment.this.page = 1;
                    Log.e("Error", call.request().body() + " " + response.message());
                    connectionIv.setVisibility(View.VISIBLE);
                } finally {
                    swipeContainer.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                HomeFragment.this.page = 1;
                Log.e("Error", t.getMessage());
                connectionIv.setVisibility(View.VISIBLE);
                swipeContainer.setRefreshing(false);

                // Logs .. logging
                Toast.makeText(requireContext(), "Network Problem", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpRecyclerView() {
        productsAdapter = new CarRvAdapter(productsList, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        productRv.setLayoutManager(layoutManager);
        productRv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        productRv.setItemAnimator(new DefaultItemAnimator());
        productRv.setAdapter(productsAdapter);
        productRv.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (productsList.size() > 0) {
                    HomeFragment.this.page++;
                } else {
                    HomeFragment.this.page = 1;
                }
                getAllProducts(page);
                resetState();
            }
        });
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", page);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
            page = savedInstanceState.getInt(getString(R.string.page), 1);
    }
}