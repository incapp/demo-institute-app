package com.test.instituteapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.test.instituteapp.R;
import com.test.instituteapp.database.DBHelper;
import com.test.instituteapp.fragments.AddNotificationFragment;
import com.test.instituteapp.models.NotificationModel;
import com.test.instituteapp.utils.Constants;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    ListView listView;
    DBHelper dbHelper;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listView);
        floatingActionButton = findViewById(R.id.fab);

        dbHelper = new DBHelper(NotificationActivity.this);

        List<NotificationModel> notifications = dbHelper.getAllNotifications();

        listView.setAdapter(new NotificationAdapter(NotificationActivity.this, notifications));

        String userType = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                .getString(Constants.SH_PREF_KEY_TYPE, "U");

        if ("A".equalsIgnoreCase(userType)) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNotificationFragment()
                        .show(getSupportFragmentManager(), AddNotificationFragment.class.getSimpleName());
            }
        });
    }

    public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

        private List<NotificationModel> list;

        NotificationAdapter(@NonNull Context context, List<NotificationModel> list) {
            super(context, R.layout.row_for_notification_list);
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_for_notification_list, null);

                TextView textViewTitle = convertView.findViewById(R.id.textView_notification_title);
                TextView textViewMessage = convertView.findViewById(R.id.textView_notification_message);
                TextView textViewDate = convertView.findViewById(R.id.textView_notification_date);

                textViewDate.setText(list.get(position).getDate());
                textViewTitle.setText(list.get(position).getTitle());
                textViewMessage.setText(list.get(position).getMessage());
            }

            return convertView;
        }
    }
}
