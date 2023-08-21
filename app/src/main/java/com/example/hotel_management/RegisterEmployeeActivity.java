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

        nameEditText = findViewById(R.id.editTextName);
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
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String selectedUserType = userTypeSpinner.getSelectedItem().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        addUserToFirestore(uid, name, selectedUserType, email);
                        Toast.makeText(RegisterEmployeeActivity.this, "Employee registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterEmployeeActivity.this, "Error registering employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToFirestore(String uid, String name, String userType, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("userType", userType);
        user.put("email", email); // Add the email attribute

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
