package com.example.hotel_management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddMenuItemActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, priceEditText;
    private Button addButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.editTextName);
        descriptionEditText = findViewById(R.id.editTextDescription);
        priceEditText = findViewById(R.id.editTextPrice);
        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(view -> addMenuItem());
    }

    private void addMenuItem() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();

        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Price cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodItem foodItem = new FoodItem(name, description, price);

        db.collection("foods")
                .add(foodItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMenuItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMenuItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                });
    }


    private void clearFields() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
    }
}
