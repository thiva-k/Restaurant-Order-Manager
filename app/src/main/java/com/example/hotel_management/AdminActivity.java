package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button registerUserButton = findViewById(R.id.registerUserButton);
        Button addMenuItemButton = findViewById(R.id.addMenuItemButton);
        Button viewEmployeesButton = findViewById(R.id.viewEmployeesButton); // Add this line

        registerUserButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, RegisterEmployeeActivity.class);
            startActivity(intent);
        });

        addMenuItemButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, AddMenuItemActivity.class);
            startActivity(intent);
        });

        viewEmployeesButton.setOnClickListener(view -> { // Add this block
            Intent intent = new Intent(AdminActivity.this, ViewEmployeesActivity.class);
            startActivity(intent);
        });
    }
}
