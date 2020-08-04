package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MedicalInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;
    LinearLayout pregnancy;

    String medicalprovider, bloodtype, medcs;
    EditText emergency_contact, reg_height, reg_weight;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_info);

        pregnancy = (LinearLayout) findViewById(R.id.linear2);
        sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();
        editor = sharedPref.edit();
        editor.putString("UID",UID.getUid()).commit();
        medcs = getIntent().getStringExtra("EXTRA_SESSION_MEDICALPROVIDER");
        String arr[] = medcs.split("-");
        String arr2[] = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};

        Log.d("------------",medcs);

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
        medicalprovider = String.valueOf(spinner.getSelectedItem());
        editor.putString("MedicalProvider",medicalprovider).commit();


        // Spinner element
        Spinner spinner2 = (Spinner) findViewById(R.id.bloodType);
        // Spinner click listener
        spinner2.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr2);
        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner2.setAdapter(dataAdapter2);
        bloodtype = String.valueOf(spinner2.getSelectedItem());




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (String.valueOf(parent).contains("bloodType"))
        {
            bloodtype = parent.getItemAtPosition(position).toString();
            Log.d("+++++++++++++++++","blood type");
            Log.d("+++++++++++++++++",bloodtype);
        }
        else if (String.valueOf(parent).contains("medicalProvider"))
        {
            medicalprovider = parent.getItemAtPosition(position).toString();
            Log.d("+++++++++++++++++","medical provider");
            Log.d("+++++++++++++++++",medicalprovider);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0)
    {
        // TODO Auto-generated method stub
    }

    private void fill ()
    {
        Log.d("00000000000000","d5al el fill");

        String status1="false", status2="false";
        emergency_contact = findViewById(R.id.emergency_contact);
        reg_height = findViewById(R.id.reg_height);
        reg_weight = findViewById(R.id.reg_weight);

        final String weight = reg_weight.getText().toString().trim();
        final String height = reg_height.getText().toString().trim();
        final String emergency = emergency_contact.getText().toString().trim();


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

        Map<String, Object> p = new HashMap<>();
        p.put("emergencycontact", emergency);
        p.put("smoker", status1);
        p.put("pregnant", status2);
        p.put("medicalprovider", medicalprovider);
        p.put("bloodtype", bloodtype);
        p.put("weight",weight);
        p.put("height",height);

        db.collection("Patient").document(UID.getUid()).update(p)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("GOOOD", "DocumentSnapshot successfully written!");

                        medcs = getIntent().getStringExtra("EXTRA_SESSION_MEDICALPROVIDER");
                        Intent intent = new Intent(MedicalInfoActivity.this, BlockScreen.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w("BAAAAAD", "Error writing document", e);
                    }
                });


    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.reg_btn:
                fill();
                startActivity(new Intent(MedicalInfoActivity.this,HomeActivity.class)); //<< BLOCK
                break;
        }
    }
}
