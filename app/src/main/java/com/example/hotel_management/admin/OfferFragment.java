package com.example.hotel_management.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hotel_management.datatypes.Offer;
import com.example.hotel_management.recyledview.OffersAdapter;
import com.example.hotel_management.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class OfferFragment extends Fragment {
    private RecyclerView offersRecyclerView;
    private FloatingActionButton addOfferButton;
    private OffersAdapter offersAdapter;
    private ArrayList<Offer> offerList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_offer, container, false);


        db = FirebaseFirestore.getInstance();

        addOfferButton = view.findViewById(R.id.addOfferItemButton);
        addOfferButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddOfferActivity.class);
            startActivity(intent);
        });

        offersRecyclerView = view.findViewById(R.id.offersRecyclerView);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offersAdapter = new OffersAdapter(offerList, new OffersAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // Handle edit button click here
                Toast.makeText(getContext(), "Edit clicked for position " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                // Handle delete button click here
                Toast.makeText(getContext(), "Delete clicked for position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        offersRecyclerView.setAdapter(offersAdapter);
        getOffers();
        return view;
    }

    private void getOffers() {
        db.collection("offers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Offer offer = document.toObject(Offer.class);
                        offerList.add(offer);
                    }
                    offersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch offers", Toast.LENGTH_SHORT).show();
                });
    }
}