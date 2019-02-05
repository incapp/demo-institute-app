package com.test.instituteapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.instituteapp.R;
import com.test.instituteapp.models.MessageModel;

import java.util.List;

public class MessageListAdapter extends ArrayAdapter<MessageModel> {

    private List<MessageModel> list;

    public MessageListAdapter(@NonNull Context context, List<MessageModel> list) {
        super(context, R.layout.row_for_message_list);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater
                .from(getContext())
                .inflate(R.layout.row_for_message_list, null);

        TextView textViewMessage = convertView.findViewById(R.id.textView_message);
        TextView textViewName = convertView.findViewById(R.id.textView_phone_name);
        TextView textViewDate = convertView.findViewById(R.id.textView_date);

        textViewMessage.setText(list.get(position).getMessage());
        textViewName.setText(String.format("%s(%s)", list.get(position).getName(), list.get(position).getPhone()));
        textViewDate.setText(list.get(position).getDate());

        return convertView;
    }
}
