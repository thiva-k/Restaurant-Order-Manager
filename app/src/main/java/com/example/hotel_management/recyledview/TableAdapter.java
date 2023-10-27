package com.example.hotel_management.recyledview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.Table;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<Table> tables;
    private OnTableDetailsListener onTableDetailsListener;
    private OnStartSessionListener onStartSessionListener;
    private OnCancelBookingListener onCancelBookingListener;
    private OnEndSessionListener onEndSessionListener;

    public interface OnTableDetailsListener {
        void OnTableDetailsClick(Table table);
    }
    public interface OnStartSessionListener {
        void OnStartSessionClick(Table table);
    }
    public interface OnCancelBookingListener {
        void OnCancelBookingClick(Table table);
    }
    public interface OnEndSessionListener {
        void OnEndSessionClick(Table table);
    }

    public TableAdapter(List<Table> tables) {
        this.tables = tables;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table table = tables.get(position);
        int color = ContextCompat.getColor(holder.itemView.getContext(), getColor(table.getStatus()));
        holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner_outer));
        holder.tableID.setText("Table "+ table.getTableID().toString());
        holder.tableStatus.setText(table.getStatus());
        holder.tableStatus.setTextColor(color);
        holder.viewDetailsButton.setOnClickListener(v -> {
            onTableDetailsListener.OnTableDetailsClick(table);
        });
        switch(table.getStatus()){
            case "Available":
                holder.startSessionButton.setVisibility(View.VISIBLE);
                holder.startSessionButton.setOnClickListener(v -> onStartSessionListener.OnStartSessionClick(table));
                break;
            case "Booked":
                holder.cancelBookingButton.setVisibility(View.VISIBLE);
                holder.cancelBookingButton.setOnClickListener(v -> onCancelBookingListener.OnCancelBookingClick(table));
                break;
            case "Ongoing":
                holder.editSessionButton.setVisibility(View.VISIBLE);
                holder.editSessionButton.setOnClickListener(v -> {
                    onStartSessionListener.OnStartSessionClick(table);
                });
                holder.endSessionButton.setVisibility(View.VISIBLE);
                holder.endSessionButton.setOnClickListener(v -> {
                    onEndSessionListener.OnEndSessionClick(table);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    //setters for listener interfaces
    public void setOnTableDetailsListener(OnTableDetailsListener onTableDetailsListener) {
        this.onTableDetailsListener = onTableDetailsListener;
    }
    public void setOnStartSessionListener(OnStartSessionListener onStartSessionListener) {
        this.onStartSessionListener = onStartSessionListener;
    }
    public void setOnCancelBookingListener(OnCancelBookingListener onCancelBookingListener) {
        this.onCancelBookingListener = onCancelBookingListener;
    }
    public void setOnEndSessionListener(OnEndSessionListener onEndSessionListener) {
        this.onEndSessionListener = onEndSessionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tableID;
        TextView tableStatus;
        Button startSessionButton;
        Button cancelBookingButton;
        Button editSessionButton;
        Button endSessionButton;
        Button viewDetailsButton;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tableID = itemView.findViewById(R.id.tableID);
            tableStatus = itemView.findViewById(R.id.tableStatus);
            startSessionButton = itemView.findViewById(R.id.startSessionButton);
            cancelBookingButton = itemView.findViewById(R.id.cancelBookingButton);
            editSessionButton = itemView.findViewById(R.id.editSessionButton);
            endSessionButton = itemView.findViewById(R.id.endSessionButton);
            viewDetailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }
    private int getColor(String status){
        switch (status){
            case "Available":
                return R.color.Success;
            case "Booked":
                return R.color.Ordered;
            case "Ongoing":
                return R.color.Failure;
            default:
                return R.color.Success;
        }
    }
}
