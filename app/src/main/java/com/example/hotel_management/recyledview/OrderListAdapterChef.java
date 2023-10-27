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

public class OrderListAdapterChef extends androidx.recyclerview.widget.RecyclerView.Adapter<OrderListAdapterChef.OrderItemViewHolder>{
    public ArrayList<OrderItem> orderItems;
    public Picasso picasso;

    public OrderListAdapterChef(ArrayList<OrderItem> orderItems){
        this.orderItems = orderItems;
    }
    public interface OnOrderButtonClickListener {
        void OnOrderButtonClick(OrderItem orderItem);
    }
    private OnOrderButtonClickListener onOrderClickListener;
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_chef, parent, false);
        picasso = Picasso.get();
        return new OrderItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.foodName.setText(orderItem.getName());
        holder.quantity.setText(orderItem.getQuantity().toString());
        picasso.load(orderItem.getImage()).error(R.drawable.baseline_emoji_food_beverage_24).into(holder.foodImage);
        holder.status.setText(orderItem.getStatus());
        holder.status.setTextColor(ContextCompat.getColor(holder.status.getContext(), getColor(orderItem.getStatus())));
        holder.tableID.setText(orderItem.getTableID().toString());
        holder.notes.setText(orderItem.getNotes());
        holder.orderButton.setOnClickListener(v ->
                onOrderClickListener.OnOrderButtonClick(orderItem));
        if(orderItem.getStatus().equals("Preparing")){
            holder.orderButton.setText("Prepared");
            holder.orderButton.setBackgroundColor(ContextCompat.getColor(holder.orderButton.getContext(), R.color.Success));
        }
        else if(orderItem.getStatus().equals("Ordered")){
            holder.orderButton.setText("Pick Up");
            holder.orderButton.setBackgroundColor(ContextCompat.getColor(holder.orderButton.getContext(), R.color.Failure));
        }
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
        public TextView notes;
        public Button orderButton;
        public OrderItemViewHolder(android.view.View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodNameWaiter);
            foodImage = itemView.findViewById(R.id.foodImageAdmin);
            quantity = itemView.findViewById(R.id.orderQuantity);
            orderButton = itemView.findViewById(R.id.orderButtonChef);
            tableID = itemView.findViewById(R.id.orderTableNumber);
            status = itemView.findViewById(R.id.status);
            notes = itemView.findViewById(R.id.orderFoodNotes);
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
