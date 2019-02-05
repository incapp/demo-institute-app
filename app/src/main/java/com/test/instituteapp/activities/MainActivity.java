package com.test.instituteapp.activities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.test.instituteapp.R;
import com.test.instituteapp.database.DBHelper;
import com.test.instituteapp.models.NotificationModel;
import com.test.instituteapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView textViewMobile, textViewName;
    DBHelper dbHelper;

    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        textViewName = navigationView.getHeaderView(0).findViewById(R.id.textView_name);
        textViewMobile = navigationView.getHeaderView(0).findViewById(R.id.textView_mobile);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        UploadImageActivity.class);
                startActivity(intent);
            }
        });

        mDemoSlider = findViewById(R.id.slider);

        dbHelper = new DBHelper(MainActivity.this);

        setupImageSlider();

        showNotifications();

        showUserInfo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_attendance) {
            startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
        } else if (id == R.id.nav_courses) {
            startActivity(new Intent(MainActivity.this, CoursesActivity.class));
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
        } else if (id == R.id.nav_forum) {
            startActivity(new Intent(MainActivity.this, ForumActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE);

        String mobile = sharedPreferences.getString(Constants.SH_PREF_KEY_MOBILE_NO, "NA");

        textViewMobile.setText(mobile);
        textViewName.setText(sharedPreferences.getString(Constants.SH_PREF_KEY_NAME, "NA"));

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .error(R.mipmap.ic_launcher);

        Glide
                .with(MainActivity.this)
                .applyDefaultRequestOptions(requestOptions)
                .load("http://www.krosscode.com/userPics" + "/" + mobile + ".jpg")
                .into(imageView);
    }

    private void showNotifications() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.API_GET_NOTIFICATION,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("notifications", response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        NotificationResponse apiResponse = new Gson().fromJson(jsonObject.toString(), NotificationResponse.class);

                        if (apiResponse.getNotifications() != null) {

                            List<NotificationModel> list = dbHelper.saveNotification(apiResponse.getNotifications());

                            displayNotification(list);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(MainActivity.this).add(jsonArrayRequest);
    }

    private void displayNotification(List<NotificationModel> notifications) {

        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

        String channel_id = "my_channel_Id";

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_DEFAULT));
        }

        for (int i = 0; i < notifications.size(); i++) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(notifications.get(i).getTitle())
                    .setContentText(notifications.get(i).getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notifications.get(i).getMessage()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            notificationManagerCompat.notify(i + 1, builder.build());
        }
    }

    private class NotificationResponse {
        private List<NotificationModel> notifications;

        List<NotificationModel> getNotifications() {
            return notifications;
        }
    }

    private void setupImageSlider() {
        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("Learn Java", "https://cdn.app.compendium.com/uploads/user/e7c690e8-6ff9-102a-ac6d-e4aebca50425/9f78fc09-faec-4068-82bd-09e7cc8bbf34/File/e19ea0216ae8395bd4b3389970928be9/java_logo.png");
        url_maps.put("Learn Android", "https://www.android.com/static/2016/img/share/n-lg.png");

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);

            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
    }

    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }
}
