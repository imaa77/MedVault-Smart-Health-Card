package com.nextgen.medvault;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nextgen.medvault.Utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SmartCardActivity extends AppCompatActivity {

    private GlobalPreference preference;

    private ImageView qrImageView;
    private TextView nameTV, addressTV, dobTV, phoneTV, healthIdTV;
    private TextView emergencyNameTV, emergencyPhoneTV;
    private Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_card);

        preference = new GlobalPreference(this);

        qrImageView = findViewById(R.id.qrImageView);
        nameTV = findViewById(R.id.nameTV);
        addressTV = findViewById(R.id.addressTV);
        dobTV = findViewById(R.id.dobTV);
        phoneTV = findViewById(R.id.phoneTV);
        healthIdTV = findViewById(R.id.healthIdTV);
        emergencyNameTV = findViewById(R.id.emergencyNameTV);
        emergencyPhoneTV = findViewById(R.id.emergencyPhoneTV);
        downloadBtn = findViewById(R.id.downloadBtn);

        downloadBtn.setOnClickListener(v -> downloadCard());

        loadUserData();
    }

    // ⭐ This refreshes smart card when screen opens again
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {

        String ip = preference.RetriveIP();
        String uid = preference.getUID();

        String url = "http://" + ip + "/medvault/get_user_details.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {

            try {

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getBoolean("success")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("user");
                    JSONObject obj = jsonArray.getJSONObject(0);

                    String name = obj.getString("name");
                    String address = obj.getString("address");
                    String dob = obj.getString("dob");
                    String phone = obj.getString("phone");
                    String publicId = obj.getString("public_id");

                    String emergencyName = obj.optString("emergency_name");
                    String emergencyPhone = obj.optString("emergency_phone");

                    nameTV.setText("Name: " + name);
                    addressTV.setText("Address: " + address);
                    dobTV.setText("DOB: " + dob);
                    phoneTV.setText("Phone: " + phone);
                    healthIdTV.setText("Health ID: " + publicId);

                    emergencyNameTV.setText("Emergency: " + emergencyName);
                    emergencyPhoneTV.setText("Phone: " + emergencyPhone);

                    String qrData = "http://" + ip + "/medvault/public.php?id=" + publicId;

                    generateQR(qrData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, error -> Toast.makeText(this, "Failed to load smart card", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void generateQR(String data) {

        try {

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.encodeBitmap(
                    data,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

            qrImageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadCard() {

        try {

            View cardView = findViewById(R.id.cardLayout);

            Bitmap bitmap = Bitmap.createBitmap(
                    cardView.getWidth(),
                    cardView.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);
            cardView.draw(canvas);

            File file = new File(getExternalFilesDir(null), "SmartCard.png");

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

            Toast.makeText(
                    this,
                    "Smart Card saved:\n" + file.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Download Failed", Toast.LENGTH_SHORT).show();
        }
    }
}