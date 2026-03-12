package com.nextgen.medvault;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {


    private EditText nameTV,dobTV,bloodTV,allergiesTV,addressTV,emailTV,phoneTV,
            emergencyNameTV,emergencyPhoneTV,emergencyEmailTV;

    private ImageView editProfileBtn;
    private Button saveProfileBtn;

    private String ip,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
        uid = preference.getUID();

        initViews();
        loadData();

        editProfileBtn.setOnClickListener(v -> enableEditing(true));

        saveProfileBtn.setOnClickListener(v -> updateProfile());
    }

    private void initViews(){

        nameTV=findViewById(R.id.nameTextView);
        dobTV=findViewById(R.id.dobTextView);
        bloodTV=findViewById(R.id.bloodTextView);
        allergiesTV=findViewById(R.id.allergiesTextView);
        addressTV=findViewById(R.id.addressTextView);
        emailTV=findViewById(R.id.emailTextView);
        phoneTV=findViewById(R.id.phoneTextView);

        emergencyNameTV=findViewById(R.id.emergencyNameTextView);
        emergencyPhoneTV=findViewById(R.id.emergencyPhoneTextView);
        emergencyEmailTV=findViewById(R.id.emergencyEmailTextView);

        editProfileBtn=findViewById(R.id.editProfileBtn);
        saveProfileBtn=findViewById(R.id.saveProfileBtn);
    }

    private void enableEditing(boolean status){

        nameTV.setEnabled(status);
        dobTV.setEnabled(status);
        bloodTV.setEnabled(status);
        allergiesTV.setEnabled(status);
        addressTV.setEnabled(status);
        emailTV.setEnabled(status);
        phoneTV.setEnabled(status);

        emergencyNameTV.setEnabled(status);
        emergencyPhoneTV.setEnabled(status);
        emergencyEmailTV.setEnabled(status);

        saveProfileBtn.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    private void loadData(){

        String url="http://"+ip+"/medvault/get_user_details.php";

        StringRequest request=new StringRequest(Request.Method.POST,url,response -> {

            try{

                JSONObject json=new JSONObject(response);

                if(json.getBoolean("success")){

                    JSONArray arr=json.getJSONArray("user");
                    JSONObject obj=arr.getJSONObject(0);

                    nameTV.setText(obj.getString("name"));
                    dobTV.setText(obj.getString("dob"));
                    bloodTV.setText(obj.getString("blood_group"));
                    allergiesTV.setText(obj.getString("allergies"));
                    addressTV.setText(obj.getString("address"));
                    emailTV.setText(obj.getString("email"));
                    phoneTV.setText(obj.getString("phone"));

                    emergencyNameTV.setText(obj.getString("emergency_name"));
                    emergencyPhoneTV.setText(obj.getString("emergency_phone"));
                    emergencyEmailTV.setText(obj.getString("emergency_email"));

                }

            }catch(Exception e){
                Log.d("PROFILE",e.toString());
            }

        },error -> Log.d("PROFILE",error.toString())){

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params=new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void updateProfile(){

        String url="http://"+ip+"/medvault/update_user.php";

        StringRequest request=new StringRequest(Request.Method.POST,url,response -> {

            Log.d("UPDATE_RES", "Response: " + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("success")) {

                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    enableEditing(false);

                    loadData();   // ⭐ reload updated data from database

                } else {
                    Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("UPDATE_ERROR", "JSON Parse error: " + e.getMessage());
                Toast.makeText(this, "Response Error", Toast.LENGTH_SHORT).show();
            }

        },error -> {
            String errorMsg = error.toString();
            if (error.networkResponse != null) {
                errorMsg += " Status Code: " + error.networkResponse.statusCode;
            }
            Log.e("UPDATE_ERR", errorMsg);
            Toast.makeText(this, "Update Failed: " + errorMsg, Toast.LENGTH_LONG).show();
        }){

            @Override
            protected Map<String,String> getParams(){

                Map<String,String> params=new HashMap<>();

                params.put("uid",uid);
                params.put("name",nameTV.getText().toString());
                params.put("dob",dobTV.getText().toString());
                params.put("blood_group",bloodTV.getText().toString());
                params.put("allergies",allergiesTV.getText().toString());
                params.put("address",addressTV.getText().toString());
                params.put("email",emailTV.getText().toString());
                params.put("phone",phoneTV.getText().toString());

                params.put("emergency_name",emergencyNameTV.getText().toString());
                params.put("emergency_phone",emergencyPhoneTV.getText().toString());
                params.put("emergency_email",emergencyEmailTV.getText().toString());

                return params;
            }
        };

        // Increase timeout to 10 seconds
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

}
