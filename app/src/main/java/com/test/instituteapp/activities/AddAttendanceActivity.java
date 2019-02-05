package com.test.instituteapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.test.instituteapp.R;
import com.test.instituteapp.models.AttendanceModel;
import com.test.instituteapp.utils.AttendanceListener;
import com.test.instituteapp.utils.Constants;
import com.test.instituteapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddAttendanceActivity extends AppCompatActivity implements AttendanceListener {

    ListView listView;
    Button buttonMark;
    List<AttendanceModel> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attendance);

        listView = findViewById(R.id.listView);
        buttonMark = findViewById(R.id.button_mark);

        Utils.showProgressDialog(AddAttendanceActivity.this, "Please Wait!!!", "Loading...");

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://www.mocky.io/v2/5c0b76512f0000790013ebf6"/*Get All User*/,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Utils.dismissDialog();

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("attendanceList", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GetAttendanceResponse apiResponse = new Gson().fromJson(jsonObject.toString(), GetAttendanceResponse.class);

                        if (apiResponse.getAttendanceList() != null
                                && apiResponse.getAttendanceList().size() != 0) {

                            for (int i = 0; i < apiResponse.getAttendanceList().size(); i++) {
                                apiResponse.getAttendanceList().get(i).setValue("A");
                            }

                            list.addAll(apiResponse.getAttendanceList());
                            listView.setAdapter(new AttendanceListAdapter(AddAttendanceActivity.this, list, AddAttendanceActivity.this));
                        } else {
                            Toast.makeText(AddAttendanceActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                            list.clear();
                            listView.setAdapter(new AttendanceListAdapter(AddAttendanceActivity.this, list, AddAttendanceActivity.this));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissDialog();
                        Toast.makeText(AddAttendanceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(AddAttendanceActivity.this).add(jsonObjectRequest);

        buttonMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showProgressDialog(AddAttendanceActivity.this, "Please Wait!!!", "Loading...");

                GetAttendanceResponse getAttendanceResponse = new GetAttendanceResponse();

                getAttendanceResponse.setAttendanceList(list);

                JSONArray request = new JSONArray();

                try {
                    request = new JSONArray(new Gson().toJson(getAttendanceResponse.getAttendanceList()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                        Request.Method.POST,
                        Constants.API_ADD_ATTENDANCE,
                        request,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Utils.dismissDialog();
                                finish();
                                Toast.makeText(AddAttendanceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Utils.dismissDialog();
                                finish();
                                Toast.makeText(AddAttendanceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                            }
                        });
                Volley.newRequestQueue(AddAttendanceActivity.this).add(jsonObjectRequest);
            }
        });
    }

    @Override
    public void onAttendanceChanged(int position, String value) {
        list.get(position).setValue(value);
    }

    private class GetAttendanceResponse {
        private List<AttendanceModel> attendanceList;

        List<AttendanceModel> getAttendanceList() {
            return attendanceList;
        }

        void setAttendanceList(List<AttendanceModel> attendanceList) {
            this.attendanceList = attendanceList;
        }
    }

    private class AttendanceListAdapter extends ArrayAdapter<AttendanceModel> {

        List<AttendanceModel> list;
        AttendanceListener listener;

        AttendanceListAdapter(@NonNull Context context, List<AttendanceModel> list, AttendanceListener listener) {
            super(context, R.layout.row_for_courses_list);
            this.list = list;
            this.listener = listener;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_for_mark_attendance_list, null);

            TextView textViewName = convertView.findViewById(R.id.textView_name);
            TextView textViewPhone = convertView.findViewById(R.id.textView_phone);
            final CheckBox checkBoxValue = convertView.findViewById(R.id.checkbox_value);

            textViewName.setText(list.get(position).getName() != null
                    ? list.get(position).getName() : "NA");
            textViewPhone.setText(list.get(position).getPhone());
            checkBoxValue.setChecked("P".equalsIgnoreCase(list.get(position).getValue()));

            checkBoxValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAttendanceChanged(position, checkBoxValue.isChecked() ? "P" : "A");
                    list.get(position).setValue(checkBoxValue.isChecked() ? "P" : "A");
                }
            });
            return convertView;
        }
    }
}
