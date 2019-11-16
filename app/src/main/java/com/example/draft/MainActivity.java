package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String Tag = "MainActivity";
    Button Scan;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public DeviceScanActivity.DeviceListAdapter mLeDeviceListAdapter;
    public DeviceScanActivity mDeviceScanActivity;
    private Toolbar mainToolbar;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ListView lvNewDevices;

    private FirebaseAuth mAuth;


    private static HashMap<String, String> attributes = new HashMap();
    private static final UUID[] HEART_RATE_MEASUREMENT = new UUID[] {
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)};

    private boolean isConnected;

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Main Page");


        lvNewDevices = findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Scan = findViewById(R.id.scan_btn);
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
//        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, Item_edit_details.class);
//                intent.putExtra("the_product", product_all.get(position));
//                startActivity(intent);
//            }
//        });
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

        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logout();
                return true;
            case R.id.action_settings_btn:
                AccountPage();
                return true;
            case R.id.scan_btn:
                //mLeDeviceListAdapter.clear();
                onScanButton();
                Toast.makeText(this, "Scanning for Devices", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
                return false;

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

            Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
            startActivity(intent);
        }

    }
    //trying to get the device list adapter to work
//    private void addListners(){
//        Scan.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction()==MotionEvent.ACTION_BUTTON_PRESS){
//                    Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
//                    startActivity(intent);
//                }
//                return false;
//            }
//        });
//    }

    //https://stackoverflow.com/questions/22408063/getview-from-another-class
    //https://stackoverflow.com/questions/4593232/how-to-call-a-method-in-another-class-in-java
//    public MainActivity(View view){
//        mLeDeviceListAdapter = new DeviceListAdapter();
//        View gView = DeviceListAdapter.getView(DeviceListAdapter);
//        lvNewDevices = findViewById(R.id.lvNewDevices);
//        lvNewDevices.addView(gView);
//    }
//    private Handler mHandler;
//    private static final long SCAN_PERIOD = 20000;   // Stops scanning after 10 seconds.
//
//    private void scanLeDevice(boolean enable) {
//        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
//        mHandler = new Handler();
//        if (mBluetoothAdapter != null){
//            if (enable) {
//
//                // Stops scanning after a pre-defined scan period.
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mScanning = false;
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    }
//                }, SCAN_PERIOD);
//
//                mScanning = true;
//                mBluetoothAdapter.startLeScan(HEART_RATE_MEASUREMENT, mLeScanCallback);
//            }
//        }
//        else{
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }
//
//
//    private boolean mScanning = false;
//    private ArrayList<BluetoothDevice> mLeDevices;
//    private LayoutInflater mInflator;
//    private DeviceListAdapter leDeviceListAdapter;


//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//        if (device == null) return;
//        final Intent intent = new Intent(this, DeviceControlActivity.class);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        startActivity(intent);
//    }

//    public BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            DeviceListAdapter addDevice = new DeviceListAdapter();
//                            addDevice.addDevice(device);
//                            addDevice.notifyDataSetChanged();
//                        }
//                    });
//                }
//            };
//    public BluetoothDevice getDevice(int position) {
//        return mLeDevices.get(position);
//    }
//    public void addDevice(BluetoothDevice device) {
//        if (!mLeDevices.contains(device)) {
//            mLeDevices.add(device);
//        }
//    }

//    DeviceListAdapter.ViewHolder getmLeDeviceListAdapter = new DeviceListAdapter.ViewHolder() {
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        DeviceListAdapter.ViewHolder viewHolder;
//        // General ListView optimization code.
//        if (view == null) {
//            view = mInflator.inflate(R.layout.device_adapter_view, null);
//            viewHolder = new DeviceListAdapter.ViewHolder();
//            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.tvDeviceAddress);
//            viewHolder.deviceName = (TextView) view.findViewById(R.id.tvDeviceName);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (DeviceListAdapter.ViewHolder) view.getTag();
//        }
//
//        BluetoothDevice device = mLeDevices.get(i);
//        final String deviceName = device.getName();
//        if (deviceName != null && deviceName.length() > 0)
//            viewHolder.deviceName.setText(deviceName);
//        else
//            viewHolder.deviceName.setText(R.string.unknown_device);
//        viewHolder.deviceAddress.setText(device.getAddress());
//
//        return view;
//    }
//    };
}
//            DeviceControlActivity.setOnScanListener(new OnScanListener() {
//                @Override
//                public void onScanFinished() {
//                    ((Button)v).setText("scan");
//                    ((Button)v).setEnabled(true);
//                }
//                @Override
//                public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {}
//            });
//            ((Button)v).setText("scanning");
//            ((Button)v).setEnabled(false);
//            mLeDeviceListAdapter = new DeviceListAdapter();
//            DeviceScanActivity scanLeDevice = new DeviceScanActivity();
//            scanLeDevice.scanLeDevice(true);
//        }
//
//
//
//    public void setConnectStatus(boolean isConnected){
//        this.isConnected = isConnected;
//        DeviceControlActivity Control = new DeviceControlActivity();
//
//        if(isConnected){
//            showMessage("Connection successful");
//            Scan.setText("break");
//        }else{
//            Control.onPause();
//            Control.onDestroy();
//            Scan.setText("scan");
//        }
//    }
//    private void showMessage(String str){
//        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
//    }

