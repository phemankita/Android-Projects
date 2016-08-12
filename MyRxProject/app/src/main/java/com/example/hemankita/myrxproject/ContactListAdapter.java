package com.example.hemankita.myrxproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hemankita on 6/20/2016.
 */
public class ContactListAdapter  extends ArrayAdapter<Contact> {

    private List<Contact> contacts;
        private int resource;
        private Context context;

        public ContactListAdapter(Context context, int resource, List<Contact> contacts) {
            super(context, resource, contacts);
            this.context = context;
            this.resource = resource;
            this.contacts = contacts;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            LayoutInflater inflater = ((ContactsActivity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.con = contacts.get(position);
            holder.settings_button = (ImageButton) row.findViewById(R.id.SettingsContactButton);
            holder.settings_button.setTag(holder.con);

            holder.contact_name = (TextView) row.findViewById(R.id.name_Contact);
            holder.status_button = (ImageView) row.findViewById(R.id.statusButton);


            row.setTag(holder);

            setupItem(holder);

            return row;
        }


        private void setupItem(ViewHolder holder) {
            holder.contact_name.setText(holder.con.getContact());

            if(holder.con.getStatus().equals("logged-in"))
            {
                Log.d("uuuuuuu", "in login");
                holder.status_button.setImageResource(R.drawable.login);
            }
            else {
                holder.status_button.setImageResource(R.drawable.logout);
            }



            holder.con.setContact(holder.con.getContact());

        }

        public class ViewHolder {
            Contact con;
            TextView contact_name;
            ImageButton settings_button;
            ImageView status_button;
        }
    }

