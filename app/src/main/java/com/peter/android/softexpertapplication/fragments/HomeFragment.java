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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.peter.android.softexpertapplication.models.CarModel;
import com.peter.android.softexpertapplication.adapters.CarRvAdapter;
import com.peter.android.softexpertapplication.R;
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

        dialog = new ProgressDialog(requireContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        getAllProducts(page);
        setUpRecyclerView();

    }

    private void getAllProducts(int page) {
        connectionIv.setVisibility(View.INVISIBLE);
        webServices = RetrofitFactory.getRetrofit().create(WebServices.class);

        Call<ProductsResponse> getProducts = webServices.getProducts(page);

        getProducts.enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                dialog.dismiss();
                try {
                    productsList.addAll(response.body().getProductsList());
                    productsAdapter.notifyDataSetChanged();
                    connectionIv.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    Log.e("Error",response.message());
                    connectionIv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                Log.e("Error",t.getMessage());
                connectionIv.setVisibility(View.VISIBLE);
                dialog.dismiss();
                // Logs .. logging
                Toast.makeText(requireContext(), "Network Problem", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        productRv.setLayoutManager(layoutManager);
        productRv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(14), true));
        productRv.setItemAnimator(new DefaultItemAnimator());
        productRv.setAdapter(productsAdapter);
        productRv.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(productsList.size()>0){
                    page++;
                }else{
                    page = 1;
                }
                getAllProducts(page);
            }
        });
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
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
        outState.putInt("page",page);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
        page = savedInstanceState.getInt(getString(R.string.page),1);
    }
}