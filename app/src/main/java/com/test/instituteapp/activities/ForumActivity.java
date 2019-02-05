package com.test.instituteapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.test.instituteapp.R;
import com.test.instituteapp.models.MessageModel;
import com.test.instituteapp.models.MessagesResponse;
import com.test.instituteapp.utils.Constants;
import com.test.instituteapp.utils.MessageListAdapter;
import com.test.instituteapp.utils.MessageService;
import com.test.instituteapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    public static final String ARGS_ID = "id";
    public static final String ARGS_MESSAGE_LIST = "list";
    public static final String ACTION_MESSAGE = "com.test.chatapp.ACTION_MESSAGE";

    RequestQueue requestQueue;
    ListView listView;
    EditText editText;
    ImageButton buttonSend;
    MessageListAdapter adapter;
    List<MessageModel> list = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getExtras() != null
                        && intent.getExtras().containsKey(ARGS_MESSAGE_LIST)) {
                    List<MessageModel> temp = (List<MessageModel>) intent.getExtras().get(ARGS_MESSAGE_LIST);
                    if (temp != null
                            && temp.size() != 0) {
                        list.addAll(temp);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(listView.getCount() - 1);
                    }
                }
                startServiceWithId(intent.getIntExtra(ARGS_ID, 0));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        buttonSend = findViewById(R.id.button_send);

        adapter = new MessageListAdapter(ForumActivity.this, list);

        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(ForumActivity.this);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty())
                    sendMessage();
            }
        });

        getAllMessages();
    }

    private void sendMessage() {

        String name = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                .getString(Constants.SH_PREF_KEY_NAME, "NA");
        String phone = getSharedPreferences(Constants.MY_SH_PREF, Context.MODE_PRIVATE)
                .getString(Constants.SH_PREF_KEY_MOBILE_NO, "NA");

        JSONObject object = new JSONObject();

        try {
            object.put("message", editText.getText().toString());
            object.put("phone", phone);
            object.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.showProgressDialog(ForumActivity.this, "Please Wait!!!", "Posting...");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.API_ADD_MESSAGE,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dismissDialog();
                        Toast.makeText(ForumActivity.this, "Posted.", Toast.LENGTH_SHORT).show();
                        editText.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissDialog();
                        Toast.makeText(ForumActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void getAllMessages() {
        Utils.showProgressDialog(ForumActivity.this, "Please Wait!!!", "Posting...");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                Constants.API_GET_ALL_MESSAGES,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Utils.dismissDialog();
                        MessagesResponse apiResponse = getResponse(response);

                        if (apiResponse != null
                                && apiResponse.getList() != null
                                && apiResponse.getList().size() != 0) {
                            list.addAll(apiResponse.getList());
                            adapter.notifyDataSetChanged();
                            listView.setSelection(listView.getCount() - 1);
                        }
                        if (list != null && list.size() != 0) {
                            startServiceWithId(list.get(list.size() - 1).getId());
                        } else {
                            startServiceWithId(0);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissDialog();
                        Toast.makeText(ForumActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        startServiceWithId(0);
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private MessagesResponse getResponse(JSONArray response) {
        JSONObject object = new JSONObject();

        try {
            object.put("list", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Gson()
                .fromJson(object.toString(), MessagesResponse.class);
    }

    private void startServiceWithId(int id) {
        Intent intent = new Intent(ForumActivity.this, MessageService.class);
        intent.putExtra(ARGS_ID, id);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager
                .getInstance(ForumActivity.this)
                .registerReceiver(receiver, new IntentFilter(ACTION_MESSAGE));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager
                .getInstance(ForumActivity.this)
                .unregisterReceiver(receiver);
        super.onStop();
    }
}
