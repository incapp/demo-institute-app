package com.test.instituteapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.test.instituteapp.R;
import com.test.instituteapp.models.LoginRegisterResponse;
import com.test.instituteapp.utils.Constants;
import com.test.instituteapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextMobile, editTextPassword, editTextConfirmPassword;
    Button buttonRegister, buttonLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editText_name);
        editTextEmail = findViewById(R.id.editText_email);
        editTextMobile = findViewById(R.id.editText_mobile);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        buttonLoginHere = findViewById(R.id.button_login_here);
        buttonRegister = findViewById(R.id.button_register);

        buttonLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        if (editTextName.getText().toString().isEmpty()) {
            editTextName.setError("Required!");
            editTextName.requestFocus();
        } else if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Required!");
            editTextEmail.requestFocus();
        } else if (editTextMobile.getText().toString().isEmpty()) {
            editTextMobile.setError("Required!");
            editTextMobile.requestFocus();
        } else if (editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("Required!");
            editTextPassword.requestFocus();
        } else if (editTextConfirmPassword.getText().toString().isEmpty()) {
            editTextConfirmPassword.setError("Required!");
            editTextConfirmPassword.requestFocus();
        } else if (!editTextConfirmPassword.getText().toString().equals(editTextPassword.getText().toString())) {
            editTextConfirmPassword.setError("Password do not match!");
            editTextConfirmPassword.requestFocus();
        } else {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String mobile = editTextMobile.getText().toString();
            String password = editTextPassword.getText().toString();

            Utils.hideKeyboard(RegisterActivity.this);

            JSONObject userObject = new JSONObject();
            try {
                userObject.put("name", name);
                userObject.put("email", email);
                userObject.put("type", "U");
                userObject.put("phone", mobile);
                userObject.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Utils.showProgressDialog(RegisterActivity.this, "Please Wait!!!", "Registering...");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.API_REGISTER, userObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LoginRegisterResponse apiResponse = new Gson().fromJson(response.toString(), LoginRegisterResponse.class);

                            if (apiResponse.isSuccess()) {
                                Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            Volley.newRequestQueue(RegisterActivity.this).add(jsonObjectRequest);
        }
    }
}
