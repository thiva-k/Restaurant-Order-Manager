package com.example.hotel_management.recyledview;


import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderListAdapterWaiter extends androidx.recyclerview.widget.RecyclerView.Adapter<OrderListAdapterWaiter.OrderItemViewHolder>{
    public ArrayList<OrderItem> orderItems;

    public OrderListAdapterWaiter(ArrayList<OrderItem> orderItems){
        this.orderItems = orderItems;
    }
    public interface OnOrderButtonClickListener {
        void OnOrderButtonClick(OrderItem orderItem);
    }
    private OnOrderButtonClickListener onOrderClickListener;
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_waiter, parent, false);
        return new OrderItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.foodName.setText(orderItem.name);
        holder.quantity.setText(orderItem.quantity.toString());
        String path ="https://i.ibb.co/m48NQyc/image-5.png";
        Picasso.get().load(path).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.status.setText(orderItem.status);
        holder.status.setTextColor(ContextCompat.getColor(holder.status.getContext(), getColor(orderItem.status)));
        holder.tableID.setText(orderItem.tableID.toString());
        holder.orderButton.setOnClickListener(v -> onOrderClickListener.OnOrderButtonClick(orderItem));
}

    @Override
    public int getItemCount() {
        return orderItems.size();
    }
    public static class OrderItemViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        public TextView foodName;
        public ImageView foodImage;
        public TextView quantity;
        public TextView status;
        public TextView tableID;
        public Button orderButton;
        public OrderItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.orderFoodName);
            foodImage = itemView.findViewById(R.id.foodImage);
            quantity = itemView.findViewById(R.id.orderQuantity);
            orderButton = itemView.findViewById(R.id.orderButton);
            tableID = itemView.findViewById(R.id.orderTableNumber);
            status = itemView.findViewById(R.id.status);
        }
    }
    public void setOnOrderButtonClickListener(OnOrderButtonClickListener onOrderClickListener){
        this.onOrderClickListener = onOrderClickListener;
    }
    private int getColor(String status){
        switch (status){
            case "Prepared":
                return R.color.Success;
            case "Delivering":
                return R.color.Failure;
            default:
                return R.color.Success;
        }
    }
}
