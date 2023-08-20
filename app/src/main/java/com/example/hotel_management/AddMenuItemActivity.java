package com.example.hotel_management;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddMenuItemActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, priceEditText;
    private  Spinner spinnerCategory;
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
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        addButton.setOnClickListener(view -> addMenuItem());
    }

    private void addMenuItem() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString(); // Get the selected category from the spinner

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

        FoodItem foodItem = new FoodItem(name, description, price, "", category); // Empty initial documentId

        db.collection("foods")
                .add(foodItem)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId(); // Get the generated document ID
                    foodItem.setDocumentId(documentId); // Set the documentId in the FoodItem
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
