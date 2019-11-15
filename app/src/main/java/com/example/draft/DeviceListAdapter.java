package com.example.draft;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.draft.R;

import java.util.ArrayList;

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
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.device_adapter_view, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.tvDeviceAddress);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.tvDeviceName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
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


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}