package com.example.hotel_management.recyledview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.R;
import com.example.hotel_management.datatypes.Booking;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class TableBookingAdapter extends RecyclerView.Adapter<TableBookingAdapter.ViewHolder>{
    private ArrayList<Booking> bookings;
    private FirebaseFirestore db;
    private Query query;
    private Integer selected;

    public interface OnTableBookingClickListener {
        void OnTableBookingClick(Booking booking);
    }
    private OnTableBookingClickListener onTableBookingClickListener;

    public TableBookingAdapter(Integer tableID) {
        selected = 0;
        db = FirebaseFirestore.getInstance();
        bookings = new ArrayList<>();
        // Define the time constraint
        Date now = new Date(); // Current date and time
        Date dayAfter = new Date(now.getTime() + (1000 * 60 * 60 * 24));
        query = db.collection("tableBookings")
                .whereEqualTo("tableID", tableID)
                .whereGreaterThanOrEqualTo("start", now)
                .whereLessThanOrEqualTo("start", dayAfter);
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("FirestoreData", "tableBookings listen failed.", error);
                return;
            }
            for (DocumentChange change : value.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        // Handle added documents here
                        Log.d("FirestoreData", "Booking " + change.getDocument().getData());
                        Booking booking = new Booking(
                                change.getDocument().getString("name"),
                                change.getDocument().getLong("tableID").intValue(),
                                change.getDocument().getString("key"),
                                change.getDocument().getTimestamp("start"),
                                change.getDocument().getTimestamp("end"),
                                change.getDocument().getId()
                        );
                        bookings.add(booking);
                        bookings.sort(Comparator.comparing(Booking::getStartTime));
                        break;

                    case MODIFIED:
                        // Handle modified documents here
                        Log.d("FirestoreData", "Modified booking: ");
                        break;

                    case REMOVED:
                        bookings.removeIf(b -> b.getBookingID().equals(change.getDocument().getId()));
                        selected = 0;
                        break;
                }
            }
            if(this.getItemCount()==0){
                onTableBookingClickListener.OnTableBookingClick(null);
            }
            else {
                onTableBookingClickListener.OnTableBookingClick(bookings.get(selected));
            }
            this.notifyDataSetChanged();
        });
    }
    @NonNull
    @Override
    public TableBookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableBookingAdapter.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        // Format the start time and end time as strings
        String startTimeString = dateFormat.format(booking.getStartTime().toDate());
        String endTimeString = dateFormat.format(booking.getEndTime().toDate());

        holder.bookedBy.setText(booking.getName());
        holder.bookedFrom.setText(startTimeString);
        holder.bookedTo.setText(endTimeString);
        holder.itemView.setOnClickListener(v -> {
            selected = holder.getAdapterPosition();
            notifyDataSetChanged();
            onTableBookingClickListener.OnTableBookingClick(booking);
        });
        if(position==selected){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner_outer));
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
    public void setOnTableBookingClickListener(OnTableBookingClickListener onTableBookingClickListener) {
        this.onTableBookingClickListener = onTableBookingClickListener;
    }
    public void deleteCurrentBooking(){
        Booking booking = bookings.get(selected);
        db.collection("tableBookings").document(booking.getBookingID()).delete();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView bookedBy;
        TextView bookedFrom;
        TextView bookedTo;
        public ViewHolder(View view) {
            super(view);
            itemView = view;
            bookedBy = view.findViewById(R.id.bookedBy);
            bookedFrom = view.findViewById(R.id.fromTime);
            bookedTo = view.findViewById(R.id.toTime);
        }
    }
}
