package com.example.draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String Tag = "MainActivity";
    public static final int ERROR_DIALOG_REQUEST = 9001;

    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    Button Scan;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public DeviceScanActivity.DeviceListAdapter mLeDeviceListAdapter;
    public DeviceScanActivity mDeviceScanActivity;
    private Toolbar mainToolbar;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ListView lvNewDevices;

    private FirebaseAuth mAuth;
    //if accepted, set to true, not able to navigate to other features
    private boolean mLocationPermissionGranted = false;

    private static HashMap<String, String> attributes = new HashMap();

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
            case R.id.MainP2:
                MainP2();
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

    private void MainP2() {
        sendToMain2();
    }

    private void sendToMain2() {
        Intent Main2Intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(Main2Intent);
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

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }
    //tells user to enables it if it does not have gps enabled.
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    //GPS, determines if application has GPS enabled on device
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    //determine if devices can use google services
    public boolean isServicesOK() {
        Log.d(Tag, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(Tag, "isServicesOK: Google Play Services is working");
            return true; //return true if google services is usable.
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(Tag, "isServicesOK: an error occured but we can fix it");
            //Dialog to download this
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    //CHECKS for the constant
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Tag, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (!mLocationPermissionGranted) {
                    getLocationPermission();
                }
            }
        }

    }
    @Override
    protected  void onResume(){
        super.onResume();
        if(checkMapServices()){
            if(!mLocationPermissionGranted){
                getLocationPermission();
            }
        }
    }
}
