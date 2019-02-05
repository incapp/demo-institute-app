package com.test.instituteapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.test.instituteapp.R;
import com.test.instituteapp.utils.Constants;

public class LandingActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonRegister;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (hasAllPermissions()) {
            moveForward();
        } else {
            ActivityCompat.requestPermissions(LandingActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (hasAllPermissions()) {
                moveForward();
            } else {
                Toast.makeText(LandingActivity.this, "All permissions are required.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(LandingActivity.this, "Some error occurred. Try Again.", Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void moveForward() {
        String name = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                .getString(Constants.SH_PREF_KEY_MOBILE_NO, "");

        assert name != null;
        if (!name.isEmpty()) {
            Intent intent = new Intent(LandingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean hasAllPermissions() {
        boolean hasAllPermissions = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(LandingActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false;
                break;
            }
        }
        return hasAllPermissions;
    }
}
