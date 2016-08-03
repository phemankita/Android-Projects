package com.example.kbaldor.rxcontactstatus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hemankita on 7/29/2016.
 */
public class listAdapter extends ArrayAdapter<String> {
    private List<String> contacts;
    private int resource;
    private Context context;


    public listAdapter(Context context, int resource, ArrayList<String> contacts) {
        super(context,resource,contacts);
        this.context = context;
        this.resource = resource;
        this.contacts = contacts;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
        row = inflater.inflate(resource, parent, false);
        holder = new ViewHolder();
        holder.con = contacts.get(position);


        holder.contact_name = (TextView) row.findViewById(R.id.name_Contact);
        holder.status_button = (ImageView) row.findViewById(R.id.statusButton);


        row.setTag(holder);

        setupItem(holder);

        return row;
    }


    private void setupItem(ViewHolder holder) {
        holder.contact_name.setText(holder.con);

        if(MainActivity.login.contains(holder.con))
        {
            Log.d("uuuuuuu", "in login");
            holder.status_button.setImageResource(R.drawable.login);
        }
        else {
            holder.status_button.setImageResource(R.drawable.logout);
        }



        //holder.con.setContact(holder.con.getContact());

    }

    public class ViewHolder {
        String con;
        TextView contact_name;
        ImageView status_button;
    }
}
