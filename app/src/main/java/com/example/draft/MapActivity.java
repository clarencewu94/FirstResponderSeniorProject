package com.example.draft;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private String userID;

    private GoogleMap mMap;
    LocationManager locationManager;
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase tracker;
    private DatabaseReference databaseLocation;
    private DatabaseReference mDatabase;
    private DatabaseReference refDatabase;
    Marker marker, marker1;
    GoogleMap googleMap1;
    GoogleMap gMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tracker = FirebaseDatabase.getInstance();
        mDatabase = tracker.getReference("UserID/Location");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase.addChildEventListener(new ChildEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.child("Location").getChildren()) {
                    Coordinates mDatabase = dataSnapshot.getValue(Coordinates.class);
                    String latitude = Objects.requireNonNull(child.child("Latitude").getValue()).toString();
                    String longitude = Objects.requireNonNull(child.child("Longitude").getValue()).toString();
                    double loclatitude = Double.parseDouble(latitude);
                    double loclongitude = Double.parseDouble(longitude);
                    LatLng cod = new LatLng(loclatitude, loclongitude);
                    googleMap1.addMarker(new MarkerOptions().position(cod).title(""));
                }
            }
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap1=googleMap;

    }

//            databaseLocation.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot child : dataSnapshot.child("UserID").getChildren()) {
//                        Coordinates UserID = dataSnapshot.getValue(Coordinates.class);
//                        String latitude = child.child("latitude").getValue().toString();
//                        String longitude = child.child("longitude").getValue().toString();
//                        double loclatitude = Double.parseDouble(latitude);
//                        double loclongitude = Double.parseDouble(longitude);
//                        LatLng cod = new LatLng(loclatitude, loclongitude);
//                        googleMap1.addMarker(new MarkerOptions().position(cod).title(""));
//                        marker1=gMap.addMarker(new MarkerOptions().position(new LatLng
//                                (dataSnapshot.child("latitude").getValue(double.class),
//                                        dataSnapshot.child("longitude").getValue(double.class)))
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
//                                .title("Bus 1")); marker1.showInfoWindow();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//
//
//            });



    }

//        databaseLocation.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.child("UserID").getChildren()) {
//                    UserID UserID = dataSnapshot.getValue(UserID.class);
//                    String latitude = child.child("latitude").getValue().toString();
//                    String longitude = child.child("longitude").getValue().toString();
//                    double loclatitude = Double.parseDouble(latitude);
//                    double loclongitude = Double.parseDouble(longitude);
//                    LatLng cod = new LatLng(loclatitude, loclongitude);
//                    googleMap.addMarker(new MarkerOptions().position(cod).title(""));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });









