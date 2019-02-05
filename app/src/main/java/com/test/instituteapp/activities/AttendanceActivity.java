package com.test.instituteapp.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import com.test.instituteapp.utils.Constants;
import com.test.instituteapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity {

    TextView textViewDate;
    ListView listView;
    FloatingActionButton floatingActionButton;
    DatePickerDialog datePickerDialog;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        textViewDate = findViewById(R.id.textView_date);
        listView = findViewById(R.id.listView_attendance);
        floatingActionButton = findViewById(R.id.fab);

        calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(
                AttendanceActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String formattedDate = String.format(Locale.US, "%d-%d-%d", dayOfMonth, month + 1, year);

                        textViewDate.setText("Date: " + formattedDate);

                        showAttendance(reverseDate(formattedDate));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(calendar.getTime());
        textViewDate.setText("Date: " + currentDate);

        showAttendance(reverseDate(currentDate));

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        String userType = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                .getString(Constants.SH_PREF_KEY_TYPE, "U");

        if ("A".equalsIgnoreCase(userType)) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttendanceActivity.this, AddAttendanceActivity.class));
            }
        });
    }

    private void showAttendance(String date) {
        Utils.showProgressDialog(AttendanceActivity.this, "Please Wait!!!", "Loading...");

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.API_GET_ATTENDANCE + "/" + date,
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
                            listView.setAdapter(new AttendanceListAdapter(AttendanceActivity.this, apiResponse.getAttendanceList()));
                        } else {
                            Toast.makeText(AttendanceActivity.this, "No data found.", Toast.LENGTH_SHORT).show();
                            listView.setAdapter(new AttendanceListAdapter(AttendanceActivity.this, new ArrayList<AttendanceModel>()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissDialog();
                        Toast.makeText(AttendanceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(AttendanceActivity.this).add(jsonObjectRequest);
    }

    private String reverseDate(@NonNull String date) {
        String[] split = date.split("-");
        return split[2] + "-" + split[1] + "-" + split[0];
    }

    private class GetAttendanceResponse {
        private List<AttendanceModel> attendanceList;

        List<AttendanceModel> getAttendanceList() {
            return attendanceList;
        }
    }

    private class AttendanceListAdapter extends ArrayAdapter<AttendanceModel> {

        List<AttendanceModel> list;

        AttendanceListAdapter(@NonNull Context context, List<AttendanceModel> list) {
            super(context, R.layout.row_for_courses_list);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_for_attendance_list, null);

                TextView textViewName = convertView.findViewById(R.id.textView_name);
                TextView textViewPhone = convertView.findViewById(R.id.textView_phone);
                TextView textViewValue = convertView.findViewById(R.id.textView_value);

                textViewName.setText(list.get(position).getName() != null
                        ? list.get(position).getName() : "NA");
                textViewPhone.setText(list.get(position).getPhone());
                textViewValue.setText(list.get(position).getValue());
            }
            return convertView;
        }
    }
}
