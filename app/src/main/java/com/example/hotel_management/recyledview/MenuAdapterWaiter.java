package com.example.hotel_management.recyledview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.FoodItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapterWaiter extends RecyclerView.Adapter<MenuAdapterWaiter.ViewHolder> {

    private List<FoodItem> foodItems;
    private Picasso picasso;
    private OnFoodItemListener onFoodItemListener;

    public interface OnFoodItemListener {
        void OnFoodItemClick(FoodItem foodItem);
    }

    public MenuAdapterWaiter(List<FoodItem> foodItems) {
        this.foodItems = foodItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_waiter, parent, false);
        picasso = Picasso.get();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodPrice.setText(String.valueOf(foodItem.getPrice()));
        picasso.load(foodItem.getImage()).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.foodImage.setClipToOutline(true);
        holder.itemView.setOnClickListener(v -> onFoodItemListener.OnFoodItemClick(foodItem));
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void setOnFoodItemListener(OnFoodItemListener onFoodItemListener) {
        this.onFoodItemListener = onFoodItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView foodPrice;
        ImageView foodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodNameWaiter);
            foodPrice= itemView.findViewById(R.id.unitPriceWaiter);
            foodImage = itemView.findViewById(R.id.foodImageWaiter);
        }
    }
}
