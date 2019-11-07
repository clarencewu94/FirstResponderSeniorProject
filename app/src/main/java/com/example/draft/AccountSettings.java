package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private DocumentReference mCollection = FirebaseFirestore.getInstance().collection("UserID").document("UserInfo");;
    private FirebaseFirestore db;
    private Toolbar mainToolbar;

    public static final String NAME_KEY = "Name";
    public static final String AGE_KEY = "Age";

    public String username;
    public String email;

    EditText set_name, set_age;
    TextView HR_Peaktxt, HR_Lowest_txt, Oxygentxt, Confidencetxt;
    Button save_btn;
    UserID UserIDN;

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
        //For Main Toolbar
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Main Page");

        //For FireStore
        db = FirebaseFirestore.getInstance();
        //Button, Text Setup
        set_name = (EditText) findViewById(R.id.set_name);
        set_age = (EditText) findViewById(R.id.set_age);
        save_btn = (Button) findViewById(R.id.save_btn);

    }
        public void AddInfo(View view){
            String AddName = set_name.getText().toString();
            String AddAge = set_age.getText().toString();

            //Adding to FireStore cloud database //from https://www.youtube.com/watch?v=7hwlMKUgTQc
            if(AddName.isEmpty() || AddAge.isEmpty()){return;}
            Map<String, Object> DataToSave = new HashMap<>();
            DataToSave.put(NAME_KEY, AddName);
            DataToSave.put(AGE_KEY, AddAge);
            db.collection("UserID").add(DataToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(AccountSettings.this, "Name Added to FireStore", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String error = e.getMessage();
                    Toast.makeText(AccountSettings.this, "Error" + error, Toast.LENGTH_SHORT).show();

                }
            });
            //from https://www.youtube.com/watch?v=kDZYIhNkQoM
//            mCollection.set(DataToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Log.d(TAG, "Document was Saved");
//                    }
//                    else{
//                        Log.w(TAG, "Document was not saved", task.getException());
//                    }
//                }
//            });

        }

//
//        public void Graph{
//            GraphView graph = (GraphView) findViewById(R.id.graph);
//            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
//                    new DataPoint(0, 1),
//                    new DataPoint(1, 5),
//                    new DataPoint(2, 3),
//                    new DataPoint(3, 2),
//                    new DataPoint(4, 6)
//            });
//            graph.addSeries(series);
//        }

    }

