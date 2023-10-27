package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;
    private String nameText;
    private String phoneText;
    private String addressText;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView email = findViewById(R.id.profileEmail);
        TextView usertype = findViewById(R.id.userType);
        EditText name = findViewById(R.id.profileName);
        EditText phone = findViewById(R.id.profilePhone);
        EditText address = findViewById(R.id.profileAddress);
        Button saveChanges = findViewById(R.id.profileEditSubmit);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getCurrentUser().getUid();
        db.collection("users").document(userID).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w("FirestoreData", "Listen failed.", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Document exists and has been changed
                email.setText(documentSnapshot.getString("email"));
                usertype.setText(documentSnapshot.getString("userType"));
                String updatedName = documentSnapshot.getString("name");
                String updatedPhone = documentSnapshot.getString("phone");
                String updatedAddress = documentSnapshot.getString("address");
                if (updatedName != null && !updatedName.equals(nameText)) {
                    name.setText(updatedName);
                    nameText = updatedName;
                }

                if (updatedPhone != null && !updatedPhone.equals(phoneText)) {
                    phone.setText(updatedPhone);
                    phoneText = updatedPhone;
                }

                if (updatedAddress != null && !updatedAddress.equals(addressText)) {
                    address.setText(updatedAddress);
                    addressText = updatedAddress;
                }
                saveChanges.setEnabled(false);
            } else {
                Log.d("FirestoreData", "Document does not exist.");
            }
        });
        saveChanges.setEnabled(false);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable the "Save" button when there is a change
                saveChanges.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        // Set TextWatchers to monitor changes in EditText fields
        name.addTextChangedListener(textWatcher);
        phone.addTextChangedListener(textWatcher);
        address.addTextChangedListener(textWatcher);
        saveChanges.setOnClickListener(v -> {
            Map<String, Object> data = new HashMap<>();
            data.put("name", name.getText().toString());
            data.put("phone", phone.getText().toString());
            data.put("address", address.getText().toString());
            db.collection("users").document(userID).update(data).addOnSuccessListener(
                    aVoid -> {
                        saveChanges.setEnabled(false);
                    }
            ).addOnFailureListener(e -> {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                name.setText(nameText);
                phone.setText(phoneText);
                address.setText(addressText);
                saveChanges.setEnabled(false);
            });
        });

    }
}