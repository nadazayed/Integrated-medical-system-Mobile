package com.example.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.helper.Constants;
import com.example.myapplication.helper.FetchAddressIntentService;
import com.example.myapplication.helper.SOS;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;


public class EmergencyActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    static String loc = "";
    static String EmergencyContact, msg, patientName, medicalProvider;

    static double longitude = 0;
    static double latitude = 0;
    SOS sos;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        Log.e("WIFIIIII", String.valueOf(isNetworkAvailable()));

        sos = new SOS();
        resultReceiver = new AddressResultReceiver(new Handler());

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ACCESS_FINE_LOCATION", "REQUEST");
            ActivityCompat.requestPermissions(EmergencyActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
        getCurrentLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION
                && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                FancyToast.makeText(getApplicationContext(), "Permission denied.", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
            }
        }
    }

    private void getCurrentLocation() {
        Log.d("*********", "d5al Current loc");
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
        }
        LocationServices.getFusedLocationProviderClient(EmergencyActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(EmergencyActivity.this)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            Log.d("d5al lo la", "");
                            int lateastLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(lateastLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(lateastLocationIndex).getLongitude();

                            Log.d("*********LATITUDE", String.valueOf(latitude));
                            Log.d("*********LONGITUDE", String.valueOf(longitude));

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong (Location location)
    {
        Log.d("d5al fetch loc","");
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    void sendSOS(String patientName, String medicalProvider, String loc)
    {
        //medical provider name -- patient name -- location
        Map<String, Object> p = new HashMap<>();
        p.put("medicalprovider", medicalProvider);
        p.put("name", patientName);
        p.put("loc", loc);

        db.collection("Emergency").document(UID.getUid()).set(p)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        FancyToast.makeText(getApplicationContext(),"SOS sent.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("Emergency Failed", String.valueOf(e));
                    }
                });
    }

    void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        FancyToast.makeText(getApplicationContext(),"Message Sent Emergency Contact.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
    }

    public boolean isNetworkAvailable()
    {
        try
        {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (manager != null)
            {
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }

        catch (NullPointerException e){ return false;}
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(EmergencyActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }


    private class AddressResultReceiver extends ResultReceiver
    {
        AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult (int resultCode, Bundle resultData)
        {
            Log.d("d5al class address","");
            if (resultCode == Constants.SUCCESS_RESULT)
            {
                Log.d("++++++++++++++Address",resultData.getString(Constants.RESULT_DATA_KEY));
                loc = resultData.getString(Constants.RESULT_DATA_KEY);

                EmergencyContact = sos.getter_EmergencyContact();
                patientName = sos.getter_patientName();
                medicalProvider = sos.getter_MedicalProvider();

                msg = "Patient "+patientName+" is having an emergency case in "+loc;
                Log.e("msg", msg);

                Log.e("Emer con", EmergencyContact);
                Log.e("Patient name", patientName);
                Log.e("Medc pro", medicalProvider);

                sendSOS(patientName, medicalProvider, loc);
                sendSMS(EmergencyContact,msg);
            }

            else
            {
                Log.d("++++++++++++++Error",resultData.getString(Constants.RESULT_DATA_KEY));
            }
        }

    }
}

