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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Services.MedicineReminderReceiver;
import com.nextgen.medvault.Utils.GlobalPreference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserAddMedicationReminderActivity extends AppCompatActivity {

    private EditText medicineNameET, dosageET, daysET;
    private TextView timeTV, startDateTV;

    private int hour, minute;
    private Calendar startDate;
    private String ip, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add_medication_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
        uid = preference.getUID();

        medicineNameET = findViewById(R.id.medicineNameET);
        dosageET = findViewById(R.id.dosageET);
        daysET = findViewById(R.id.daysET);
        timeTV = findViewById(R.id.timeTV);
        startDateTV = findViewById(R.id.startDateTV);

        timeTV.setOnClickListener(v -> openTimePicker());
        startDateTV.setOnClickListener(v -> openDatePicker());

        findViewById(R.id.saveReminderBtn)
                .setOnClickListener(v -> saveReminder());

        createNotificationChannel();
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();

        new TimePickerDialog(this,
                (view, h, m) -> {
                    hour = h;
                    minute = m;
                    timeTV.setText(String.format("%02d:%02d", h, m));
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        ).show();
    }

    private void openDatePicker() {
        Calendar now = Calendar.getInstance();

        new DatePickerDialog(this,
                (view, year, month, day) -> {
                    startDate = Calendar.getInstance();
                    startDate.set(year, month, day);
                    startDateTV.setText(day + "/" + (month + 1) + "/" + year);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveReminder() {

        String medicine = medicineNameET.getText().toString().trim();
        String dosage = dosageET.getText().toString().trim();
        String daysStr = daysET.getText().toString().trim();

        if (medicine.isEmpty() || dosage.isEmpty() || daysStr.isEmpty() || startDate == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int days = Integer.parseInt(daysStr);

        String time = String.format("%02d:%02d", hour, minute);

        String startDateStr = startDate.get(Calendar.YEAR) + "-" +
                (startDate.get(Calendar.MONTH) + 1) + "-" +
                startDate.get(Calendar.DAY_OF_MONTH);

        saveReminderToServer(
                medicine,
                dosage,
                time,
                startDateStr,
                days
        );

        scheduleMedicineNotifications(
                medicine,
                dosage,
                startDate,
                days
        );

        Toast.makeText(this, "Medicine reminder set", Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MED_REMINDER",
                    "Medicine Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void scheduleMedicineNotifications(
            String medicine,
            String dosage,
            Calendar startDate,
            int days
    ) {

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < days; i++) {

            Calendar alarmTime = (Calendar) startDate.clone();
            alarmTime.add(Calendar.DAY_OF_YEAR, i);
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);

            Intent intent = new Intent(this, MedicineReminderReceiver.class);
            intent.putExtra("medicine", medicine);
            intent.putExtra("dosage", dosage);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis() + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    // Graceful fallback (still works, may be slightly inexact)
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime.getTimeInMillis(),
                            pendingIntent
                    );
                }

            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }

    private void saveReminderToServer(
            String medicine,
            String dosage,
            String time,
            String startDate,
            int days
    ) {

        String url = "http://"+ip+"/medvault/save_medicine_reminder.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    Log.d("REMINDER_TAG", "saveReminderToServer: "+response);
                    // Server response
                    Toast.makeText(this, "Reminder saved to server", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("medicine", medicine);
                params.put("dosage", dosage);
                params.put("time", time);
                params.put("start_date", startDate);
                params.put("days", String.valueOf(days));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}