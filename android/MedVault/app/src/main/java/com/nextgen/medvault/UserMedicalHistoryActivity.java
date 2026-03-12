package com.nextgen.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.nextgen.medvault.Adapter.MedicalHistoriesAdapter;
import com.nextgen.medvault.Models.MedicalHistory;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserMedicalHistoryActivity extends AppCompatActivity {

    FloatingActionButton addNewFab;
    RecyclerView medicalHistoryRV;
    private String ip, uid;
    private ArrayList<MedicalHistory> histories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_medical_history);
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

        addNewFab.setOnClickListener(v -> {
            startActivity(new Intent(UserMedicalHistoryActivity.this, UserAddMedicalActivity.class));
        });
    }

    private void loadList() {
        histories.clear();
        histories = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/medvault/get_medical_history.php", s -> {

            String result = s.trim();
            Log.d("******", "onResponse: "+result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                Boolean status = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");

                histories.clear();
                histories = new ArrayList<>();

                if(status){
                    JSONArray jsonArray = jsonObject.getJSONArray("history");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String id = obj.getString("id");
                        String title = obj.getString("title");
                        String file_name = obj.getString("file_name");
                        String date_time = obj.getString("date_time");
                        String notes = obj.getString("notes");
                        String statuss = obj.getString("status");

                        histories.add(new MedicalHistory(id, title, file_name, date_time, notes, statuss));
                    }

                    MedicalHistoriesAdapter historiesAdapter = new MedicalHistoriesAdapter(histories, UserMedicalHistoryActivity.this);
                    medicalHistoryRV.setAdapter(historiesAdapter);

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

        addNewFab = findViewById(R.id.addNewFab);

        medicalHistoryRV = findViewById(R.id.medicalHistoryRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        medicalHistoryRV.setLayoutManager(layoutManager);
        medicalHistoryRV.setHasFixedSize(true);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadList();
    }

}