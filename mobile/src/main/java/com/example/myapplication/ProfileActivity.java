package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    EditText nameView, phoneView, emergencycontactView, addressView, weightView, heightView, smokerView, pregnantView, medicalView;

    List<String> list = new ArrayList<String>();
    String medcs = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        Retrieve();

        nameView = (EditText)findViewById(R.id.name);
        phoneView = (EditText)findViewById(R.id.phone);
        emergencycontactView = (EditText)findViewById(R.id.emergencycontact);
        addressView = (EditText)findViewById(R.id.address);
        weightView = (EditText)findViewById(R.id.weight);
        heightView = (EditText)findViewById(R.id.height);
        smokerView = (EditText)findViewById(R.id.smoker);
        pregnantView = (EditText)findViewById(R.id.pregnant);
        medicalView = (EditText) findViewById(R.id.medicalProvider);

        sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
        editor = sharedPref.edit();


        getDetails();
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
                        nameView.setText(String.valueOf(document.getData().get("name")));
                        phoneView.setText(String.valueOf(document.getData().get("phone")));
                        addressView.setText(String.valueOf(document.getData().get("address")));
                        weightView.setText(String.valueOf(document.getData().get("weight")));
                        heightView.setText(String.valueOf(document.getData().get("height")));
                        emergencycontactView.setText(String.valueOf(document.getData().get("emergencycontact")));
                        smokerView.setText(String.valueOf(document.getData().get("smoker")));
                        pregnantView.setText(String.valueOf(document.getData().get("pregnant")));
                        medicalView.setText(String.valueOf(document.getData().get("medicalprovider")));
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

    private void Retrieve()
    {
        db.collection("MedicalProvider")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d("Success", " => " + document.getData());
                                list.add((String) document.getData().get("name"));
                            }
                            extract();
                        }

                        else
                        {
                            Log.w("Error", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void extract()
    {
        for (int i=0; i<list.size(); i++)
        {
            medcs+= list.get(i)+"-";
        }
        Log.d("*************", medcs);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                editor.putString("Name",nameView.getText().toString()).commit();
                editor.putString("EmergencyContact",emergencycontactView.getText().toString()).commit();
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                break;

            case R.id.edit:
                editor.putString("Name",nameView.getText().toString()).commit();
                editor.putString("EmergencyContact",emergencycontactView.getText().toString()).commit();
                Intent intent2 = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                intent2.putExtra("EXTRA_SESSION_MEDICALPROVIDER", medcs);
                startActivity(intent2);
        }
    }
}
