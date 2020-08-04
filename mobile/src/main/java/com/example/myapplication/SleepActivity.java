package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SleepActivity extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    TextView less_txt, optimal_txt, healthy_txt, recommended_txt, more_txt, now_txt;

    DateFormat simpleFormat = new SimpleDateFormat("HH:mm");
    String time,less,optimal,healthy,recommended,more;

    Date today = Calendar.getInstance().getTime();

    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    String date = df.format(today);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        less_txt = (TextView)findViewById(R.id.less_txt);
        optimal_txt = (TextView)findViewById(R.id.optimal_txt);
        healthy_txt = (TextView)findViewById(R.id.healthy_txt);
        recommended_txt = (TextView)findViewById(R.id.recommended_txt);
        more_txt = (TextView)findViewById(R.id.more_txt);
        now_txt = (TextView)findViewById(R.id.now_txt);

        setTime();
    }

    void setTime()
    {
        simpleFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        time = simpleFormat.format(today);

        Log.e("TIME",simpleFormat.format(today));
        now_txt.setText(simpleFormat.format(today));

        LESS(0);
        OPTIMAL(0);
        HEALTHY(0);
        REC(0);
        MORE(0);
    }

    void LESS (int x)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 6);
        Date today = calendar.getTime();
        less = simpleFormat.format(today);

        Log.e("Less 6hrz",less);

        String temp[] = less.split(":");

        if (Integer.parseInt(temp[0]) >12)
        {
            int t = Integer.parseInt(temp[0]) -12;
            if (t < 10)
                less_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                less_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }
        else
        {
            int t = Integer.parseInt(temp[0]);
            if (t < 10)
                less_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                less_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        if (x == 1)
        {
            setReminder (Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),6);
        }

    }


    void OPTIMAL (int x)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 7);
        Date today = calendar.getTime();
        optimal = simpleFormat.format(today);

        Log.e("Optimal 7hrz",simpleFormat.format(today));

        String temp[] = optimal.split(":");

        if (Integer.parseInt(temp[0]) >12)
        {
            int t = Integer.parseInt(temp[0]) -12;
            if (t < 10)
                optimal_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                optimal_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        else
        {
            int t = Integer.parseInt(temp[0]);
            if (t < 10)
                optimal_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                optimal_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        if (x == 1)
        {
            setReminder (Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),7);
        }
    }

    void HEALTHY (int x)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 8);
        Date today = calendar.getTime();
        healthy = simpleFormat.format(today);

        Log.e("Healthy 8hrz",simpleFormat.format(today));

        String temp[] = healthy.split(":");

        if (Integer.parseInt(temp[0]) >12)
        {
            int t = Integer.parseInt(temp[0]) -12;
            if (t < 10)
                healthy_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                healthy_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }
        else
        {
            int t = Integer.parseInt(temp[0]);
            if (t < 10)
                healthy_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                healthy_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        if (x == 1)
        {
            setReminder (Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),8);
        }
    }

    void REC (int x)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 9);
        Date today = calendar.getTime();
        recommended = simpleFormat.format(today);

        Log.e("Recommended 9hrz",simpleFormat.format(today));

        String temp[] = recommended.split(":");

        if (Integer.parseInt(temp[0]) >12)
        {
            int t = Integer.parseInt(temp[0]) -12;
            if (t < 10)
                recommended_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                recommended_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        else
        {
            int t = Integer.parseInt(temp[0]);
            if (t < 10)
                recommended_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                recommended_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }

        if (x == 1)
        {
            setReminder (Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),9);
        }
    }

    void MORE (int x)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 10);
        Date today = calendar.getTime();
        more = simpleFormat.format(today);
        Log.e("More 10hrz",simpleFormat.format(today));
        String temp[] = more.split(":");

        if (Integer.parseInt(temp[0]) >12)
        {
            int t = Integer.parseInt(temp[0]) -12;
            if (t < 10)
                more_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
            more_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }
        else
        {
            int t = Integer.parseInt(temp[0]);
            if (t < 10)
                more_txt.setText("0"+t+":"+Integer.parseInt(temp[1]));
            else
                more_txt.setText(t+":"+Integer.parseInt(temp[1]));
        }
        if (x == 1)
        {
            setReminder (Integer.parseInt(temp[0]), Integer.parseInt(temp[1]),10);
        }
    }

    public void setReminder (int hr, int min, int num)
    {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hr);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, min);
        startActivity(intent);

        addSleep(num);
        FancyToast.makeText(getApplicationContext(),"Sleep tight now :)",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
    }

    public void addSleep(int num)
    {
        db.collection("Patient").document(UID.getUid()).update(("sleep."+date),num)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("GOOOD", "DocumentSnapshot successfully written!");
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(SleepActivity.this, HomeActivity.class);
                startActivity(intent);
                break;

            case R.id.less:
                LESS(1);
                break;

            case R.id.optimal:
                OPTIMAL(1);
                break;

            case R.id.healthy:
                HEALTHY(1);
                break;

            case R.id.recommended:
                REC(1);
                break;

            case R.id.more:
                MORE(1);
                break;
        }

    }
}
