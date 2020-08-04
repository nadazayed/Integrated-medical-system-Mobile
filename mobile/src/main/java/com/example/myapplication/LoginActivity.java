package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    EditText log_email, log_pass;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        if (isNetworkAvailable())
        {
            //Firebase def
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            findViewById(R.id.Login).setOnClickListener(this);

            UID = FirebaseAuth.getInstance().getCurrentUser();
            if (UID != null && UID.isEmailVerified())
            {
                // User is signed in
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }

    }

    public boolean isNetworkAvailable()
    {
        try
        {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;

            if (manager != null)
            {
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }

        catch (NullPointerException e){ return false;}
    }

    private void LoginUser()
    {
        log_email = findViewById(R.id.etUsername);
        final String email = log_email.getText().toString().trim();

        log_pass = findViewById(R.id.etPassword);
        final String password = log_pass.getText().toString().trim();

        Log.d("","-------------registerUser");
        mAuth.signInWithEmailAndPassword(email.toLowerCase(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d("","-------------onComplete");
                if (task.isSuccessful())
                {
                    Log.d("","--------------Logged");
                    UID = mAuth.getCurrentUser();
                    UID.reload();
                    if (UID.isEmailVerified())
                    {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }

                    else
                    {
                        startActivity(new Intent(LoginActivity.this, VerifyActivity.class));
                    }
                }
                else
                {
                    FancyToast.makeText(getApplicationContext(),task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.Login:
                if (isNetworkAvailable())
                {LoginUser();}
                else {FancyToast.makeText(getApplicationContext(),"No Internet Connection.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show(); }
                break;

            case R.id.Signup:
                if (isNetworkAvailable())
                {startActivity(new Intent(LoginActivity.this,SignupActivity.class));}
                else {FancyToast.makeText(getApplicationContext(),"No Internet Connection.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show(); }
                break;

            case R.id.buttonForget:
                if (isNetworkAvailable())
                {startActivity(new Intent(LoginActivity.this,ForgetActivity.class));}
                else {FancyToast.makeText(getApplicationContext(),"No Internet Connection.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show(); }
                break;
        }
    }
}
