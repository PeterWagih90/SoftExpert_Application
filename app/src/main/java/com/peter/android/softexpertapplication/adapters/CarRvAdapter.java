package com.peter.android.softexpertapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.peter.android.softexpertapplication.R;
import com.peter.android.softexpertapplication.models.CarModel;

import java.util.List;

public class CarRvAdapter extends RecyclerView.Adapter<CarRvAdapter.ProductViewHolder> {

    private final RequestOptions mRequestOptions;
    private List<CarModel> productsList;
    private Context context;

    public CarRvAdapter(List<CarModel> productsList, Context context) {
        this.productsList = productsList;
        this.context = context;
        mRequestOptions = new RequestOptions().placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_rv_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        CarModel carModel = productsList.get(position);
        holder.brand.setText(context.getString(R.string.brand, carModel.getBrand()));
        holder.condition.setText(carModel.isUsed()?context.getString(R.string.used):context.getString(R.string.newCar));
        holder.year.setText(String.format(context.getString(R.string.year), carModel.getConstractionYear()));
        // glide is a library for image loading and caching
        Glide.with(context).load(carModel.getImage()).apply(mRequestOptions).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView brand;
        TextView condition;
        TextView year;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.product_iv);
            brand = itemView.findViewById(R.id.product_brand);
            condition = itemView.findViewById(R.id.product_state);
            year = itemView.findViewById(R.id.product_year);
        }
    }
}
