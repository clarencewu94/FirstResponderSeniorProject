/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.draft;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity{
    //scan bluetooth Device
    public BluetoothAdapter mBluetoothAdapter;

    private boolean mEnabled = false;
    private boolean mScanning = false;
    private static final long SCAN_PERIOD = 20000;   // Stops scanning after 10 seconds.
    private DeviceListAdapter mDevicelistAdapter;
    private String CurrentlyConnected; //for if the ble device is currently connected

    //connect bluetooth device
    private BluetoothLeService mBluetoothService;
    private String mDeviceAddress = null;
    private boolean mConnected = false;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    private Toolbar mainToolbar;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (!mScanning) {
            menu.findItem(R.id.scan_btn).setVisible(true);
        } else {
            menu.findItem(R.id.scan_btn).setVisible(false);
        }
        return true;
    }   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_btn:
                mDevicelistAdapter.clear();
                scanLeDevice(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainToolbar = findViewById(R.id.main_toolbar);
        setContentView(R.layout.activity_main3);
        mHandler = new Handler();
        ExpandableListView elv = (ExpandableListView) findViewById(android.R.id.list);

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
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.scan_btn:
//                mDevicelistAdapter.clear();
//                scanLeDevice(true);
//                break;
//        }
//        return true;
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
            // Initializes list view adapter.
            mDevicelistAdapter = new DeviceListAdapter();
            setListAdapter(mDevicelistAdapter);
            scanLeDevice(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mDevicelistAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDevicelistAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }
    private static final UUID[] HEART_RATE_MEASUREMENT = new UUID[] {
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)};

    public void scanLeDevice(final boolean enable) {
//        Intent i = getIntent();
//        res = i.getStringExtra("res");
        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler();
        if (mBluetoothAdapter != null){
            if (enable) {

                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(HEART_RATE_MEASUREMENT, mLeScanCallback);
            }
        }
            else{
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    // Device scan callback.
    public BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DeviceListAdapter ListAd = new DeviceListAdapter();
                            ListAd.addDevice(device);
                            ListAd.notifyDataSetChanged();
                        }
                    });
                }
            };
    private ArrayList<BluetoothDevice> mLeDevices;
    // Adapter for holding devices found through scanning.
     class DeviceListAdapter extends BaseAdapter {
        private TextView tvDeviceName, tvDeviceAddress;          //From adapterView
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        private Context context;

        public DeviceListAdapter() {
            this.context = context;
            mLeDevices = new ArrayList<BluetoothDevice>();
//        mInflator = DeviceScanActivity.this.getLayoutInflater();

        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();

        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            com.example.draft.DeviceScanActivity.ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.device_adapter_view, null);
                viewHolder = new com.example.draft.DeviceScanActivity.ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.tvDeviceAddress);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.tvDeviceName);
                view.setTag(viewHolder);
            } else {
                viewHolder = (com.example.draft.DeviceScanActivity.ViewHolder) view.getTag();
            }
            //add parameters
            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

        static class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
        }
    }





