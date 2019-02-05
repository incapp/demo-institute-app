package com.test.instituteapp.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.test.instituteapp.activities.ForumActivity;
import com.test.instituteapp.models.MessagesResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MessageService extends Service {

    RequestQueue requestQueue;

    public MessageService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(MessageService.this);
        }

        if (intent != null
                && intent.getExtras() != null) {
            final int id = intent.getExtras().getInt(ForumActivity.ARGS_ID, 0);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    Constants.API_GET_MESSAGES_BY_ID + "/" + String.valueOf(id),
                    new JSONArray(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject object = new JSONObject();

                            try {
                                object.put("list", response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MessagesResponse apiResponse = new Gson()
                                    .fromJson(object.toString(), MessagesResponse.class);

                            if (apiResponse != null
                                    && apiResponse.getList() != null
                                    && apiResponse.getList().size() != 0) {

                                Intent newIntent = new Intent(ForumActivity.ACTION_MESSAGE);

                                newIntent.putExtra(ForumActivity.ARGS_ID, apiResponse.getList().get(apiResponse.getList().size() - 1).getId());
                                newIntent.putExtra(ForumActivity.ARGS_MESSAGE_LIST, (Serializable) apiResponse.getList());

                                LocalBroadcastManager
                                        .getInstance(getApplicationContext())
                                        .sendBroadcast(newIntent);
                            } else {
                                Intent newIntent = new Intent(ForumActivity.ACTION_MESSAGE);
                                newIntent.putExtra(ForumActivity.ARGS_ID, id);
                                LocalBroadcastManager
                                        .getInstance(getApplicationContext())
                                        .sendBroadcast(newIntent);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Intent newIntent = new Intent(ForumActivity.ACTION_MESSAGE);
                            newIntent.putExtra(ForumActivity.ARGS_ID, id);
                            LocalBroadcastManager
                                    .getInstance(getApplicationContext())
                                    .sendBroadcast(newIntent);
                        }
                    }
            );

            requestQueue.add(jsonArrayRequest);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
