package com.example.hotel_management.admin;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.hotel_management.datatypes.Employee;
import com.example.hotel_management.recyledview.EmployeeAdapter;
import com.example.hotel_management.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.pop_up_register_employee,null);
            EditText nameEditText = dialogView.findViewById(R.id.editTextName);
            EditText emailEditText = dialogView.findViewById(R.id.editTextEmail);
            EditText passwordEditText = dialogView.findViewById(R.id.editTextPassword);
            Spinner userTypeSpinner = dialogView.findViewById(R.id.spinnerUserType);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.user_types_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userTypeSpinner.setAdapter(adapter);
            Button registerButton = dialogView.findViewById(R.id.registerButton);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            registerButton.setOnClickListener(view1 ->{
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String selectedUserType = userTypeSpinner.getSelectedItem().toString();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = task.getResult().getUser().getUid();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("userType", selectedUserType);
                                user.put("email", email); // Add the email attribute

                                db.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("RegisterEmployeeActivity", "User successfully added to Firestore");
                                            Toast.makeText(getContext(), "Employee registered successfully to Firestore", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("RegisterEmployeeActivity", "Error adding user to Firestore", e);
                                            Toast.makeText(getContext(), "Employee NOT registered to Firestore", Toast.LENGTH_SHORT).show();
                                        });
                                Toast.makeText(getContext(), "Employee registered successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Error registering employee", Toast.LENGTH_SHORT).show();
                            }
                        });
            } );
            dialog.show();
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