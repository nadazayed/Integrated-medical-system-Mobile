package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText edemail;
    String email;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
    }

    void reset()
    {
        edemail = findViewById(R.id.email);
        email = edemail.getText().toString().trim();

        if (email.isEmpty())
        {
            FancyToast.makeText(getApplicationContext(),"Invalid Email Address..",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        }
        else
        {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        FancyToast.makeText(getApplicationContext(),"Password reset successfully",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                    }
                    else
                    {
                        FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.reset:
                reset();
        }
    }
}
