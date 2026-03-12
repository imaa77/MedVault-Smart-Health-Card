package com.nextgen.medvault;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nextgen.medvault.Adapter.ReminderAdapter;
import com.nextgen.medvault.Models.Reminder;
import com.nextgen.medvault.Services.ReminderReceiver;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserAddReminderActivity extends AppCompatActivity {

    private int hour, minute;
    private Calendar startDate;

    private String ip, uid;

    private FloatingActionButton addReminderFAB;
    private RecyclerView reminderRV;

    private ArrayList<Reminder> reminders;
    private ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add_reminder);

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
        uid = preference.getUID();

        initViews();
        createNotificationChannel();

        addReminderFAB.setOnClickListener(v -> showDialog());

        loadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    /* ---------------- INIT ---------------- */

    private void initViews() {

        addReminderFAB = findViewById(R.id.addReminderFAB);

        reminderRV = findViewById(R.id.reminderRV);
        reminderRV.setLayoutManager(new LinearLayoutManager(this));

        reminders = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminders, this);

        reminderRV.setAdapter(reminderAdapter);
    }

    /* ---------------- LOAD REMINDERS ---------------- */

    private void loadList() {

        String url = "http://" + ip + "/medvault/get_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        reminders.clear();

                        if (success) {

                            JSONArray jsonArray = jsonObject.getJSONArray("reminders");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                reminders.add(new Reminder(
                                        obj.getString("id"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("time"),
                                        obj.getString("start_date"),
                                        obj.getString("days"),
                                        obj.getString("created_at"),
                                        obj.optString("status", "pending")
                                ));
                            }

                            reminderAdapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.d("REMINDER_ERROR", error.toString())
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /* ---------------- ADD REMINDER DIALOG ---------------- */

    private void showDialog() {

        android.view.View view = getLayoutInflater()
                .inflate(R.layout.raw_dialog_add_reminder, null);

        TextView titleET = view.findViewById(R.id.titleET);
        TextView descpET = view.findViewById(R.id.descpET);
        TextView daysET = view.findViewById(R.id.daysET);
        TextView timeTV = view.findViewById(R.id.timeTV);
        TextView startDateTV = view.findViewById(R.id.startDateTV);

        timeTV.setOnClickListener(v -> openTimePicker(timeTV));
        startDateTV.setOnClickListener(v -> openDatePicker(startDateTV));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        view.findViewById(R.id.cancelBtn).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.cancelIV).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.saveReminderBtn)
                .setOnClickListener(v -> saveReminder(
                        titleET.getText().toString(),
                        descpET.getText().toString(),
                        daysET.getText().toString(),
                        dialog
                ));

        dialog.show();
    }

    /* ---------------- TIME PICKER ---------------- */

    private void openTimePicker(TextView timeTV) {

        Calendar now = Calendar.getInstance();

        new TimePickerDialog(this, (view, h, m) -> {

            hour = h;
            minute = m;

            timeTV.setText(String.format("%02d:%02d", h, m));

        }, now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true).show();
    }

    /* ---------------- DATE PICKER ---------------- */

    private void openDatePicker(TextView startDateTV) {

        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this, (view, year, month, day) -> {

            startDate = Calendar.getInstance();
            startDate.set(year, month, day);

            startDateTV.setText(day + "/" + (month + 1) + "/" + year);

        }, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }

    /* ---------------- SAVE REMINDER ---------------- */

    private void saveReminder(String title, String descp, String totalDays, AlertDialog dialog) {

        if (title.isEmpty() || descp.isEmpty() || totalDays.isEmpty() || startDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int days = Integer.parseInt(totalDays);

        String time = String.format("%02d:%02d", hour, minute);

        String startDateStr = startDate.get(Calendar.YEAR) + "-"
                + (startDate.get(Calendar.MONTH) + 1) + "-"
                + startDate.get(Calendar.DAY_OF_MONTH);

        saveReminderToServer(title, descp, time, startDateStr, days, dialog);

        scheduleNotifications(title, descp, startDate, days);

        Toast.makeText(this, "Reminder scheduled", Toast.LENGTH_LONG).show();
    }

    /* ---------------- NOTIFICATION CHANNEL ---------------- */

    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "REMINDER",
                    "Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    /* ---------------- SCHEDULE NOTIFICATIONS ---------------- */

    private void scheduleNotifications(String title, String descp, Calendar startDate, int days) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < days; i++) {

            Calendar alarmTime = (Calendar) startDate.clone();
            alarmTime.add(Calendar.DAY_OF_YEAR, i);

            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("descp", descp);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis() + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime.getTimeInMillis(),
                            pendingIntent
                    );

                } else alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }

    /* ---------------- SAVE TO SERVER ---------------- */

    private void saveReminderToServer(String title, String descp, String time,
                                      String startDate, int days, AlertDialog dialog) {

        String url = "http://" + ip + "/medvault/save_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    Log.d("REMINDER_SERVER", response);

                    dialog.dismiss();

                    Toast.makeText(this, "Reminder saved", Toast.LENGTH_SHORT).show();

                    loadList();
                },
                error -> {

                    error.printStackTrace();

                    dialog.dismiss();

                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("uid", uid);
                params.put("title", title);
                params.put("descp", descp);
                params.put("time", time);
                params.put("start_date", startDate);
                params.put("days", String.valueOf(days));

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
