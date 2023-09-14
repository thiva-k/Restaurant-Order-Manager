package com.example.hotel_management.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hotel_management.datatypes.Employee;
import com.example.hotel_management.recyledview.EmployeeAdapter;
import com.example.hotel_management.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EmployeeFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton addEmployeeButton;
    private EmployeeAdapter employeeAdapter;
    private ArrayList<Employee> employeeList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        addEmployeeButton = view.findViewById(R.id.addEmployeeButton);
        addEmployeeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterEmployeeActivity.class);
            startActivity(intent);
        });
        recyclerView = view.findViewById(R.id.employeeEditRecylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(employeeAdapter);
        retrieveEmployees();
        return view;
    }

    private void retrieveEmployees() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String email = document.getString("email"); // Retrieve email directly from Firestore
                    String userType = document.getString("userType");

                    employeeList.add(new Employee(name, email, userType));
                }
                employeeAdapter.notifyDataSetChanged();
            } else {
                Log.d("Error getting documents: ", String.valueOf(task.getException()));
            }
        });
    }
}