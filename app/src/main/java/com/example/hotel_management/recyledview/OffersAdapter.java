package com.example.hotel_management.recyledview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.datatypes.Offer;
import com.example.hotel_management.R;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    private List<Offer> offerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    public OffersAdapter(List<Offer> offerList, OnItemClickListener listener) {
        this.offerList = offerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);

        // Bind offer details to views
        holder.offerDetailsTextView.setText("Item:   " + offer.getName() +
                "\nStart Date:   " + offer.getStartDate() +
                "\nEnd Date:   " + offer.getEndDate() +
                "\nPercentage:   " + offer.getPercentage() + "%" +
                "\nDescription:   " + offer.getDescription());

        // Set click listeners for edit and delete buttons
        holder.editOfferButton.setOnClickListener(view -> listener.onEditClick(position));
        holder.deleteOfferButton.setOnClickListener(view -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView offerDetailsTextView;
        public Button editOfferButton;
        public Button deleteOfferButton;

        public OfferViewHolder(View itemView) {
            super(itemView);
            offerDetailsTextView = itemView.findViewById(R.id.offerDetailsTextView);
            editOfferButton = itemView.findViewById(R.id.editOfferButton);
            deleteOfferButton = itemView.findViewById(R.id.deleteEmployeeButton);
        }
    }
}
