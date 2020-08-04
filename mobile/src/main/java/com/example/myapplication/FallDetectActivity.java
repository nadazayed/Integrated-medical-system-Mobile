package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FallDetectActivity extends AppCompatActivity implements SensorEventListener
{
    Sensor sensor,sensor_main;
    SensorManager sm;

    double sum;
    boolean min,max;
    int i,count;

    private TextView heading;

    String response_mode = "High(1 minute)";
    long response_time = 60000;

    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_main=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        count = 0;
        heading= findViewById(R.id.heading);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong("Response",response_time);
        editor.commit();

        heading.setText("Once the app is running it will trace your movements" +
                " \n If a fall is suspected you will be given an option to confirm you are OK by cancelling an SMS alert \n If you" +
                        "are not cancelling the SMS alert in a predefined time then an sms will be send to the number you selected along with your geographical Location");
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        sum = Math.round(Math.sqrt(Math.pow(event.values[0], 2)
                + Math.pow(event.values[1], 2)
                + Math.pow(event.values[2], 2)));
        Log.e("sum", String.valueOf(sum));
        // total.setText("Total::" + sum);
        Log.e("Standard Gravity" , String.valueOf(sm.STANDARD_GRAVITY));

        if (sum <= 5.0)
        {
            min = true;
            Log.e("min", "true");
        }

        if (min == true)
        {
            i++;
            if (sum >= 16.5)
            {
                max = true;
                Log.e("max", "true");
            }
        }

        if (min == true && max == true)
        {
            heading.setText("Suspected Fall");
            sm.unregisterListener(this);
//            Toast.makeText(this, "Suspected Fall", Toast.LENGTH_SHORT).show();
            Intent test= new Intent(FallDetectActivity.this, FallCheckActivity.class);
            startActivityForResult(test,2);

            min = false;
            max = false;

            if (count > 25)
            {
                Log.e("Fall accepted","");
                Toast.makeText(this,"Fall Confirmed",Toast.LENGTH_LONG).show();
                i = 0;
                count=0;
                min = false;
                max = false;
            }
            else
            {
                Log.e("Fall rejected","");
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public void back(View view)
    {
        startActivity(new Intent(FallDetectActivity.this, HomeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            int message=data.getIntExtra("count_value", 0);
            Log.e("request code = 2", String.valueOf(message));
            String msg=String.valueOf(message);
            Log.e("Result is",msg);
            if(message>25)
            {
                Intent alert_intent=new Intent(FallDetectActivity.this,FallHandleActivity.class);
                startActivity(alert_intent);
            }
            else
            {
                heading.setText("You're fine <3");
                Log.e("did","nth");
            }
        }
    }
}
