package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.datatypes.FoodItem;
import com.example.hotel_management.datatypes.OrderItem;
import com.example.hotel_management.recyledview.MenuAdapter;
import com.example.hotel_management.recyledview.OrderListAdapterMenu;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private MenuAdapter menuAdapter;
    private ArrayList<FoodItem> foodItems;
    private RecyclerView orderRecyclerView;
    private OrderListAdapterMenu orderListAdapter;
    private ArrayList<OrderItem> orderItems;
    private Integer tableID;
    private String status;
    private String sessionID;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        tableID = intent.getIntExtra("tableID", 0);
        //fetch table data from firestore
        db = FirebaseFirestore.getInstance();
        db.collection("tables").document(tableID.toString()).get().addOnSuccessListener(documentSnapshot -> {
            status = documentSnapshot.getString("status");
            sessionID = documentSnapshot.getString("lastSessionID");
        });
        setContentView(R.layout.activity_menu);

        foodRecyclerView = findViewById(R.id.menuItems);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Order list
        orderItems = new ArrayList<>();
        orderListAdapter = new OrderListAdapterMenu(orderItems);
        orderRecyclerView = findViewById(R.id.orderList);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderRecyclerView.setAdapter(orderListAdapter);

        //food menu
        foodItems = new ArrayList<>();
        menuAdapter = new MenuAdapter(foodItems);
        menuAdapter.setOnFoodItemListener((foodItem)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View popUpView = getLayoutInflater().inflate(R.layout.pop_up_order_confirm, null);
            TextView foodName = popUpView.findViewById(R.id.menuItemName);
            TextView foodPrice = popUpView.findViewById(R.id.menuItemPrice);
            Button confirmButton = popUpView.findViewById(R.id.addFoodConfirmButton);
            EditText quantity = popUpView.findViewById(R.id.orderQuantity);
            TextView totalPrice = popUpView.findViewById(R.id.orderTotalPrice);
            EditText notes = popUpView.findViewById(R.id.orderNotes);
            foodName.setText(foodItem.getName());
            foodPrice.setText(foodItem.getPrice().toString());
            totalPrice.setText(foodItem.getPrice().toString());
            builder.setView(popUpView);
            AlertDialog dialog = builder.create();
            confirmButton.setOnClickListener(v->{
                Integer quantityValue = Integer.parseInt(quantity.getText().toString());
                String note = notes.getText().toString();
                OrderItem orderItem = new OrderItem(foodItem.getName(), foodItem.getPrice(), quantityValue, tableID ,note);
                //this callback is called when the order is added to the database. we are checking whether there is an ongoing session in the given table
                //if that's the case, the order will be added to that session. otherwise a new session will be created
                orderItem.setCallback((orderId -> {
                    if (status.equals("Available")){
                        Map<String,Object> data= new HashMap<>();
                        data.put("checkOut",false);
                        data.put("paid",false);
                        data.put("tableID",tableID);
                        data.put("orders", Arrays.asList(orderId));
                        data.put("totalBill",orderItem.totalPrice);
                        db.collection("sessions").add(data).addOnSuccessListener(documentReference -> {
                            orderItem.sessionID = documentReference.getId();
                            db.collection("orders").document(orderId).update("sessionID",orderItem.sessionID).addOnSuccessListener(documentReference1 -> {
                                orderItems.add(orderItem);
                                orderListAdapter.notifyItemInserted(orderItems.size()-1);
                            }).addOnFailureListener(e -> {
                                Log.d("FirestoreData", "Error updating order", e);
                            });
                            Log.d("FirestoreData", "New session created with ID: " + orderItem.sessionID);
                        }).addOnFailureListener(e -> {
                            orderItem.sessionID = null;
                            Log.d("FirestoreData", "Error creating session", e);
                        });
                    }
                    else if(status.equals("Ongoing")){
                        db.collection("sessions").document(sessionID).update("orders", FieldValue.arrayUnion(orderId)).addOnSuccessListener(documentReference -> {
                            orderItem.sessionID = sessionID;
                            db.collection("orders").document(orderId).update("sessionID",orderItem.sessionID).addOnSuccessListener(documentReference1 -> {
                                orderItems.add(orderItem);
                                orderListAdapter.notifyItemInserted(orderItems.size()-1);
                            }).addOnFailureListener(e -> {
                                Log.d("FirestoreData", "Error updating order", e);
                            });
                            Log.d("FirestoreData", "New session created with ID: " + orderItem.sessionID);
                        }).addOnFailureListener(e -> {
                            orderItem.sessionID = null;
                            Log.d("FirestoreData", "Error creating session", e);
                        });
                    }
                    else{
                        Toast.makeText(this, "Table is booked at the moment", Toast.LENGTH_SHORT).show();
                    }
                }));
                orderItem.firestoreUpload();
            dialog.dismiss();
            });
            dialog.show();
            }
            );

        foodRecyclerView.setAdapter(menuAdapter);

        // Retrieve initial food items from Firestore
        db.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodItems.clear(); // Clear the list before adding items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            Integer price = document.getLong("price").intValue();
                            String type = document.getString("type");
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
                                foodItems.add(new FoodItem(name, description, price, documentId, type));
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
                                    String type = change.getDocument().getString("type"); // Get the type field

                                    FoodItem newFoodItem = new FoodItem(name, description, price, documentId, type);
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
                                        String modifiedtype = change.getDocument().getString("type"); // Get the modified type field

                                        FoodItem modifiedFoodItem = new FoodItem(modifiedName, modifiedDescription, modifiedPrice, documentId, modifiedtype);

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
