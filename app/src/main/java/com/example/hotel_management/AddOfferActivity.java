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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;



public class AddOfferActivity extends AppCompatActivity {

    private Spinner foodNameSpinner;
    private EditText startDateEditText, endDateEditText, percentageEditText, descriptionEditText;
    private Button addOfferButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<String> foodNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        foodNameSpinner = findViewById(R.id.foodNameSpinner);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        percentageEditText = findViewById(R.id.percentageEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addOfferButton = findViewById(R.id.addOfferButton);

        // Call getFoodNames() to fetch food names from Firestore
        getFoodNames();

        addOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOffer();
            }
        });
    }

    // Fetch food names from the "foods" collection in Firestore
    private void getFoodNames() {
        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String foodName = document.getString("name");
                        if (foodName != null) {
                            foodNamesList.add(foodName);
                        }
                    }
                    // Update the spinner with the fetched food names
                    updateFoodNameSpinner();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddOfferActivity.this, "Failed to fetch food names", Toast.LENGTH_SHORT).show();
                });
    }

    // Update the spinner with the fetched food names
    private void updateFoodNameSpinner() {
        ArrayAdapter<String> foodNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodNamesList);
        foodNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodNameSpinner.setAdapter(foodNameAdapter);
    }

    // Add the offer to the "offers" collection in Firestore
    private void addOffer() {
        String foodName = foodNameSpinner.getSelectedItem().toString();
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();
        int percentage = Integer.parseInt(percentageEditText.getText().toString());
        String description = descriptionEditText.getText().toString();

        Offer offer = new Offer(foodName, startDate, endDate, percentage, description);

        db.collection("offers")
                .add(offer)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddOfferActivity.this, "Offer added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddOfferActivity.this, "Failed to add offer", Toast.LENGTH_SHORT).show();
                });
    }

    // Clear input fields
    private void clearFields() {
        startDateEditText.setText("");
        endDateEditText.setText("");
        percentageEditText.setText("");
        descriptionEditText.setText("");
    }
}
