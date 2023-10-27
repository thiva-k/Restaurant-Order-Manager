package com.example.hotel_management.recyledview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView; // Don't forget this import
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotel_management.datatypes.Employee;
import com.example.hotel_management.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;

    public interface OnEmployeeDeleteListener {
        void onEmployeeDelete(Employee employee);
    }

    private OnEmployeeDeleteListener onEmployeeDeleteListener;

    public EmployeeAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.nameTextView.setText(employee.getName());
        holder.emailTextView.setText(employee.getEmail());
        holder.userTypeTextView.setText(employee.getUserType());
        holder.deleteEmployeeButton.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(view.getContext());
            builder.setTitle("Delete Employee");
            builder.setBackground(view.getContext().getDrawable(R.drawable.rounded_corners));
            builder.setMessage("Are you sure you want to delete this employee?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                employeeList.remove(position);
                notifyItemRemoved(position);
                dialog.dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        });
    }

    public void setOnEmployeeDeleteListener(OnEmployeeDeleteListener onEmployeeDeleteListener) {
        this.onEmployeeDeleteListener = onEmployeeDeleteListener;
    }
    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView emailTextView;
        private TextView userTypeTextView;
        private Button deleteEmployeeButton;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            userTypeTextView = itemView.findViewById(R.id.userTypeTextView);
            deleteEmployeeButton = itemView.findViewById(R.id.deleteEmployeeButton);
        }

    }
}
