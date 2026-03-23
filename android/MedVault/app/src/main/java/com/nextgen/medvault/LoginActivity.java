package com.nextgen.medvault;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {



    private EditText loginPhoneET, loginPasswordET;
    private String ip;
    private GlobalPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        initViews();

        preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
    }


    public void registerUser(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void userLogin(View view) {

        String phone = loginPhoneET.getText().toString();
        String password = loginPasswordET.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ip+"/medvault/login.php", s -> {

            String result = s.trim();
            Log.d("******", "onResponse: "+result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                Boolean status = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
                if(status){
                    JSONObject obj = jsonObject.getJSONObject("user");
                    String id = obj.getString("id");
                    String name = obj.getString("name");

                    preference.setUID(id);
                    preference.setName(name);
                    preference.setUserDetails(obj.toString());

                    Log.d("USER_JSON", "userLogin: "+obj.toString());

                    startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));

                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, volleyError -> Log.d("******", "onErrorResponse: "+volleyError)){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("email", phone);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void initViews() {
        loginPhoneET = findViewById(R.id.uloginPhoneEditText);
        loginPasswordET = findViewById(R.id.uloginPasswordEditText);
    }
}