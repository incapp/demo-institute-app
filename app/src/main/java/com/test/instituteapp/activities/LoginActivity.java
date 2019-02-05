package com.test.instituteapp.activities;

import android.content.Context;
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

public class LoginActivity extends AppCompatActivity {

    EditText editTextMobile, editTextPassword;
    Button buttonLogin, buttonRegisterHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextMobile = findViewById(R.id.editText_mobile);
        editTextPassword = findViewById(R.id.editText_password);
        buttonRegisterHere = findViewById(R.id.button_register_here);
        buttonLogin = findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextMobile.getText().toString().isEmpty()) {
                    editTextMobile.setError("Required!");
                    editTextMobile.requestFocus();
                } else if (editTextPassword.getText().toString().isEmpty()) {
                    editTextPassword.setError("Required!");
                    editTextPassword.requestFocus();
                } else {
                    Utils.hideKeyboard(LoginActivity.this);
                    JSONObject userObject = new JSONObject();

                    try {
                        userObject.put("phone", editTextMobile.getText().toString());
                        userObject.put("password", editTextPassword.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Utils.showProgressDialog(LoginActivity.this, "Please Wait!!!", "Logging in...");

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            Constants.API_LOGIN,
                            userObject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Utils.dismissDialog();

                                    LoginRegisterResponse apiResponse = new Gson()
                                            .fromJson(response.toString(), LoginRegisterResponse.class);

                                    if (apiResponse.isSuccess()) {
                                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                        getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                                                .edit()
                                                .putString(Constants.SH_PREF_KEY_MOBILE_NO, editTextMobile.getText().toString())
                                                .putString(Constants.SH_PREF_KEY_NAME, apiResponse.getName())
                                                .putString(Constants.SH_PREF_KEY_TYPE, apiResponse.getType() != null ? apiResponse.getType() : "U")
                                                .apply();
                                    } else {
                                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Utils.dismissDialog();
                                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);
                }
            }
        });

        buttonRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
}
