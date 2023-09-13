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
    RecyclerView recyclerView;
    OrderListAdapterWaiter orderListAdapterWaiter;
    ArrayList<OrderItem> orderItems;
    TextView noOrdersText;
    FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        noOrdersText = view.findViewById(R.id.noOrdersText);
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.orderRecyleView);
        orderItems = new ArrayList<>();
        orderListAdapterWaiter = new OrderListAdapterWaiter(orderItems);
        orderListAdapterWaiter.setOnOrderButtonClickListener(orderItem -> {
            Log.d("heyyou","order " +orderItem.orderID);
            if(orderItem.status.equals("Prepared")){
                orderItem.status = "Delivering";
                orderItems.add(0, orderItem);
                db.collection("orders").document(orderItem.orderID).update("status", "Delivering").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
            else{
                orderItem.status="Delivered";
                orderItems.remove(orderItem);
                if(orderItems.size()==0){
                    noOrdersText.setVisibility(View.VISIBLE);
                }
                orderListAdapterWaiter.notifyDataSetChanged();
                db.collection("orders").document(orderItem.orderID).update("status", "Delivered").addOnSuccessListener(documentReference -> {
                    Log.d("FirestoreData", "status successfully updated!");
                }).addOnFailureListener(e -> {
                    Log.d("FirestoreData", "Error updating status", e);
                });
            }
        });
        recyclerView.setAdapter(orderListAdapterWaiter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //fetching data from firestore
        db.collection("orders").whereEqualTo("status", "Ordered").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                String name = documentSnapshot.getString("name");
                Integer price = documentSnapshot.getLong("price").intValue();
                Integer quantity = documentSnapshot.getLong("quantity").intValue();
                Integer tableID = documentSnapshot.getLong("tableID").intValue();
                String notes = documentSnapshot.getString("notes");
                OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes);
                orderItem.status = "Prepared";
                orderItem.orderID = documentSnapshot.getId();
                orderItems.add(orderItem);
            }
            if(orderItems.size()==0){
                noOrdersText.setVisibility(View.VISIBLE);
            }
            orderListAdapterWaiter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.d("FirestoreData", "Error getting documents: ", e);
        });
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
                if(change.getType()== DocumentChange.Type.MODIFIED){
                    String newStatus= change.getDocument().getString("status");
                    if(newStatus.equals("Delivering")){
                        //remove from the list
                        String id = change.getDocument().getId();
                        for(OrderItem item: orderItems){
                            if(item.orderID.equals(id)){
                                orderItems.remove(item);
                                if(orderItems.size()==0){
                                    noOrdersText.setVisibility(View.VISIBLE);
                                }
                                orderListAdapterWaiter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    else if(newStatus.equals("Prepared")){
                        String name = change.getDocument().getString("name");
                        Integer price = change.getDocument().getLong("price").intValue();
                        Integer quantity = change.getDocument().getLong("quantity").intValue();
                        Integer tableID = change.getDocument().getLong("tableID").intValue();
                        String notes = change.getDocument().getString("notes");
                        OrderItem orderItem = new OrderItem(name, price, quantity, tableID, notes);
                        orderItem.status = "Prepared";
                        orderItem.orderID = change.getDocument().getId();
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