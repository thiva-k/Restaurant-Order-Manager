package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        // Retrieve food items from Firestore

        db.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String name = document.getString("name");
                            String description = document.getString("description"); // Replace with actual field name
                            Integer price = document.getLong("price").intValue(); // Replace with actual field name
                            Log.d("FirestoreData", "Name: " + name + ", Description: " + description);

                            foodItems.add(new FoodItem(name, description, price));
                        }
                        menuAdapter.notifyDataSetChanged();
                    }
                });
    }
}
