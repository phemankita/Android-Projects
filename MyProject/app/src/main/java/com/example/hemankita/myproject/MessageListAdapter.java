package com.example.hemankita.myproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hemankita on 6/20/2016.
 */
public class MessageListAdapter extends ArrayAdapter<Message> {
    private List<Message> messages;
    private int resource;
    private Context context;

    private LayoutInflater lf;
    private List<ViewHolder> lstHolders;
    private Handler mHandler = new Handler();
    private ArrayList<String> msglist = new ArrayList<>();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

    public MessageListAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
        this.context = context;
        this.resource = resource;
        this.messages = messages;
        lf = LayoutInflater.from(context);
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }





    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /*View row = convertView;
        ViewHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(resource, parent, false);
        holder = new ViewHolder();
        holder.msg = messages.get(position);

        holder.message_name = (TextView) row.findViewById(R.id.message_name);
        holder.time_to_live = (TextView) row.findViewById(R.id.ttl);


        row.setTag(holder);

        setupItem(holder);
        return row;*/
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = lf.inflate(R.layout.activity_listview, parent, false);
            holder.message_name = (TextView) convertView.findViewById(R.id.message_name);
            holder.time_to_live = (TextView) convertView.findViewById(R.id.ttl);
            Log.i("holder text",holder.time_to_live.getText().toString());
            /*if(holder.time_to_live.getText().toString().equals("Expired!!")){
                MessageDBHelper msgdata = new MessageDBHelper(convertView.getContext());
                String name = holder.message_name.getText().toString();
                msgdata.deleteMessage(msgdata.getMessage(name));
            }*/
            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setData(getItem(position));

        return convertView;
    }



    private void setupItem(ViewHolder holder) {
        holder.message_name.setText(holder.msg.getSenderName()+" | "+holder.msg.getSubjectLine());
        holder.time_to_live.setText(String.valueOf(holder.msg.getTimeToLive_ms())+"ms");
        //holder.con.setContact(holder.con.getContact());
    }

    public class ViewHolder {
        Message msg;
        TextView message_name;
        TextView time_to_live;

        public void setData(Message item) {
            msg = item;
            message_name.setText(item.getSenderName()+" | "+item.getSubjectLine());
            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {

            long timeDiff =   msg.getTimeToLive_ms() - currentTime;
           // Log.i("TTL",Long.valueOf(currentTime).toString());
           // Log.i("born time",Long.valueOf(msg.getBornTime_ms()).toString());
            //Log.i("TTL",Long.valueOf(msg.getTimeToLive_ms()).toString());
            //Log.i("Time difference",Long.valueOf(timeDiff).toString());
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000) % 60;
                //Log.i("TTL",Integer.valueOf(seconds).toString());
                int minutes = (int) ((timeDiff / (1000 * 60)) % 60);
                int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                time_to_live.setText(hours + " hrs " + minutes + " mins " + seconds + " sec");
            } else {
                time_to_live.setText("Expired!!");
                //Log.i("expired","done");
                MessageDBHelper m = MessageDBHelper.getInstance(getContext());
                m.deleteMessage(msg);
                //Log.i("jj","deleted");
                notifyDataSetChanged();

            }

        }

    }
}
