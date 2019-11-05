package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String Tag = "MainActivity";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdaptor;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private Toolbar mainToolbar;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdaptor mDeviceListAdapter;
    ListView lvNewDevices;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mBluetoothAdaptor = BluetoothAdapter.getDefaultAdapter();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Main Page");

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);
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
           case R.id.enable_ble_btn:
               EnableBLE();
               return true;
           case R.id.Discover_btn:
               Discover();
               return true;
           case R.id.scan_btn:
               scan();
               return true;

           default:
                return false;
       }
    }

    /*--------------------------------------------Definitions here----------------------------------------------------   */
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
    //Enabling the BLE, Turning on or off
    private void EnableBLE() {
        if(mBluetoothAdaptor == null){
            Log.d(Tag, "enabledDisableBT: Does not have Bluetooth capabilities");
        }
        if(!mBluetoothAdaptor.isEnabled()){
            Log.d(Tag, "enabledDisableBT: Enabling BT");

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdaptor.isEnabled()){
            Log.d(Tag, "enabledDisableBT: Disabling BT");
            mBluetoothAdaptor.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }
    //Android-powered devices are not discoverable by default
    //Make android device discoverable for 300 seconds
    public void Discover(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdaptor.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);
    }

    //Enable Searching for other devices.
    public void scan(){
        Log.d(Tag, "Scan: Looking for Unpaired Devices");
        if(mBluetoothAdaptor.isDiscovering()){
            mBluetoothAdaptor.cancelDiscovery();
            Log.d(Tag, "Scan: Cancelled Discovery");

            //Check BT Permissions in Manifest;
            checkBTPermissions();
            mBluetoothAdaptor.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);

        }
        if(!mBluetoothAdaptor.isDiscovering())
        {
            checkBTPermissions();
            mBluetoothAdaptor.isDiscovering();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    //Enable/Disable Bluetooth Broadcaster
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdaptor.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdaptor.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(Tag, "onRecieve: State OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(Tag, "onRecieve: State ON");
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(Tag, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(Tag, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(Tag, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(Tag, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(Tag, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    //Scanning for unpaired devices
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(Tag, "onReceive: ACTION FOUND");
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(Tag, "onReceive:" + device.getName() + ": "+ device.getAddress());
                mDeviceListAdapter =  new DeviceListAdaptor(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };
    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(Tag, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(Tag, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(Tag, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    /*method requires for all devices funning API23+;
       android must programmatically check the permissions for bluetooth. Put proper permission from manifest
       is not enough
       * */
    private void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else{
                Log.d(Tag, "CheckBTPermissions: No need to check permissions. SDK version <LOLLIPop");
            }
        }
    }
    @Override
    protected void onDestroy() {
        Log.d(Tag, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdaptor.cancelDiscovery();

        Log.d(Tag, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(Tag, "onItemClick: deviceName = " + deviceName);
        Log.d(Tag, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(Tag, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();
        }
    }

}
