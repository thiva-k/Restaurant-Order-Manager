package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private List<FoodItem> foodItems;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodItems = new ArrayList<>();
        menuAdapter = new MenuAdapter(foodItems);
        recyclerView.setAdapter(menuAdapter);

        // Retrieve initial food items from Firestore
        db.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodItems.clear(); // Clear the list before adding items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description"); // Replace with actual field name
                            Integer price = document.getLong("price").intValue(); // Replace with actual field name
                            String category = document.getString("category"); // Get the category field
                            String documentId = document.getId();

                            // Check if the item with the same document ID already exists
                            boolean itemExists = false;
                            for (FoodItem existingItem : foodItems) {
                                if (existingItem.getDocumentId().equals(documentId)) {
                                    itemExists = true;
                                    break;
                                }
                            }

                            if (!itemExists) {
                                foodItems.add(new FoodItem(name, description, price, documentId, category));
                            }
                        }
                        menuAdapter.notifyDataSetChanged();
                    }
                });

        // Listen for real-time updates
        db.collection("foods")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("FirestoreRealtimeError", "Listen failed.", error);
                        return;
                    }
                    Log.d("FirestoreRealtime", "Snapshot listener triggered");
                    for (DocumentChange change : value.getDocumentChanges()) {
                        String documentId = change.getDocument().getId();

                        switch (change.getType()) {
                            case ADDED:
                                // Check if the item with the same document ID already exists
                                boolean itemExists = false;
                                for (FoodItem existingItem : foodItems) {
                                    if (existingItem.getDocumentId().equals(documentId)) {
                                        itemExists = true;
                                        break;
                                    }
                                }

                                if (!itemExists) {
                                    String name = change.getDocument().getString("name");
                                    String description = change.getDocument().getString("description");
                                    Integer price = change.getDocument().getLong("price").intValue();
                                    String category = change.getDocument().getString("category"); // Get the category field

                                    FoodItem newFoodItem = new FoodItem(name, description, price, documentId, category);
                                    foodItems.add(newFoodItem);

                                    menuAdapter.notifyDataSetChanged();
                                }
                                break;

                            case MODIFIED:
                                // Find the corresponding FoodItem using the document ID
                                for (int i = 0; i < foodItems.size(); i++) {
                                    if (foodItems.get(i).getDocumentId().equals(documentId)) {
                                        String modifiedName = change.getDocument().getString("name");
                                        String modifiedDescription = change.getDocument().getString("description");
                                        Integer modifiedPrice = change.getDocument().getLong("price").intValue();
                                        String modifiedCategory = change.getDocument().getString("category"); // Get the modified category field

                                        FoodItem modifiedFoodItem = new FoodItem(modifiedName, modifiedDescription, modifiedPrice, documentId, modifiedCategory);

                                        // Replace the old item with the modified one
                                        foodItems.set(i, modifiedFoodItem);

                                        menuAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;

                            // Handle REMOVED case if needed
                        }
                    }
                });
    }
}
