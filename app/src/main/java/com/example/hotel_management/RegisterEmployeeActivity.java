package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmployeeActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Spinner userTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);

        nameEditText = findViewById(R.id.editTextName); // Reference to the new EditText
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        userTypeSpinner = findViewById(R.id.spinnerUserType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> registerEmployee());
    }

    private void registerEmployee() {
        String name = nameEditText.getText().toString(); // Retrieve name from EditText
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String selectedUserType = userTypeSpinner.getSelectedItem().toString();

        // Register the user in Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User registration successful
                        String uid = task.getResult().getUser().getUid();
                        addUserToFirestore(uid, name, selectedUserType); // Add user to Firestore with name and userType
                        Toast.makeText(RegisterEmployeeActivity.this, "Employee registered successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the registration activity
                    } else {
                        // User registration failed
                        Toast.makeText(RegisterEmployeeActivity.this, "Error registering employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToFirestore(String uid, String name, String userType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name); // Add the name attribute
        user.put("userType", userType);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RegisterEmployeeActivity", "User successfully added to Firestore");
                    Toast.makeText(RegisterEmployeeActivity.this, "Employee registered successfully to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("RegisterEmployeeActivity", "Error adding user to Firestore", e);
                    Toast.makeText(RegisterEmployeeActivity.this, "Employee NOT registered to Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
