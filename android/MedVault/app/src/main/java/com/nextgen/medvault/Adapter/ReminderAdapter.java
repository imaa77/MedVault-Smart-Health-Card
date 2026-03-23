package com.nextgen.medvault.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Models.Reminder;
import com.nextgen.medvault.R;
import com.nextgen.medvault.Utils.GlobalPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private ArrayList<Reminder> reminders;
    private Context context;
    private String ip, uid;

    public ReminderAdapter(ArrayList<Reminder> reminders, Context context) {

        this.reminders = reminders;
        this.context = context;

        GlobalPreference preference = new GlobalPreference(context);
        ip = preference.RetriveIP();
        uid = preference.getUID();
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_med_reminder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderAdapter.ViewHolder holder, int position) {

        Reminder reminder = reminders.get(position);

        holder.medicineNameTV.setText(reminder.getTitle());
        holder.dosageTV.setText(reminder.getDesc());
        holder.timeTV.setText("⏰ " + reminder.getTime());
        holder.dateRangeTV.setText("📅 " + reminder.getDays() + " days");

        if(reminder.getStatus().equals("completed")){
            holder.statusTV.setText("✔ Completed");
        }else{
            holder.statusTV.setText("Pending");
        }

        holder.deleteReminderIV.setOnClickListener(v -> showDeleteDialog(reminder, position));

        holder.completeReminderIV.setOnClickListener(v -> markCompleted(reminder, position));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicineNameTV, dosageTV, timeTV, dateRangeTV, statusTV;
        ImageView deleteReminderIV, completeReminderIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineNameTV = itemView.findViewById(R.id.medicineNameTV);
            dosageTV = itemView.findViewById(R.id.dosageTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            dateRangeTV = itemView.findViewById(R.id.dateRangeTV);
            statusTV = itemView.findViewById(R.id.statusTV);

            deleteReminderIV = itemView.findViewById(R.id.deleteReminderIV);
            completeReminderIV = itemView.findViewById(R.id.completeReminderIV);
        }
    }

    /* ---------------- DELETE REMINDER ---------------- */

    private void showDeleteDialog(Reminder reminder, int position){

        new AlertDialog.Builder(context)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReminder(reminder.getId(), position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReminder(String id, int position){

        String url = "http://" + ip + "/medvault/delete_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    reminders.remove(position);
                    notifyItemRemoved(position);

                },
                error -> Log.d("DELETE_REMINDER", error.toString())
        ){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> params = new HashMap<>();
                params.put("id", id);

                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    /* ---------------- MARK COMPLETED ---------------- */

    private void markCompleted(Reminder reminder, int position){

        String url = "http://" + ip + "/medvault/complete_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    reminder.setStatus("completed");
                    notifyItemChanged(position);

                },
                error -> Log.d("COMPLETE_REMINDER", error.toString())
        ){
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> params = new HashMap<>();
                params.put("id", reminder.getId());

                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}