package com.nextgen.medvault;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText nameET, dobET, bloodET, allergiesET,
            addressET, emailET, phoneET, passwordET,
            emergencyNameET, emergencyPhoneET, emergencyEmailET;

    Button registerButton;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();

        initViews();

        registerButton.setOnClickListener(v -> submitData());
    }

    private void initViews() {

        nameET = findViewById(R.id.nameEditText);
        dobET = findViewById(R.id.dobEditText);   // ✅ FIXED
        bloodET = findViewById(R.id.bloodEditText);
        allergiesET = findViewById(R.id.allergiesEditText);
        addressET = findViewById(R.id.addressEditText);
        emailET = findViewById(R.id.emailEditText);
        phoneET = findViewById(R.id.phoneEditText);
        passwordET = findViewById(R.id.passwordEditText);

        emergencyNameET = findViewById(R.id.emergencyNameEditText);
        emergencyPhoneET = findViewById(R.id.emergencyPhoneEditText);
        emergencyEmailET = findViewById(R.id.emergencyEmailEditText);

        registerButton = findViewById(R.id.registerButton);
    }

    private void submitData() {

        String url = "http://" + ip + "/medvault/user_signup.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        Toast.makeText(this,
                                obj.getString("message"),
                                Toast.LENGTH_LONG).show();

                        if (obj.getBoolean("success"))
                            finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Response Error",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this,
                            "Server Error",
                            Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put("name", nameET.getText().toString());
                params.put("dob", dobET.getText().toString());   // ✅ FIXED
                params.put("blood", bloodET.getText().toString());
                params.put("allergies", allergiesET.getText().toString());
                params.put("address", addressET.getText().toString());
                params.put("email", emailET.getText().toString());
                params.put("phone", phoneET.getText().toString());
                params.put("password", passwordET.getText().toString());

                params.put("emergency_name", emergencyNameET.getText().toString());
                params.put("emergency_phone", emergencyPhoneET.getText().toString());
                params.put("emergency_email", emergencyEmailET.getText().toString());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}