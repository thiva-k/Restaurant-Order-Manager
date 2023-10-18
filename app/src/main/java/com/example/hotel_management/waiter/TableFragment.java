package com.example.hotel_management.waiter;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.Table;
import com.example.hotel_management.recyledview.TableAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class TableFragment extends Fragment {
    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private ArrayList<Table> tables;
    private FirebaseFirestore db;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.waiterOrderRecyleView);
        tables = new ArrayList<>();
        tableAdapter = new TableAdapter(tables);
        recyclerView.setAdapter(tableAdapter);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 3));
        tableAdapter.setOnStartSessionListener(table -> {
            Intent intent = new Intent(getContext(), MenuActivity.class);
            intent.putExtra("tableID", table.getTableID());
            startActivity(intent);
        });
        tableAdapter.setOnEndSessionListener(table -> {
                    table.setStatus("Available");
                    tableAdapter.notifyDataSetChanged();
                    db.collection("tables").document(table.getTableID().toString()).update("status", "Available").addOnSuccessListener(documentReference -> {
                        Log.d("FirestoreData", "status successfully updated!");
                    }).addOnFailureListener(e -> {
                        Log.d("FirestoreData", "Error updating status", e);
                    });
                    //for now we are not updating the session status
//                    db.collection("sessions").document(table.lastSessionID).update("checkedOut", true).addOnSuccessListener(documentReference -> {
//                        Log.d("FirestoreData", "checkedOut successfully updated!");
//                    }).addOnFailureListener(e -> {
//                        Log.d("FirestoreData", "Error updating checkedOut", e);
//                    });
                }
        );

        tableAdapter.setOnTableDetailsListener(table -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_table_info);
            bottomSheetDialog.getBehavior().setMaxWidth(2000);
            TextView noBookingsText = bottomSheetDialog.findViewById(R.id.noBookingText);
            View divider = bottomSheetDialog.findViewById(R.id.divider);
            ConstraintLayout bookingLayout = bottomSheetDialog.findViewById(R.id.bookingExpanded);
            RecyclerView bookingRecyclerView = bottomSheetDialog.findViewById(R.id.tableBookingRecyclerView);
            noBookingsText.setVisibility(View.VISIBLE);
            divider.setVisibility(View.GONE);
            bookingLayout.setVisibility(View.GONE);
            bookingRecyclerView.setVisibility(View.GONE);
            bottomSheetDialog.show();
        });
        db = FirebaseFirestore.getInstance();
        db.collection("tables").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()){
                Log.d("FirestoreData", "No tables found");
                return;
            }
            tables.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Integer tableID = documentSnapshot.getLong("tableID").intValue();
                String tableStatus = documentSnapshot.getString("status");
                String lastSessionID = documentSnapshot.getString("lastSessionID");
                Table table = new Table(tableID, tableStatus, lastSessionID);
                tables.add(table);
            }
            tableAdapter.notifyDataSetChanged();
        });
        db.collection("tables").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("FirestoreData", "Error getting documents: ", error);
                return;
            }
            tables.clear();
            for (com.google.firebase.firestore.QueryDocumentSnapshot documentSnapshot : value) {
                Integer tableID = documentSnapshot.getLong("tableID").intValue();
                String tableStatus = documentSnapshot.getString("status");
                String lastSessionID = documentSnapshot.getString("lastSessionID");
                Table table = new Table(tableID, tableStatus, lastSessionID);
                tables.add(table);
            }
            tableAdapter.notifyDataSetChanged();
        });
        return view;
    }
}