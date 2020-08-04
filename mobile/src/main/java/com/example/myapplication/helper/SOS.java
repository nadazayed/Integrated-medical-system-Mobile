package com.example.myapplication.helper;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SOS extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;
    String uid;

    String MedicalProvider, EmergencyContact, Patient_name, location, msg;

    public SOS()
    {
        Log.d("----", "d5al");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();
        uid = UID.getUid();

        getEmergencyContact();
    }

    public void getEmergencyContact()
    {
        db.collection("Patient").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        EmergencyContact = (String) document.getData().get("emergencycontact");
                        Patient_name = (String) document.getData().get("name");
                        location = (String) document.getData().get("address");
                        Log.d("-----------EMERGENCY CONTACT", EmergencyContact);
                        MedicalProvider = String.valueOf(document.getData().get("medicalprovider"));

                        msg = "WeCare\n" + Patient_name + " is Having Emergency at";
                    } else {
                        Log.d("getEmergencyContact", "No such document");
                    }
                } else {
                    Log.d("getEmergencyContact", "get failed with ", task.getException());
                }
            }
        });
    }

    public String getter_EmergencyContact()
    {
        return EmergencyContact;
    }
    String getter_msg()
    {
        return msg;
    }
    public String getter_patientName()
    {
        return Patient_name;
    }
    public String getter_MedicalProvider()
    {
        return MedicalProvider;
    }

}
