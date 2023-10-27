package com.example.hotel_management.admin;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.hotel_management.datatypes.Booking;
import com.example.hotel_management.datatypes.Offer;
import com.example.hotel_management.recyledview.OffersAdapter;
import com.example.hotel_management.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
            addOrEditOffer(null);
        });

        offersRecyclerView = view.findViewById(R.id.offersRecyclerView);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        offersAdapter = new OffersAdapter(offerList, new OffersAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                addOrEditOffer(offerList.get(position));
            }

            @Override
            public void onDeleteClick(int position) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
                builder.setTitle("Delete Offer");
                builder.setBackground(view.getContext().getDrawable(R.drawable.rounded_corners));
                builder.setMessage("Are you sure you want to delete this offer?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("offers").document(offerList.get(position).getOfferID())
                            .delete()
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getContext(), "Offer deleted successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to delete offer", Toast.LENGTH_SHORT).show();
                            });
                    dialog.dismiss();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.show();
            }
        });
        offersRecyclerView.setAdapter(offersAdapter);
        addOffersSnapshotListener();
        return view;
    }

    private void addOrEditOffer(Offer offer) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.pop_up_add_offer,null);
        Spinner foodNameSpinner = dialogView.findViewById(R.id.foodNameSpinner);
        EditText startDateEditText = dialogView.findViewById(R.id.startDateEditText);
        EditText endDateEditText = dialogView.findViewById(R.id.endDateEditText);
        EditText percentageEditText = dialogView.findViewById(R.id.percentageEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        Button confirmAddOfferButton = dialogView.findViewById(R.id.addOfferButton);
        foodNameSpinner.setPrompt("Select the item eligible for offer");
        if(offer!=null){
            foodNameSpinner.setSelection(foodNamesList.indexOf(offer.getName()));
            startDateEditText.setText(offer.getStartDate());
            endDateEditText.setText(offer.getEndDate());
            percentageEditText.setText(String.valueOf(offer.getPercentage()));
            descriptionEditText.setText(offer.getDescription());
            confirmAddOfferButton.setText("Update Offer");
        }
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        confirmAddOfferButton.setOnClickListener(view1 -> {
            String foodName = foodNameSpinner.getSelectedItem().toString();
            String startDate = startDateEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();
            int percentage = Integer.parseInt(percentageEditText.getText().toString());
            String description = descriptionEditText.getText().toString();
            Map<String,Object> newOffer = new HashMap<>();
            newOffer.put("name", foodName);
            newOffer.put("startDate", startDate);
            newOffer.put("endDate", endDate);
            newOffer.put("percentage", percentage);
            newOffer.put("description", description);
            if(offer==null){
                db.collection("offers")
                        .add(newOffer)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Offer added successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to add offer", Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                db.collection("offers").document(offer.getOfferID())
                        .update(newOffer)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Offer updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update offer", Toast.LENGTH_SHORT).show();
                        });
            }
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
    }

    private void addOffersSnapshotListener() {
        db.collection("offers")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Failed to fetch offers", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (DocumentChange change : value.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                // Handle added document (already implemented)
                                String name = change.getDocument().getString("name");
                                String startDate = change.getDocument().getString("startDate");
                                String endDate = change.getDocument().getString("endDate");
                                Integer percentage = change.getDocument().getLong("percentage").intValue();
                                String description = change.getDocument().getString("description");
                                String offerID = change.getDocument().getId();
                                Offer offer = new Offer(name, startDate, endDate, percentage, description, offerID);
                                offerList.add(offer);
                                break;
                            case MODIFIED:
                                // Handle modified document
                                String modifiedOfferID = change.getDocument().getId();
                                // Find the corresponding Offer object in your offerList
                                for (Offer temp : offerList) {
                                    if (temp.getOfferID().equals(modifiedOfferID)) {
                                        // Update the fields of the offer if needed
                                        temp.setName(change.getDocument().getString("name"));
                                        temp.setStartDate(change.getDocument().getString("startDate"));
                                        temp.setEndDate(change.getDocument().getString("endDate"));
                                        temp.setPercentage(change.getDocument().getLong("percentage").intValue());
                                        temp.setDescription(change.getDocument().getString("description"));
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                // Handle removed document
                                String removedOfferID = change.getDocument().getId();
                                // Find and remove the corresponding Offer object from your offerList
                                offerList.removeIf(temp -> temp.getOfferID().equals(removedOfferID));
                                break;
                        }
                    }
                    // Notify your adapter or UI of the data changes
                    offersAdapter.notifyDataSetChanged();
                });
    }

}