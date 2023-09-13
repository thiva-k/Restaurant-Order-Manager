package com.example.hotel_management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewOfferActivity extends AppCompatActivity {
    private RecyclerView offersRecyclerView;
    private OffersAdapter offersAdapter;
    private List<Offer> offerList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offer);

        db = FirebaseFirestore.getInstance();

        offersRecyclerView = findViewById(R.id.offersRecyclerView);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        offersAdapter = new OffersAdapter(offerList, new OffersAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // Handle edit button click here
                Toast.makeText(ViewOfferActivity.this, "Edit clicked for position " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                // Handle delete button click here
                Toast.makeText(ViewOfferActivity.this, "Delete clicked for position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        offersRecyclerView.setAdapter(offersAdapter);

        // Call method to fetch offers from Firestore
        getOffers();
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
                    Toast.makeText(ViewOfferActivity.this, "Failed to fetch offers", Toast.LENGTH_SHORT).show();
                });
    }
}
