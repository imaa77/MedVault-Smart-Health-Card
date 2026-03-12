package com.nextgen.medvault.Adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.nextgen.medvault.Models.MedReminder;
import com.nextgen.medvault.R;
import com.nextgen.medvault.Services.MedicineReminderReceiver;
import com.nextgen.medvault.Utils.GlobalPreference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MedReminderAdapter extends RecyclerView.Adapter<MedReminderAdapter.ViewHolder> {

    private ArrayList<MedReminder> reminders;
    private Context context;
    private String ip, uid;

    public MedReminderAdapter(ArrayList<MedReminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;

        GlobalPreference preference = new GlobalPreference(context);
        ip = preference.RetriveIP();
        uid = preference.getUID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_med_reminder, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MedReminder reminder = reminders.get(position);

        holder.medicineNameTV.setText(reminder.getMedicine());
        holder.dosageTV.setText(reminder.getDosage());
        holder.timeTV.setText("⏰ " + reminder.getTime());
        holder.dateRangeTV.setText("📅 " + reminder.getDays() + " days");

        // DELETE REMINDER
        holder.deleteReminderIV.setOnClickListener(v -> {

            cancelMedicineAlarms(
                    reminder.getMedicine(),
                    Integer.parseInt(reminder.getDays())
            );
            deleteReminderFromServer(reminder.getId(), position);

        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView medicineNameTV, dosageTV, timeTV, dateRangeTV;
        ImageView deleteReminderIV;
        CardView medRemCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            medicineNameTV = itemView.findViewById(R.id.medicineNameTV);
            dosageTV = itemView.findViewById(R.id.dosageTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            dateRangeTV = itemView.findViewById(R.id.dateRangeTV);
            deleteReminderIV = itemView.findViewById(R.id.deleteReminderIV);
            medRemCV = itemView.findViewById(R.id.medRemCV);
        }
    }

    // CANCEL LOCAL ALARMS
    private void cancelMedicineAlarms(String medicine, int days) {

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < days; i++) {

            Intent intent = new Intent(context, MedicineReminderReceiver.class);

            int requestCode = (uid + medicine + i).hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    // DELETE FROM SERVER
    private void deleteReminderFromServer(String id, int position) {

        String url = "http://" + ip + "/medvault/delete_med_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    reminders.remove(position);
                    notifyItemRemoved(position);

                    Toast.makeText(context, "Medicine deleted", Toast.LENGTH_SHORT).show();

                },
                error -> Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("id", id);

                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}