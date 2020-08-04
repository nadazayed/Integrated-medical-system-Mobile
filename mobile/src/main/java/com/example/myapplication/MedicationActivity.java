package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MedicationActivity extends AppCompatActivity
{
    TextView name, dose, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        name = (TextView)findViewById(R.id.name);
        dose = (TextView)findViewById(R.id.dose);
        desc = (TextView)findViewById(R.id.desc);

        Log.e("NAME",getIntent().getStringExtra("NAME"));
        Log.e("DOSE", String.valueOf(getIntent().getIntExtra("DOSE",0)));
        Log.e("DESC",getIntent().getStringExtra("DESC"));

        name.setText(getIntent().getStringExtra("NAME"));
        dose.setText(String.valueOf(getIntent().getIntExtra("DOSE",0)));
        desc.setText(getIntent().getStringExtra("DESC"));
    }
}
