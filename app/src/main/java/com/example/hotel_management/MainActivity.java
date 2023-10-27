package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotel_management.admin.AdminActivity;
import com.example.hotel_management.waiter.WaiterActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextTextPassword);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> signInWithEmailPassword());
    }

    private void signInWithEmailPassword() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Retrieve user type from Firestore and start appropriate activity
                            getUserTypeAndStartActivity(user.getUid());
                        }
                    } else {
                        //Give a toast message that login is unsuccessful
                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        emailEditText.setText("");
                        passwordEditText.setText("");

                        // Handle authentication failure
                    }
                });
    }

    private void getUserTypeAndStartActivity(String uid) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userType = document.getString("userType");
                            switch (userType) {
                                case "admin":
                                    startActivity(new Intent(this, AdminActivity.class));
                                    break;
                                case "chef":
                                    startActivity(new Intent(this, ChefActivity.class));
                                    break;
                                case "waiter":
                                    startActivity(new Intent(this, WaiterActivity.class));
                                    break;
                                default:
                                    // Handle unknown user types or show a default activity
                                    break;
                            }
                        } else {
                            // Document does not exist, handle accordingly
                        }
                    } else {
                        // Handle exceptions
                    }
                });
    }

}

