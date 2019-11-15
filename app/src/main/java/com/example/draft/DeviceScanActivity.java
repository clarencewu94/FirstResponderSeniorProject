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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

            // Initializes list view adapter.
            mDevicelistAdapter = new DeviceListAdapter();
            setListAdapter(mDevicelistAdapter);
            scanLeDevice(true);
        }
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
    private static final UUID[] Discovery = new UUID[] {
            UUID.fromString(SampleGattAttributes.Discovery)};

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
                mBluetoothAdapter.startLeScan(Discovery, mLeScanCallback);
            }
        }
            else if (mBluetoothAdapter == null) {
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
                            mDevicelistAdapter.addDevice(device);
                            mDevicelistAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };
}
