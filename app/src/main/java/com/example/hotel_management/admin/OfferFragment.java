package com.example.hotel_management.admin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hotel_management.datatypes.Offer;
import com.example.hotel_management.recyledview.OffersAdapter;
import com.example.hotel_management.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OfferFragment extends Fragment {
    private RecyclerView offersRecyclerView;
    private FloatingActionButton addOfferButton;
    private OffersAdapter offersAdapter;
    private ArrayList<Offer> offerList = new ArrayList<>();
    private FirebaseFirestore db;
    private SimpleDateFormat dateFormatter;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener startDatePicker, endDatePicker;
    private ArrayList<String> foodNamesList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_offer, container, false);
        db = FirebaseFirestore.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        calendar = Calendar.getInstance();

        addOfferButton = view.findViewById(R.id.addOfferItemButton);
        addOfferButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.pop_up_add_offer,null);
            Spinner foodNameSpinner = dialogView.findViewById(R.id.foodNameSpinner);
            EditText startDateEditText = dialogView.findViewById(R.id.startDateEditText);
            EditText endDateEditText = dialogView.findViewById(R.id.endDateEditText);
            EditText percentageEditText = dialogView.findViewById(R.id.percentageEditText);
            EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
            Button addOfferButton = dialogView.findViewById(R.id.addOfferButton);
            foodNameSpinner.setPrompt("Select the item eligible for offer");
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            addOfferButton.setOnClickListener(view1 -> {
                String foodName = foodNameSpinner.getSelectedItem().toString();
                String startDate = startDateEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                int percentage = Integer.parseInt(percentageEditText.getText().toString());
                String description = descriptionEditText.getText().toString();

                Offer offer = new Offer(foodName, startDate, endDate, percentage, description);

                db.collection("offers")
                        .add(offer)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Offer added successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to add offer", Toast.LENGTH_SHORT).show();
                        });
                }
            );

            //initialize date pickers
            startDatePicker = (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                startDateEditText.setText(dateFormatter.format(calendar.getTime()));
            };

            startDateEditText.setOnClickListener(v1 -> new DatePickerDialog(getContext(), startDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show());

            // End Date Picker
            endDatePicker = (view12, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);
                endDateEditText.setText(dateFormatter.format(calendar.getTime()));
            };

            endDateEditText.setOnClickListener(v12 -> new DatePickerDialog(getContext(), endDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ).show());

            // Fetch food names from the "foods" collection in Firestore
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
                        ArrayAdapter<String> foodNameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, foodNamesList);
                        foodNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        foodNameSpinner.setAdapter(foodNameAdapter);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch food names", Toast.LENGTH_SHORT).show();
                    });
            dialog.show();
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