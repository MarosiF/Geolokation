package com.example.geolokation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geoloktaion.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //references to UI elements
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationupdates, sw_gps;

    // FusedLocationProviderClient is the standard API used for reading GPS and cell phone tower location data.
    FusedLocationProviderClient  fusedLocationProviderClient;

    //LocationRequest is a config file, contains quality of service parameters that will influence the way the  FusedLocationProvider works.
    //declare LocationRequest  :)
    //LocationRequest locationRequest;

    LocationCallback locationCallBack;

    //variable to remember if we are tracking location or not
    boolean updateOn;

    //update intervals
    final int DEFAULT_UPDATE_INTERVAL = 30;
    final int FAST_UPDATE_INTERVAL = 5;

    //permission constant
    private static final int PERMISSION_FINE_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting all the values of the UI settings
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);

        sw_locationupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);

        //set all properties of LocationRequest, initiation
        locationRequest = new LocationRequest();
        //how often does the default location check occur?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        //how often does the default location check occur, if we are using maximum power & maximum accuracy?
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        //default provider
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // event that is triggered whenever the update interval is set
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //save the location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        //switch between providers
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_gps.isChecked()){
                    //most accurate - Provider: GPS
                    // set provider GPS
                    //LocationRequest.PRIORITY_HIGH_ACCURACY
                    locationRequest.setPriority(LocationRequest.);
                    tv_sensor.setText("Using GPS sensors");
                }else{
                    //Provider: Network Wifi, cell phone tower
                    //set provider Network
                    //LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    locationRequest.setPriority(LocationRequest.);
                    tv_sensor.setText("Using Cell Towers or Wifi");
                }
            }
        });

        //
        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationupdates.isChecked()){
                    //turn on location tracking
                    startLocationUpdates();
                }
                else{
                    //turn off tracking
                    stopLocationUpdates();

                }
            }
        });

        //update location data and UI
        updateGPS();
    } //end OnCreate method

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked!");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked!");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }


    private void updateGPS(){
        // FusedLocationProviderClient initialisieren
        // = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fusedLocationProviderClient =

        //get permission from the user to track GPS
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //user provided the permission
            //if a location is found than the SuccessListener will create an other trigger to an other anonymous function it´s called
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>(){
                @Override
                    public void onSuccess(Location location) {
                    //Update the UI
                        updateUIValues(location);
                    }
                });
        }
        else{
            //user permission not granted yet
            //checking valid OS
              if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                  requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
              }
        }

    }

    //trigger a method after permissions has been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else{
                    Toast.makeText(this, "This app requires permission.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    //update all of the text view objects with a new location
    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else{
          tv_altitude.setText("Not avaible");
        }

        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tv_speed.setText("Not avaible");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }catch(Exception e){
            tv_address.setText("Unable to get address");
        }
    }
}
