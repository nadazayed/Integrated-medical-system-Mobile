package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener
{
    FirebaseAuth mAuth;
    FirebaseUser UID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();
        UID = mAuth.getCurrentUser();

        verify();
    }

    void verify()
    {
        UID.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FancyToast.makeText(getApplicationContext(),"Verification email sent.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        }
                        else
                        {
                            FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        }
                    }
                });
    }

    void check()
    {
        UID = mAuth.getCurrentUser();
        UID.reload();

        if (UID.isEmailVerified())
        {
            startActivity(new Intent(VerifyActivity.this, PersonalInfoActivity.class));
        }
        else
        {
            FancyToast.makeText(getApplicationContext(),"Email is not verified yet.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.check:
                check();
        }
    }
}
