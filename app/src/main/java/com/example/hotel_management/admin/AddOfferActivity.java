package com.example.hotel_management.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.Offer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddOfferActivity extends AppCompatActivity {

    private Spinner foodNameSpinner;
    private EditText startDateEditText, endDateEditText, percentageEditText, descriptionEditText;
    private Button addOfferButton;
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormatter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker, endDatePicker;
    private List<String> foodNamesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        db = FirebaseFirestore.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        calendar = Calendar.getInstance();

        foodNameSpinner = findViewById(R.id.foodNameSpinner);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        percentageEditText = findViewById(R.id.percentageEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addOfferButton = findViewById(R.id.addOfferButton);
        foodNameSpinner.setPrompt("Select the item eligible for offer");

        // Initialize date pickers
        initializeDatePickers();

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

    // Initialize date pickers
    private void initializeDatePickers() {
        // Start Date Picker
        startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                startDateEditText.setText(dateFormatter.format(calendar.getTime()));
            }
        };

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        AddOfferActivity.this,
                        startDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        // End Date Picker
        endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                endDateEditText.setText(dateFormatter.format(calendar.getTime()));
            }
        };

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        AddOfferActivity.this,
                        endDatePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
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
