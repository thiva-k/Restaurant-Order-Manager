package com.example.hotel_management.admin;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.FoodItem;
import com.example.hotel_management.recyledview.MenuAdapterAdmin;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private ArrayList<FoodItem> foodItems;
    private RecyclerView menuRecyclerView;
    private MenuAdapterAdmin menuAdapterAdmin;
    private FloatingActionButton addButton;
    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_menu, container, false);
        foodItems = new ArrayList<>();
        db= FirebaseFirestore.getInstance();
        menuRecyclerView = view.findViewById(R.id.foodEditRecylerView);
        menuAdapterAdmin = new MenuAdapterAdmin(foodItems);
        addButton = view.findViewById(R.id.addOfferItemButton);

        //this lisnter is called when a food item is clicked on the menu. this will open a pop up window to edit the food item
        menuAdapterAdmin.setOnFoodItemListener(foodItem -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.pop_up_menu_edit, null);
            TextView topic = dialogView.findViewById(R.id.foodEditTopic);
            topic.setText("Edit Food Item");
            EditText foodName = dialogView.findViewById(R.id.addFoodName);
            foodName.setText(foodItem.getName());
            EditText foodDescription = dialogView.findViewById(R.id.addFoodDescription);
            foodDescription.setText(foodItem.getDescription());
            EditText foodPrice = dialogView.findViewById(R.id.addFoodPrice);
            foodPrice.setText(foodItem.getPrice().toString());
            Spinner foodCategory = dialogView.findViewById(R.id.foodTypeSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.category_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodCategory.setAdapter(adapter);
            foodCategory.setSelection(adapter.getPosition(foodItem.getType()));
            Button editConfirmButton = dialogView.findViewById(R.id.addFoodConfirmButton);
            Button deleteButton = dialogView.findViewById(R.id.foodDeleteButton);
            deleteButton.setVisibility(View.VISIBLE);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            editConfirmButton.setOnClickListener(
                    v -> {
                        String name = foodName.getText().toString();
                        String description = foodDescription.getText().toString();
                        String priceStr = foodPrice.getText().toString();
                        String type = foodCategory.getSelectedItem().toString(); // Get the selected category from the spinner
                        if (priceStr.isEmpty()) {
                            Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int price;
                        try {
                            price = Integer.parseInt(priceStr);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        foodItem.setName(name);
                        foodItem.setDescription(description);
                        foodItem.setPrice(price);
                        foodItem.setType(type);

                        db.collection("foods")
                                .document(foodItem.getDocumentId())
                                .set(foodItem)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
                                });
                        menuAdapterAdmin.notifyDataSetChanged();
                        dialog.dismiss();
                    }
            );
            deleteButton.setOnClickListener(
                    v -> {
                        db.collection("foods")
                                .document(foodItem.getDocumentId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                                });
                        menuAdapterAdmin.notifyDataSetChanged();
                        dialog.dismiss();
                    }
            );
            dialog.show();
        });

        //this will open a pop up window to add a new food item
        addButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.pop_up_menu_edit, null);
            TextView topic = dialogView.findViewById(R.id.foodEditTopic);
            topic.setText("Add New Food Item");
            EditText foodName = dialogView.findViewById(R.id.addFoodName);
            EditText foodDescription = dialogView.findViewById(R.id.addFoodDescription);
            EditText foodPrice = dialogView.findViewById(R.id.addFoodPrice);
            Spinner foodCategory = dialogView.findViewById(R.id.foodTypeSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.category_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodCategory.setAdapter(adapter);
            Button confirmButton = dialogView.findViewById(R.id.addFoodConfirmButton);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            confirmButton.setOnClickListener(
                    v1 -> {
                        String name = foodName.getText().toString();
                        String description = foodDescription.getText().toString();
                        String priceStr = foodPrice.getText().toString();
                        String type = foodCategory.getSelectedItem().toString(); // Get the selected category from the spinner

                        if (priceStr.isEmpty()) {
                            Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int price;
                        try {
                            price = Integer.parseInt(priceStr);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FoodItem foodItem = new FoodItem(name, description, price, "", type,"https://i.ibb.co/wsv038X/Homemade-French-Fries-8.jpg");

                        db.collection("foods").add(foodItem).addOnSuccessListener(documentReference -> {
                            foodItem.setDocumentId(documentReference.getId());
                            Toast.makeText(getContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
                        });
                        menuAdapterAdmin.notifyDataSetChanged();
                        dialog.dismiss();
                    }
            );
            dialog.show();
        });
        menuRecyclerView.setAdapter(menuAdapterAdmin);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        //this will get all the food items from the database and display them in the menu
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
                            String image = document.getString("url");
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
                                foodItems.add(new FoodItem(name, description, price, documentId, type, image));
                            }
                        }
                        menuAdapterAdmin.notifyDataSetChanged();
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
                                    String type = change.getDocument().getString("type");
                                    String image = change.getDocument().getString("url");

                                    FoodItem newFoodItem = new FoodItem(name, description, price, documentId, type, image);
                                    foodItems.add(newFoodItem);

                                    menuAdapterAdmin.notifyDataSetChanged();
                                }
                                break;

                            case MODIFIED:
                                // Find the corresponding FoodItem using the document ID
                                for (int i = 0; i < foodItems.size(); i++) {
                                    if (foodItems.get(i).getDocumentId().equals(documentId)) {
                                        String modifiedName = change.getDocument().getString("name");
                                        String modifiedDescription = change.getDocument().getString("description");
                                        Integer modifiedPrice = change.getDocument().getLong("price").intValue();
                                        String modifiedtype = change.getDocument().getString("type");
                                        String modifiedImage = change.getDocument().getString("url");

                                        FoodItem modifiedFoodItem = new FoodItem(modifiedName, modifiedDescription, modifiedPrice, documentId, modifiedtype,modifiedImage);

                                        // Replace the old item with the modified one
                                        foodItems.set(i, modifiedFoodItem);
                                        menuAdapterAdmin.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;

                            case REMOVED:
                                // Find the corresponding FoodItem using the document ID
                                for (int i = 0; i < foodItems.size(); i++) {
                                    if (foodItems.get(i).getDocumentId().equals(documentId)) {
                                        // Remove the item from the list
                                        foodItems.remove(i);
                                        menuAdapterAdmin.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                });
        return view;
    }
}