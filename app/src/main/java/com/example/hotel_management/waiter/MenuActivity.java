package com.example.hotel_management.waiter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.FoodItem;
import com.example.hotel_management.datatypes.OrderItem;
import com.example.hotel_management.recyledview.MenuAdapterWaiter;
import com.example.hotel_management.recyledview.OrderListAdapterMenu;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private MenuAdapterWaiter menuAdapterWaiter;
    private ArrayList<FoodItem> foodItems;
    private RecyclerView orderRecyclerView;
    private OrderListAdapterMenu orderedListAdapter;
    private ArrayList<OrderItem> orderedItems;
    private SessionManager sessionManager;
    private SessionManager.AddOrderCallback addOrderCallback;
    private Boolean onGoingSession = false;
    private Integer tableID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        tableID = intent.getIntExtra("tableID", 0);
        //fetch table data from firestore
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_menu);

        foodRecyclerView = findViewById(R.id.menuItems);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Order list
        orderedItems = new ArrayList<>();
        orderedListAdapter = new OrderListAdapterMenu(orderedItems);
        orderRecyclerView = findViewById(R.id.orderList);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderRecyclerView.setAdapter(orderedListAdapter);

        // Session manager


        addOrderCallback = (totalBill,orderItem) -> {
            TextView beforeDiscount = findViewById(R.id.beforeDiscount);
            TextView afterDiscount = findViewById(R.id.afterDiscount);
            beforeDiscount.setText(totalBill.toString());
            afterDiscount.setText(totalBill.toString());
            orderedItems.add(orderItem);
            orderedListAdapter.notifyItemInserted(orderedItems.size()-1);
            Log.d("heyyou", "addOrderCallback: "+orderedItems.size());
        };

        sessionManager = new SessionManager(tableID,addOrderCallback,orderedListAdapter);

        //this is to check if there is an ongoing session for the table
        db.collection("sessions").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "session change listen failed.", error);
                return;
            }
            if(value == null){
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                Log.d("FirestoreData", "session change: " + change.getDocument().getData());
                if(!onGoingSession && change.getDocument().getLong("tableID").intValue()==tableID && change.getDocument().getBoolean("checkedOut").equals(false)){
                    if(change.getType() == DocumentChange.Type.ADDED){
                        Log.d("FirestoreData", "New session: " + change.getDocument().getData());
                        sessionManager = new SessionManager(tableID,addOrderCallback,orderedListAdapter);
                        sessionManager.firebaseDownload(change.getDocument().getId());
                        onGoingSession = true;
                    }
                }
            }
        });

        //food menu
        foodItems = new ArrayList<>();
        menuAdapterWaiter = new MenuAdapterWaiter(foodItems);
        menuAdapterWaiter.setOnFoodItemListener(this::OnFoodItemClick);
        foodRecyclerView.setAdapter(menuAdapterWaiter);

        fetchFoodData();

    }

    //this will fetch initial food items from firestore and set up listeners to the real time updates
    private void fetchFoodData() {
        db.collection("foods")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        foodItems.clear(); // Clear the list before adding items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            Integer price = document.getLong("price").intValue();
                            String type = document.getString("type");
                            String image = document.getString("url");
                            String documentId = document.getId();

                            // Check if the item with the same document ID already exists
                            boolean itemExists = false;
                            for (FoodItem existingItem : foodItems) {
                                if (existingItem.getDocumentId().equals(documentId)) {
                                    itemExists = true;
                                    break;
                                }
                            }

                            if (!itemExists) {
                                foodItems.add(new FoodItem(name, description, price, documentId, type, image));
                            }
                        }
                        menuAdapterWaiter.notifyDataSetChanged();
                    }
                });

        // Listen for real-time updates
        db.collection("foods")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("FirestoreRealtimeError", "Listen failed.", error);
                        return;
                    }
                    Log.d("FirestoreRealtime", "Snapshot listener triggered");
                    for (DocumentChange change : value.getDocumentChanges()) {
                        String documentId = change.getDocument().getId();

                        switch (change.getType()) {
                            case ADDED:
                                // Check if the item with the same document ID already exists
                                boolean itemExists = false;
                                for (FoodItem existingItem : foodItems) {
                                    if (existingItem.getDocumentId().equals(documentId)) {
                                        itemExists = true;
                                        break;
                                    }
                                }

                                if (!itemExists) {
                                    String name = change.getDocument().getString("name");
                                    String description = change.getDocument().getString("description");
                                    Integer price = change.getDocument().getLong("price").intValue();
                                    String type = change.getDocument().getString("type");
                                    String image = change.getDocument().getString("url");

                                    FoodItem newFoodItem = new FoodItem(name, description, price, documentId, type, image);
                                    foodItems.add(newFoodItem);

                                    menuAdapterWaiter.notifyDataSetChanged();
                                }
                                break;

                            case MODIFIED:
                                // Find the corresponding FoodItem using the document ID
                                for (int i = 0; i < foodItems.size(); i++) {
                                    if (foodItems.get(i).getDocumentId().equals(documentId)) {
                                        String modifiedName = change.getDocument().getString("name");
                                        String modifiedDescription = change.getDocument().getString("description");
                                        Integer modifiedPrice = change.getDocument().getLong("price").intValue();
                                        String modifiedtype = change.getDocument().getString("type");
                                        String modifiedImage = change.getDocument().getString("url");

                                        FoodItem modifiedFoodItem = new FoodItem(modifiedName, modifiedDescription, modifiedPrice, documentId, modifiedtype, modifiedImage);

                                        // Replace the old item with the modified one
                                        foodItems.set(i, modifiedFoodItem);
                                        menuAdapterWaiter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                            // Handle REMOVED case if needed
                        }
                    }
                });
    }

    private void OnFoodItemClick(FoodItem foodItem){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View popUpView = getLayoutInflater().inflate(R.layout.pop_up_order_confirm, null);
        TextView foodName = popUpView.findViewById(R.id.menuItemName);
        TextView foodPrice = popUpView.findViewById(R.id.menuItemPrice);
        Button confirmButton = popUpView.findViewById(R.id.addFoodConfirmButton);
        EditText quantity = popUpView.findViewById(R.id.orderQuantity);
        TextView totalPrice = popUpView.findViewById(R.id.orderTotalPrice);
        EditText notes = popUpView.findViewById(R.id.orderNotes);
        foodName.setText(foodItem.getName());
        foodPrice.setText(foodItem.getPrice().toString());
        totalPrice.setText(foodItem.getPrice().toString());
        builder.setView(popUpView);
        AlertDialog dialog = builder.create();
        confirmButton.setOnClickListener(v->{
            Integer quantityValue = Integer.parseInt(quantity.getText().toString());
            String note = notes.getText().toString();
            OrderItem orderItem = new OrderItem(foodItem.getName(), foodItem.getPrice(), quantityValue, tableID ,note ,foodItem.getImage());

            //this callback is used to get the order id from the orderitem class and add it to the session.
            onGoingSession = true;
            orderItem.setCallback(orderId -> {
                sessionManager.addOrder(orderItem);
                TextView beforeDiscount = findViewById(R.id.beforeDiscount);
                TextView afterDiscount = findViewById(R.id.afterDiscount);
                beforeDiscount.setText(sessionManager.getTotalBill().toString());
                afterDiscount.setText(sessionManager.getTotalBill().toString());
            });
            orderItem.firebaseUpload();
            dialog.dismiss();
        });
        dialog.show();
    }
}
