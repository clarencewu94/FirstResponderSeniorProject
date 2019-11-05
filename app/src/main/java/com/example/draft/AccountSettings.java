package com.example.draft;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AccountSettings extends AppCompatActivity {
//https://www.youtube.com/watch?v=E6vE8fqQPTE for username
//https://firebase.google.com/docs/auth/android/manage-users
//continue to work, work on bluetooth tomorrow
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
       // ListView mListView = (ListView) findViewById(R.id.listView);

    }

    ArrayList<Users> Profile = new ArrayList<>();

//    ListAdaptor adapter = new ListAdaptor(this, R.layout.adapter_view_layout, Profile);
//        mListView.setAdapter(adapter);

}
