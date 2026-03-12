package com.nextgen.medvault;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText username_et, password_et;
    Button login_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username_et = findViewById(R.id.username_editText);
        password_et = findViewById(R.id.password_editText);
        login_bt = findViewById(R.id.login_button);


        login_bt.setOnClickListener(v -> {

            String username = username_et.getText().toString();
            String password = password_et.getText().toString();

            Toast.makeText(this, username +" " + password, Toast.LENGTH_SHORT).show();

        });


    }
}