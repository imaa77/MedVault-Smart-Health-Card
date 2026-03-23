package com.nextgen.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nextgen.medvault.Adapter.MedReminderAdapter;
import com.nextgen.medvault.Adapter.MedicalHistoriesAdapter;
import com.nextgen.medvault.Models.MedReminder;
import com.nextgen.medvault.Models.MedicalHistory;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserMedRemindersActivity extends AppCompatActivity {

    private RecyclerView medicationListRV;
    private FloatingActionButton addMedReminderFAB;
    private String ip, uid;
    private ArrayList<MedReminder> medReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_med_reminders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
        uid = preference.getUID();

        initViews();

        loadList();

        addMedReminderFAB.setOnClickListener(v -> {
            startActivity(new Intent(UserMedRemindersActivity.this, UserAddMedicationReminderActivity.class));
        });
    }

    private void loadList() {

        medReminders = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/medvault/get_med_reminder.php", s -> {

            String result = s.trim();
            Log.d("******", "onResponse: "+result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                Boolean status = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");

                medReminders.clear();
                medReminders = new ArrayList<>();

                if(status){
                    JSONArray jsonArray = jsonObject.getJSONArray("reminders");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String id = obj.getString("id");
                        String medicine = obj.getString("medicine");
                        String dosage = obj.getString("dosage");
                        String time = obj.getString("time");
                        String start_date = obj.getString("start_date");
                        String days = obj.getString("days");
                        String created_at = obj.getString("created_at");

                        medReminders.add(new MedReminder(id, medicine, dosage, time, start_date, days, created_at));
                    }

                    MedReminderAdapter medReminderAdapter = new MedReminderAdapter(medReminders, UserMedRemindersActivity.this);
                    medicationListRV.setAdapter(medReminderAdapter);

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, volleyError -> Log.d("******", "onErrorResponse: "+volleyError)){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


    }

    private void initViews() {

        medicationListRV = findViewById(R.id.medicationListRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        medicationListRV.setLayoutManager(layoutManager);

        addMedReminderFAB = findViewById(R.id.addMedReminderFAB);

    }
}