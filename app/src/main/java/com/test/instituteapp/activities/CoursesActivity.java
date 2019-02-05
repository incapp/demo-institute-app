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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.test.instituteapp.R;
import com.test.instituteapp.models.CoursesModel;
import com.test.instituteapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ListView listViewCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        progressBar = findViewById(R.id.progressBar);
        listViewCourses = findViewById(R.id.listView_courses);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.API_GET_ALL_COURSES,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("coursesList", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        GetAllCoursesResponse apiResponse = new Gson().fromJson(jsonObject.toString(), GetAllCoursesResponse.class);

                        if (apiResponse.getCoursesList() != null) {
                            listViewCourses.setAdapter(new CoursesListAdapter(CoursesActivity.this, apiResponse.getCoursesList()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CoursesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(CoursesActivity.this).add(jsonObjectRequest);
    }

    private class GetAllCoursesResponse {
        private List<CoursesModel> coursesList;

        List<CoursesModel> getCoursesList() {
            return coursesList;
        }
    }

    private class CoursesListAdapter extends ArrayAdapter<CoursesModel> {

        List<CoursesModel> list;

        CoursesListAdapter(@NonNull Context context, List<CoursesModel> list) {
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_for_courses_list, null);

                TextView textViewName = convertView.findViewById(R.id.textView_course_name);
                TextView textViewDesc = convertView.findViewById(R.id.textView_course_desc);

                textViewName.setText(list.get(position).getName());
                textViewDesc.setText(list.get(position).getDescp());
            }
            return convertView;
        }
    }
}
