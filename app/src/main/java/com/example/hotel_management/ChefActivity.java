package com.example.hotel_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hotel_management.datatypes.OrderItem;
import com.example.hotel_management.recyledview.OrderListAdapterChef;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ChefActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderListAdapterChef orderListAdapterChef;
    private ArrayList<OrderItem> orderItems;
    private TextView noOrdersText;
    private FirebaseFirestore db;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);
        noOrdersText = findViewById(R.id.noOrdersText2);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.chefOrderRecycleView);
        orderItems = new ArrayList<>();
        orderListAdapterChef = new OrderListAdapterChef(orderItems);
        orderListAdapterChef.setOnOrderButtonClickListener(orderItem -> {
            if(orderItem.status.equals("Ordered")){
                orderItem.status = "Preparing";
                orderItems.add(0, orderItem);
                db.collection("orders").document(orderItem.orderID).update("status", "Preparing").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
            else{
                orderItem.status="Prepared";
                orderItems.remove(orderItem);
                if(orderItems.size()==0){
                    noOrdersText.setVisibility(View.VISIBLE);
                }
                orderListAdapterChef.notifyDataSetChanged();
                db.collection("orders").document(orderItem.orderID).update("status", "Prepared").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
        });
        recyclerView.setAdapter(orderListAdapterChef);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChefActivity.this, LinearLayoutManager.VERTICAL, false));


        //listen to real time changes in the database
        //if a new item is added then that will have the status "Prepared". so we need to add that to the list
        //if an item status changed from "Prepared" to "Delivering" then we need to remove that from the list
        db.collection("orders").addSnapshotListener(((value, error) ->{
            if (error != null) {
                Log.w("FirestoreData", "order change listen failed.", error);
                return;
            }
            if(value == null){
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        if(change.getDocument().getString("status").equals("Ordered")){
                            Log.d("FirestoreData", "New order: " + change.getDocument().getData());
                            String name = change.getDocument().getString("name");
                            Integer price = change.getDocument().getLong("price").intValue();
                            Integer quantity = change.getDocument().getLong("quantity").intValue();
                            Integer tableID = change.getDocument().getLong("tableID").intValue();
                            String notes = change.getDocument().getString("notes");
                            String image = change.getDocument().getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.orderID = change.getDocument().getId();
                            orderItems.add(orderItem);
                            if(orderItems.size()!=0){
                                noOrdersText.setVisibility(View.GONE);
                            }
                            orderListAdapterChef.notifyDataSetChanged();
                        }
                        break;
                    case MODIFIED:
                        String newStatus = change.getDocument().getString("status");
                        if (newStatus.equals("Preparing")) {
                            //remove from the list
                            String id = change.getDocument().getId();
                            for (OrderItem item : orderItems) {
                                if (item.orderID.equals(id)) {
                                    orderItems.remove(item);
                                    if (orderItems.size() == 0) {
                                        noOrdersText.setVisibility(View.VISIBLE);
                                    }
                                    orderListAdapterChef.notifyDataSetChanged();
                                    break;
                                }
                            }}
                }
            }
        }));
    }
}