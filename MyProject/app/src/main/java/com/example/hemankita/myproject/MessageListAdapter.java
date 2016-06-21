package com.example.hemankita.myproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hemankita on 6/20/2016.
 */
public class MessageListAdapter extends ArrayAdapter<Message> {
    private List<Message> messages;
    private int resource;
    private Context context;

    public MessageListAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
        this.context = context;
        this.resource = resource;
        this.messages = messages;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(resource, parent, false);
        holder = new ViewHolder();
        holder.msg = messages.get(position);

        holder.message_name = (TextView) row.findViewById(R.id.message_name);
        holder.time_to_live = (TextView) row.findViewById(R.id.ttl);


        row.setTag(holder);

        setupItem(holder);
        return row;
    }


    private void setupItem(ViewHolder holder) {
        holder.message_name.setText(holder.msg.getMessage());
        holder.time_to_live.setText(String.valueOf(holder.msg.getTimeToLive_ms()));
        //holder.con.setContact(holder.con.getContact());
    }

    public class ViewHolder {
        Message msg;
        TextView message_name;
        TextView time_to_live;
    }
}
