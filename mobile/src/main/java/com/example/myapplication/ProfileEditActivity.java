package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;
    EditText nameView, phoneView, emergencycontactView, addressView, weightView, heightView;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    String status1, status2, medicalprovider,temp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        temp = getIntent().getStringExtra("EXTRA_SESSION_MEDICALPROVIDER");
        Log.d("+++++++",temp);
        String arr[] = temp.split("-");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.medicalProvider);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
        editor = sharedPref.edit();

//        medicalprovider = String.valueOf(spinner.getSelectedItem());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (String.valueOf(parent).contains("medicalProvider"))
        {
            medicalprovider = parent.getItemAtPosition(position).toString();
            Log.d("+++++++++++++++++","medical provider");
            Log.d("+++++++++++++++++",medicalprovider);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0)
    {
        FancyToast.makeText(getApplicationContext(),"You must choose Medical Provider",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
    }

    void addDetails()
    {
        Log.d("00000000000000","d5al el add Details");
        nameView = findViewById(R.id.name);
        phoneView = findViewById(R.id.phone);
        emergencycontactView = findViewById(R.id.emergencycontact);
        addressView = findViewById(R.id.address);
        weightView = findViewById(R.id.weight);
        heightView = findViewById(R.id.height);

        final String name = nameView.getText().toString().trim();
        final String phone = phoneView.getText().toString().trim();
        final String emergency = emergencycontactView.getText().toString().trim();
        final String address = addressView.getText().toString().trim();
        final String weight = weightView.getText().toString().trim();
        final String height = heightView.getText().toString().trim();

        Switch switch1 = (Switch) findViewById(R.id.switch1);
        Switch switch2 = (Switch) findViewById(R.id.switch2);

        if (switch1.isChecked())
            status1 = switch1.getTextOn().toString();
        else
            status1 = switch1.getTextOff().toString();

        if (switch2.isChecked())
            status2 = switch2.getTextOn().toString();
        else
            status2 = switch2.getTextOff().toString();

        Log.d("^^",status1);
        Log.d("^^",status2);
        Log.d("^^",medicalprovider);

        Map<String, Object> p = new HashMap<>();
        if (!name.isEmpty()) {p.put("name", name);}
        if (!phone.isEmpty()) {p.put("phone", phone);}
        if (!emergency.isEmpty()) {p.put("emergencycontact", emergency);}
        if (!address.isEmpty()) {p.put("address", address);}
        if (!weight.isEmpty()) {p.put("weight",weight);}
        if (!height.isEmpty()) {p.put("height",height);}
        p.put("medical provider", medicalprovider);

        if (p.isEmpty())
            Log.d("P","EMpty");

        db.collection("Patient").document(UID.getUid()).update(p)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("GOOOD", "DocumentSnapshot successfully written!");

                        FancyToast.makeText(getApplicationContext(),"Information updated successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        startActivity(new Intent(ProfileEditActivity.this,HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(ProfileEditActivity.this,"Information is invalid.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.done:
                editor.putString("Name",nameView.getText().toString()).commit();
                editor.putString("EmergencyContact",emergencycontactView.getText().toString()).commit();
                addDetails();
        }
    }
}
