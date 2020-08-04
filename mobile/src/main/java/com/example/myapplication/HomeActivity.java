package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.helper.FeaturedAdapter;
import com.example.myapplication.helper.FeaturedHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private int SMS_PERMISSION_CODE = 1;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    String name ;

    TextView nameView, dateView;
    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        if (isNetworkAvailable())
        {
            //Firebase
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            UID = mAuth.getCurrentUser();
        }




        //SharedPref for current user
        pref = getApplicationContext().getSharedPreferences("BACKUP", 0); // 0 - for private mode
        editor = pref.edit();



        //Hooks
        nameView = (TextView)findViewById(R.id.name);
        featuredRecycler = findViewById(R.id.featured_recycler);
        featuredRecycler();

        //Function call
        getDetails();
        isSmsPermissionGranted();
        requestReadAndSendSmsPermission();




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

    private void featuredRecycler ()
    {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();
        featuredLocations.add(new FeaturedHelperClass(R.drawable.heart,"Heart Rate", "Heart monitor.\nare essential to guarantee better health."));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.steps,"Steps Count", "Steps count.\nare essential to guarantee better health."));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.sleep,"Sleep Analysis", "Sleep cycle.\nare essential to guarantee better health."));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.medcs,"Medication", "Medications.\nare essential to guarantee better health."));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.drive,"Drive Mode", "Speed limit.\nare essential to guarantee better health."));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.fall,"Fall Detection", "Falls Monitor.\nare essential to guarantee better health."));

        adapter = new FeaturedAdapter(featuredLocations, HomeActivity.this);
        featuredRecycler.setAdapter(adapter);
    }

    public void getDetails()
    {
        db.collection("Patient").document(UID.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        name = (String) document.getData().get("name");
                        nameView.setText(""+name.toUpperCase());


                    }
                    else
                    {
                        Log.d("getEmergencyContact", "No such document");
                    }
                }
                else
                {
                    Log.d("getEmergencyContact", "get failed with ", task.getException());
                }
            }
        });
    }


    public void isSmsPermissionGranted()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            Log.e("--------","perm2");
            ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.SEND_SMS}, 1);
        }

    }

    private void requestReadAndSendSmsPermission()
    {
        //Request runtime SMS permission
        Log.d("--------","perm3");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.profile:
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                break;

            case R.id.logout:
            {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                break;
            }

            case R.id.contact:
                startActivity(new Intent(HomeActivity.this,ContactActivity.class));
                break;

            case R.id.community:
                startActivity(new Intent(HomeActivity.this,CommunityActivity.class));
                break;

            case R.id.sos:
                startActivity(new Intent(HomeActivity.this,EmergencyActivity.class));
                break;

            case R.id.tests:
                startActivity(new Intent(HomeActivity.this,TestsActivity.class));
                break;

            case R.id.procedures:
                startActivity(new Intent(HomeActivity.this,ProceduresActivity.class));
                break;

            case R.id.allergy:
                startActivity(new Intent(HomeActivity.this,AllergyActivity.class));
                break;

            case R.id.nearme:
                startActivity(new Intent(HomeActivity.this,GoogleMapsActivity.class));
                break;
        }
    }


}
