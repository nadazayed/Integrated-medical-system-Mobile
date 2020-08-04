package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class DriveActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView spd, trip;
    LocationManager lm;
    Location last_loc;
    Float trip_dist = (float) 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);

        spd = (TextView) findViewById(R.id.speedo);
        trip = (TextView) findViewById(R.id.trip_dist);

//        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/ff.ttf");
//        spd.setTypeface(tf);
//        trip.setTypeface(tf);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        last_loc = null;
        request_updates();

    }

    public void request_updates()
    {
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(DriveActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            Toast.makeText(this, "GPS is Disabled in your device", Toast.LENGTH_SHORT).show();
        }
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            new_loc(location);
        }

        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };

    public void new_loc(Location loc){
        int speed_now = (int) (loc.getSpeed() * 60 * 60)/1601;

        if (speed_now > 0) {
            // Getting spurious values when not moving
            // Probably accuracy related
            if (last_loc != null) {
                trip_dist += loc.distanceTo(last_loc);

            }
            last_loc = loc;
        }
        if (trip_dist > 9999){
            trip_dist = (float) 0;
        }
        int odom_miles = (int) (trip_dist/1601);
        String speed = String.format("%02d", speed_now);
        String dist = String.format("%04d", odom_miles);

        spd.setText(speed);
        trip.setText(dist);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(DriveActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
