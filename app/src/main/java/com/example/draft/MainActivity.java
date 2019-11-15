package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.draft.DeviceListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private static final String Tag = "MainActivity";
    Button Scan;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public DeviceListAdapter mLeDeviceListAdapter;
    private Toolbar mainToolbar;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ListView lvNewDevices;

    private FirebaseAuth mAuth;



    private static HashMap<String, String> attributes = new HashMap();
    public static String Discovery = "0000180d-0000-1000-8000-00805f9b34fb"; //replace this UUID
    static
    {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Discovery Service");
        attributes.put(Discovery, "Discovery Module");

    }
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Main Page");


        lvNewDevices =(ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_settings_btn:
                AccountPage();
                return true;
            case R.id.scan_btn:
                //mLeDeviceListAdapter.clear();
                onScanButton();
                return true;
            default:
                return false;
        }
    }


    /*--------------------------------------------Definitions here----------------------------------------------------   */

    private void AccountPage() {
        sendToAccountPage();
    }
    private void sendToAccountPage() {
        Intent AccountIntent = new Intent(MainActivity.this, AccountSettings.class);
        startActivity(AccountIntent);
        finish();//makes sure user does not go back, finished intent
    }
    //Logging out
    private void logout() {
        mAuth.signOut();
        sendToLogin();
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();//makes sure user does not go back, finished intent
    }
    private static final int REQUEST_ENABLE_BT = 1;

    public void onScanButton() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

            // Initializes list view adapter.
            mLeDeviceListAdapter = new DeviceListAdapter();
            DeviceScanActivity scanLeDevice = new DeviceScanActivity();
            scanLeDevice.scanLeDevice(true);
        }
        //DeviceScanActivity.onListItemClick
//-----------------------------Testing----------------------------------------
    }
}
