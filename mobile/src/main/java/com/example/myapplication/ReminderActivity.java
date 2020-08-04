package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.helper.Alert;
import com.example.myapplication.helper.CancelAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    String temp ="";

    TextInputEditText viw;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        viw = findViewById(R.id.viw);

        getMedications ();
    }

    public void getMedications ()
    {
        db.collection("Patient").document(UID.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if (document.getData().containsKey("medication"))
                        {
                            Log.e("EXIST","MEDCS");
                            String allMedc = String.valueOf(document.getData().get("medication"));
                            String trimMedc = allMedc.substring(1,(allMedc.length()-1));
                            String eachMedc [] = trimMedc.split(",");
                            for (int i=0; i<eachMedc.length; i++)
                            {
                                //14=1=4=1=Take after launch
                                String data[] = eachMedc[i].split("=");
                                String name = data[0];
                                int days = Integer.parseInt(data[1]);
                                int times = Integer.parseInt(data[2]);
                                int time = Integer.parseInt(data[3]);
                                int dose = Integer.parseInt(data[4]);
                                String desc = data[5];
                                int curr = Integer.parseInt(data[6]);

                                setReminder(name, dose, desc, i, time, days, curr);
                                temp+= "Name: "+name+"\n"+"Days: "+days+"\n"+"Times: "+times+"\n"+"Time: "+time+"\n"+"Dose: "+dose+"\n"+"Desc: "+desc +"\n\n";
                            }

                            viw.setText(temp);
                        }
                        else
                        {
                            viw.setText("No Medications Available yet.");
                        }

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

    public void setReminder (String name, int dose, String desc, int id, int time, int days, int curr)
    {
        int date;
        Log.d("Hay8","DCM0");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time); //0 > 12   13 > 1
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 0);

        Intent alertIntent = new Intent(getApplicationContext(), Alert.class);
        alertIntent.putExtra("NAME", name);
        alertIntent.putExtra("DOSE", dose);
        alertIntent.putExtra("DESC", desc);
        alertIntent.putExtra("ID", id);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //DAILY
        Log.d("Hay8","DCM1");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(getApplicationContext(), id,
                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); //0 id for each notf


        //Remove after
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String formattedDate = df.format(c);
        System.out.println("Current date => " + formattedDate);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, time); //0 > 12   13 > 1
        if ((curr+days) > 30)
            date = (curr+days) - 30;
        else
            date = curr+days;
        calendar2.set(Calendar.DAY_OF_MONTH, (date));
        Log.e("FINISH", String.valueOf(date));
        calendar2.set(Calendar.MINUTE, 00);
        calendar2.set(Calendar.SECOND, 0);

        Intent alertIntent2 = new Intent(getApplicationContext(), CancelAlert.class);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        //DAILY
        Log.d("Hay8","DCM1");
        alarmManager2.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), PendingIntent.getBroadcast(getApplicationContext(), id,
                alertIntent2, PendingIntent.FLAG_UPDATE_CURRENT)); //0 id for each notf
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(ReminderActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }

}
