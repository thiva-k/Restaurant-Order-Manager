package com.example.hotel_management.recyledview;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.Table;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    private List<Table> tables;
    private OnTableItemListener onTableItemListener;

    public interface OnTableItemListener {
        void OnTableItemClick(Table table);
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
        int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.Preparing);
        holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner_outer));
        holder.tableID.setText(table.tableID.toString());
        holder.tableStatus.setText(table.status);
        holder.tableStatus.setTextColor(color);
        holder.itemView.setOnClickListener(v -> onTableItemListener.OnTableItemClick(table));
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public void setOnTableItemListener(OnTableItemListener onTableItemListener) {
        this.onTableItemListener = onTableItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tableID;
        TextView tableStatus;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tableID = itemView.findViewById(R.id.tableID);
            tableStatus = itemView.findViewById(R.id.tableStatus);
        }
    }
}
