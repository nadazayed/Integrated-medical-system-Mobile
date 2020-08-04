package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class FallHandleActivity extends AppCompatActivity
{
    long response_mode,resp_time;
    int timer_enabled;

    CountDownTimer timer;
    TextToSpeech t1;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall);

        heading= findViewById(R.id.heading);

        timer_enabled=0;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        response_mode=sharedpreferences.getLong("Response", 00);
        resp_time=response_mode/60000;

        if(response_mode==0.0f)
        {
            Toast.makeText(this,"Error in reading",Toast.LENGTH_LONG).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                            // this function will generate the voice message

                            speakout();
                        }
                    }
                });
            }
        }, 10000);

        timer = new CountDownTimer(response_mode, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                heading.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds) + "  CLICK TO CANCEL REQUEST");

                heading.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        timer.cancel();
                        heading.setText("Request Cancelled");
                        timer_enabled=1;

                    }
                });
            }

            @Override
            public void onFinish()
            {
                Intent alert_intent=new Intent(FallHandleActivity.this,EmergencyActivity.class);
                startActivity(alert_intent);
            }
        }.start();
    }

    public void speakout()
    {
        Log.e("Speech", "In speech module");
        t1.speak("A fall have been detected.Please cancel the SMS alert if you are OK ", TextToSpeech.QUEUE_FLUSH, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { }

        else { t1.speak("A fall have been detected.Please cancel the SMS alert if you are OK ", TextToSpeech.QUEUE_FLUSH, null); }
    }

    @Override
    public void onBackPressed()
    {
        if(timer_enabled==1)
        { super.onBackPressed(); }

        else { Toast.makeText(getApplicationContext(),"Please cancel the Emergency request to return to main menu ",Toast.LENGTH_LONG).show(); }
    }
}
