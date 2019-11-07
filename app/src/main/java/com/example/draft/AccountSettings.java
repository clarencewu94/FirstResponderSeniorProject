package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AccountSettings extends AppCompatActivity {
/*Profile Page, should include, Create Names (not email names)
Display Maximum Heart Rate Peak
Display Oxygen Levels
Graph
* */

//https://www.youtube.com/watch?v=E6vE8fqQPTE for username
//https://firebase.google.com/docs/auth/android/manage-users
//continue to work, work on bluetooth tomorrow
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    

    public String username;
    public String email;

    EditText set_name, set_age;
    TextView HR_Peaktxt, HR_Lowest_txt,Oxygentxt,Confidencetxt;
    Button save_btn;
    UserID userID;

    public AccountSettings() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public AccountSettings(String username, String email) {
        this.username = username;
        this.email = email;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        // ListView mListView = (ListView) findViewById(R.id.listView);

        set_name = (EditText) findViewById(R.id.set_name);
        set_age = (EditText) findViewById(R.id.set_age);
        save_btn = (Button) findViewById(R.id.save_btn);

        //Manually Adding Data to database (test)
        userID = new UserID();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("UserID");
       save_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int agea = Integer.parseInt(set_age.getText().toString().trim());

               userID.setName(set_name.getText().toString().trim());
               userID.setAge(agea);
               mDatabase.child("UserInfo").setValue(userID);
               Toast.makeText(AccountSettings.this, "data inserted successfully", Toast.LENGTH_SHORT).show();
           }
       });
            //retriving data from database (not set to show yet)
            //A DataSnapshot instance contains data from a Firebase Database location.
            //Any time you read Database data, you receive the data as a DataSnapshot.
            mDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("Name").getValue().toString();
                    String age = dataSnapshot.child("Age").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
        }


    ArrayList<Users> Profile = new ArrayList<>();


//    ListAdaptor adapter = new ListAdaptor(this, R.layout.adapter_view_layout, Profile);
//        mListView.setAdapter(adapter);
    public void submitReading(View view){

    }
    public void submitWriting(View view){

    }

}
