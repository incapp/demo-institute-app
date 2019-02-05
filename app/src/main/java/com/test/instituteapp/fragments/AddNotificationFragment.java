package com.test.instituteapp.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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

import org.json.JSONException;
import org.json.JSONObject;

public class AddNotificationFragment extends DialogFragment {

    EditText editTextTitle, editTextMessage;
    Button buttonAdd, buttonCancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_dialog_add_notification, null);

        editTextTitle = view.findViewById(R.id.editText_title);
        editTextMessage = view.findViewById(R.id.editText_message);
        buttonAdd = view.findViewById(R.id.button_add);
        buttonCancel = view.findViewById(R.id.button_cancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextTitle.getText().toString().isEmpty()) {
                    editTextTitle.setError("Required!");
                    editTextTitle.requestFocus();
                } else if (editTextMessage.getText().toString().isEmpty()) {
                    editTextMessage.setError("Required!");
                    editTextMessage.requestFocus();
                } else {
                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("title",
                                editTextTitle.getText().toString());
                        jsonObject.put("message",
                                editTextMessage.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    buttonAdd.setEnabled(false);
                    JsonObjectRequest request =
                            new JsonObjectRequest(
                                    Request.Method.POST,
                                    Constants.API_ADD_NOTIFICATION,
                                    jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            LoginRegisterResponse apiResponse =
                                                    new Gson().fromJson(response.toString(),
                                                            LoginRegisterResponse.class);
                                            Toast.makeText(getActivity(),
                                                    apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (apiResponse.isSuccess()) {
                                                dismiss();
                                            } else {
                                                buttonAdd.setEnabled(true);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(),
                                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                    Volley.newRequestQueue(getActivity())
                            .add(request);
                }
            }
        });

        AlertDialog alertDialog = builder.setView(view).create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        return alertDialog;
    }
}
