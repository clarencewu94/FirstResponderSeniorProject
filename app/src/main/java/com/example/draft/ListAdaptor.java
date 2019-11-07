package com.example.draft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
//Specifically for User List/Personal Data
public class ListAdaptor extends ArrayAdapter<Users> {

    private static final String TAG = "ListAdaptor";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ListAdaptor(Context context, int resource, ArrayList<Users> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the persons information
        String Username = getItem(position).getUsername();
        String HeartRate = getItem(position).getHeartRate();
        String Oxygen = getItem(position).getOxygen();



            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            TextView UserN = (TextView) convertView.findViewById(R.id.tvName);
            TextView HR = (TextView) convertView.findViewById(R.id.tvHeartRate);
            TextView Oxy = (TextView) convertView.findViewById(R.id.tvOxygen);

            UserN.setText(Username);
            HR.setText(HeartRate);
            Oxy.setText(Oxygen);

        return convertView;
    }
}