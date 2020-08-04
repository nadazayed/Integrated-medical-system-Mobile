package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener
{
    List<String> list = new ArrayList<String>();
    String medcs = "";

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    EditText reg_name, reg_address, reg_phoneNo;
    RadioButton genderradioButton;
    RadioGroup radioGroup;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    String dob;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        sharedPref = getSharedPreferences("BACKUP",MODE_PRIVATE);
        editor = sharedPref.edit();

        //Firebase def
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();




        Retrieve();

        mDisplayDate = (TextView) findViewById(R.id.tvDate);


        mDisplayDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PersonalInfoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("-----", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                dob = date;
                mDisplayDate.setText(date);
            }
        };
    }


    private void fill ()
    {
        Log.d("00000000000000","d5al el fill");

        reg_name = findViewById(R.id.reg_name);
        reg_address = findViewById(R.id.reg_address);
        reg_phoneNo = findViewById(R.id.reg_phoneNo);

         String name = reg_name.getText().toString().trim();

        editor.putString("PatientName",name).commit();

        final String addr = reg_address.getText().toString().trim();
        final String phone = reg_phoneNo.getText().toString().trim();

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        genderradioButton = (RadioButton) findViewById(selectedId);

        String gender = "";
        if(selectedId==-1)
        {
            FancyToast.makeText(getApplicationContext(),"Please select your gender",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        }
        else
        {
            gender = (String) genderradioButton.getText();
            editor.putString("Gender",gender).commit();

            Map<String, Object> p = new HashMap<>();
            Map<String, Object> hr = new HashMap<>();
            Map<String, Object> steps = new HashMap<>();

            p.put("name", name.toLowerCase());
            p.put("address", addr.toLowerCase());
            p.put("phone", phone);
            p.put("gender", gender.toLowerCase());
            p.put("dob",dob);
            p.put("hr",hr);
            p.put("steps",steps);

            db.collection("Patient").document(UID.getUid()).set(p)
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Log.d("GOOOD", "DocumentSnapshot successfully written!");

                            Intent intent = new Intent(PersonalInfoActivity.this, MedicalInfoActivity.class);
                            intent.putExtra("EXTRA_SESSION_MEDICALPROVIDER", medcs);
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
            case R.id.reg_btn:
                fill();
                break;
        }
    }
}
