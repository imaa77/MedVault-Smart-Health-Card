package com.nextgen.medvault;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nextgen.medvault.Utils.GlobalPreference;

public class UserHomeActivity extends AppCompatActivity {

    CardView smartCardCV, medicalHistoryCV, reminderCV, medicationCV, profileCV;
    private GlobalPreference preference;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preference = new GlobalPreference(this);

        requestNotificationPermission();
        initViews();

        // ✅ FIXED: Open SmartCardActivity instead of QR dialog
        smartCardCV.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, SmartCardActivity.class)));

        medicalHistoryCV.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserMedicalHistoryActivity.class)));

        medicationCV.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserMedRemindersActivity.class)));

        reminderCV.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserAddReminderActivity.class)));

        profileCV.setOnClickListener(v ->
                startActivity(new Intent(UserHomeActivity.this, UserProfileActivity.class)));
    }

    private void initViews() {
        smartCardCV = findViewById(R.id.smartCardCV);
        medicalHistoryCV = findViewById(R.id.medicalHistoryCV);
        reminderCV = findViewById(R.id.reminderCV);
        medicationCV = findViewById(R.id.medicationCV);
        profileCV = findViewById(R.id.profileCV);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }
}