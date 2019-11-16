package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.jjoe64.graphview.Viewport;
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
    public final static String EXTRA_DATA =
            "com.example.draft.EXTRA_DATA";
    //-----------------------------------------//For Graph
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    public boolean isRunning = true;

    //-----------------------------------------//for bluetooth data


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
        mainToolbar = (Toolbar) findViewById(R.id.second_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Account Page");

        //For FireStore
        db = FirebaseFirestore.getInstance();
        //Button, Text Setup
        set_name = (EditText) findViewById(R.id.set_name);
        set_age = (EditText) findViewById(R.id.set_age);
        save_btn = (Button) findViewById(R.id.save_btn);

        //TextView setup
        HR_Peaktxt = (TextView) findViewById(R.id.HRPeakEdit);
        HR_Lowest_txt = (TextView) findViewById(R.id.HR_Lowest_txt);
        Oxygentxt = (TextView) findViewById(R.id.OxygenEdit);
        Confidencetxt = (TextView) findViewById(R.id.ConfidenceEdit);

        //Graph
        GraphView graph = (GraphView)findViewById(R.id.graph);
        Button Run = (Button) findViewById(R.id.RunGraph);

        //Create the Datapoints
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        //Viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(60);
        viewport.setMaxY(110);
        viewport.setScrollable(true);
        // Creating onClickListener for the "RUN" Button
        Run.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isRunning=true;

                // Create a thread to run the graph
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (isRunning==true) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addEntry();
                                }
                            });
                            // using sleep to slow down the addition of new points
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                System.out.print(e.getMessage());
                            }
                        }
                    }
                }).start();
            }
        });

        HR_Peaktxt.addTextChangedListener(generalTextWatcher);
        HR_Lowest_txt.addTextChangedListener(generalTextWatcher);
        Oxygentxt.addTextChangedListener(generalTextWatcher);
        Confidencetxt.addTextChangedListener(generalTextWatcher);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu2, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.main_btn:
                mainpage();
                return true;
//            case R.id.scan_btn:
//                //mLeDeviceListAdapter.clear();
//                onScanButton();
//                Toast.makeText(this, "Scanning for Devices", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                break;
        }
        return false;

    }
    //Logging out
    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(AccountSettings.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();//makes sure user does not go back, finished intent
    }
    private void mainpage(){
        Intent loginIntent = new Intent(AccountSettings.this, MainActivity.class);
        startActivity(loginIntent);
        finish();//makes sure user does not go back, finished intent
    }
    private TextWatcher generalTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

//            if (HR_Peaktxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText1_onTextChanged(s, start, before, count);
//            }
//            else if (HR_Lowest_txt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_onTextChanged(s, start, before, count);
//            }
//            else if (Oxygentxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_onTextChanged(s, start, before, count);
//            }
//            else if (Confidencetxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_onTextChanged(s, start, before, count);
//            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

//            if (HR_Peaktxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText1_beforeTextChanged(s, start, count, after);
//            }
//            else if (HR_Lowest_txt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_beforeTextChanged(s, start, count, after);
//            }
//            else if (Oxygentxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_beforeTextChanged(s, start, count, after);
//            }
//            else if (Confidencetxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_beforeTextChanged(s, start, count, after);
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {
//            if (HR_Peaktxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText1_afterTextChanged(s);
//            }
//            else if (HR_Lowest_txt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_afterTextChanged(s);
//            }
//            else if (Oxygentxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_afterTextChanged(s);
//            }
//            else if (Confidencetxt.getText().hashCode() == s.hashCode())
//            {
//                myEditText2_afterTextChanged(s);
//            }
        }

    };
    // Method that adds new points to the graph
    private void addEntry() {


        //series.appendData(new DataPoint(lastX++,60+ rand.nextDouble() * 40d), true, 10);
       // series.appendData(new DataPoint(lastX++,60+ EXTRA_DATA.nextDouble() * 40d), true, 10);
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


    }

