package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView locationTextView;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private void initialise() {
        locationTextView = findViewById(R.id.locationTextView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                setLocationTextOnTextView(location);
            }
        };
    }

    private String appendString(String string, String defaultValue, String partToAppend, String spaceFromNextPart) {
        if (partToAppend != null && partToAppend.isEmpty()) {
            string += defaultValue + spaceFromNextPart;
        }
        else if (partToAppend != null) {
            string += partToAppend + spaceFromNextPart;
        }
        return  string;
    }

    private void setLocationTextOnTextView(Location locationOnMap) {
        Log.i("Info", "Location = " + locationOnMap.toString());
        String textForLocationTextView = "";
        textForLocationTextView += appendString("Latitude: ", "We could not find Latitude :(",
                Double.toString(BigDecimal
                        .valueOf(locationOnMap.getLatitude()).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()), "\n\n");

        textForLocationTextView += appendString("Longitude: ", "We could not find Longitude :(",
                Double.toString(BigDecimal
                        .valueOf(locationOnMap.getLongitude()).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()), "\n\n");

        textForLocationTextView += appendString("Accuracy: ", "We could not find Accuracy :(",
                Double.toString(BigDecimal
                        .valueOf(locationOnMap.getAccuracy()).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()), "\n\n");

        textForLocationTextView += appendString("Altitude: ", "We could not find Altitude :(",
                Double.toString(BigDecimal
                        .valueOf(locationOnMap.getAltitude()).setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()), "\n\n");

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(locationOnMap.getLatitude(),
                    locationOnMap.getLongitude(), 1);
            if (addressList != null && !addressList.isEmpty()) {
                Log.i("Info", "Address Object: " + addressList.get(0).toString());
                textForLocationTextView += appendString("Address: ", "We could not find Address :(",
                        addressList.get(0).getThoroughfare(), "");
            }
            else {
                textForLocationTextView += "Address: " + "We could not find Address :(";
            }
        } catch (IOException e) {
            e.printStackTrace();
            textForLocationTextView += "Address: " + "We could not find Address :(";
        }

        locationTextView.setText(textForLocationTextView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if permission was granted
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                /*
                We should ideally choose the arguments to the following method carefully as they effect
                the battery life of the user's phone and how frequently we want to be updated of the
                user's location based on time and distance
                */
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000,0, locationListener);
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // For the check if SDK version is less than 23.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();

        // Checking if we did not get user's permission to use their location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Asking for user to grant permission to use their location
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            // If we did get the permission, let's get some updates on the location of the user
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000,0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setLocationTextOnTextView(lastKnownLocation);
        }
    }
}