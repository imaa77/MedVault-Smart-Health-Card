package com.nextgen.medvault;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nextgen.medvault.Services.VolleyMultipartRequest;
import com.nextgen.medvault.Utils.GlobalPreference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UserAddMedicalActivity extends AppCompatActivity {

    TextView fileTextView, dateTimeTextView;
    EditText titleEditText, notesEditText;
    Button addButton;

    private Uri selectedFileUri;
    private String ip, uid;
    private String selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add_medical);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GlobalPreference preference = new GlobalPreference(this);
        ip = preference.RetriveIP();
        uid = preference.getUID();

        initViews();

        addButton.setOnClickListener(v -> {
            addMedicalHistory();
        });

        fileTextView.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{
                    "application/pdf",
                    "image/*"
            });
        });

        dateTimeTextView.setOnClickListener(v -> showDateTimePicker());
    }

    private void showDateTimePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    showTimePicker(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(String date) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String time = hourOfDay + ":" + minute1;
                    dateTimeTextView.setText(date + " " + time);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void addMedicalHistory() {

        String dateTime = dateTimeTextView.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Title required");
            return;
        }

        if (dateTime.equals("Select date and time") || dateTime.isEmpty()) {
            Toast.makeText(this, "Date & time required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Please select a PDF or image", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] fileBytes = readBytesFromUri(selectedFileUri);
        if (fileBytes == null) {
            Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show();
            return;
        }

        String mimeType = getContentResolver().getType(selectedFileUri);
        String fileName = selectedFileName;

        String url = "http://"+ip+"/medvault/add_medical_history.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(
                Request.Method.POST,
                url,
                response -> {
                    String result = new String(response.data);
                    Log.d("UPLOAD_SUCCESS", result);
                    Toast.makeText(this, "Medical record uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("notes", notes);
                params.put("date_time", dateTime);
                params.put("uid", uid);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> data = new HashMap<>();
                data.put("file", new DataPart(
                        fileName,
                        fileBytes,
                        mimeType
                ));
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private final ActivityResultLauncher<String[]> filePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {
                        if (uri != null) {
                            selectedFileUri = uri;
                            getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                            showSelectedFileName(uri);
                        }
                    }
            );

    private void showSelectedFileName(Uri uri) {
        String fileName = "Selected file";

        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }

        selectedFileName = fileName;
        fileTextView.setText(fileName);
    }

    private byte[] readBytesFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initViews() {
        fileTextView = findViewById(R.id.fileTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        titleEditText = findViewById(R.id.titleEditText);
        notesEditText = findViewById(R.id.notesEditText);
        addButton = findViewById(R.id.addButton);
    }
}