package com.example.hotel_management.waiter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.OrderItem;
import com.example.hotel_management.recyledview.OrderListAdapterWaiter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class OrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderListAdapterWaiter orderListAdapterWaiter;
    private ArrayList<OrderItem> orderItems;
    private TextView noOrdersText;
    private FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        noOrdersText = view.findViewById(R.id.noOrdersText);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.waiterOrderRecyleView);
        orderItems = new ArrayList<>();
        orderListAdapterWaiter = new OrderListAdapterWaiter(orderItems);
        orderListAdapterWaiter.setOnOrderButtonClickListener(orderItem -> {
            if(orderItem.getStatus().equals("Prepared")){
                int index = orderItems.indexOf(orderItem);
                db.collection("orders").document(orderItem.getOrderID()).update("status", "Delivering").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                    orderItem.setStatus("Delivering");
                    orderItems.add(0, orderItem);
                    orderListAdapterWaiter.notifyItemMoved(index, 0);
                    orderListAdapterWaiter.notifyItemChanged(0);
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
            else{
                orderItem.setStatus("Delivered");
                int index = orderItems.indexOf(orderItem);
                orderItems.remove(orderItem);
                if(orderItems.size()==0){
                    noOrdersText.setVisibility(View.VISIBLE);
                }
                orderListAdapterWaiter.notifyItemRemoved(index);
                db.collection("orders").document(orderItem.getOrderID()).update("status", "Delivered").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
        });
        recyclerView.setAdapter(orderListAdapterWaiter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

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
                        if(change.getDocument().getString("status").equals("Prepared")){
                            Log.d("FirestoreData", "New order: " + change.getDocument().getData());
                            String name = change.getDocument().getString("name");
                            Integer price = change.getDocument().getLong("price").intValue();
                            Integer quantity = change.getDocument().getLong("quantity").intValue();
                            Integer tableID = change.getDocument().getLong("tableID").intValue();
                            String notes = change.getDocument().getString("notes");
                            String image = change.getDocument().getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.setStatus("Prepared");
                            orderItem.setOrderID(change.getDocument().getId());
                            orderItems.add(orderItem);
                            if(orderItems.size()!=0){
                                noOrdersText.setVisibility(View.GONE);
                            }
                            else {
                                noOrdersText.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case MODIFIED:
                        String newStatus = change.getDocument().getString("status");
                        if (newStatus.equals("Delivering")) {
                            //remove from the list because the a waiter has taken this order
                            String id = change.getDocument().getId();
                            for (OrderItem item : orderItems) {
                                if (item.getOrderID().equals(id)) {
                                    orderItems.remove(item);
                                    if (orderItems.size() == 0) {
                                        noOrdersText.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                }
                            }}
                        else if(newStatus.equals("Prepared")){
                            String name = change.getDocument().getString("name");
                            Integer price = change.getDocument().getLong("price").intValue();
                            Integer quantity = change.getDocument().getLong("quantity").intValue();
                            Integer tableID = change.getDocument().getLong("tableID").intValue();
                            String notes = change.getDocument().getString("notes");
                            String image = change.getDocument().getString("url");
                            OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes, image);
                            orderItem.setStatus("Prepared");
                            orderItem.setOrderID(change.getDocument().getId());
                            orderItems.add(orderItem);
                            noOrdersText.setVisibility(View.GONE);
                            orderListAdapterWaiter.notifyDataSetChanged();
                        }
                }
            }
        }));

        return view;
    }
}