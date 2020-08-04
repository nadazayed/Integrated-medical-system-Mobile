package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText log_email, log_pass;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    List<String> list = new ArrayList<String>();
    String medcs = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Firebase def
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Layout def
        findViewById(R.id.Signup).setOnClickListener(this);
    }

    private void RegisterUser()
    {
        log_email = findViewById(R.id.log_email);
        final String email = log_email.getText().toString().trim();

        log_pass = findViewById(R.id.log_pass);
        final String password = log_pass.getText().toString().trim();

        Log.d("","-------------registerUser");
        mAuth.createUserWithEmailAndPassword(email.toLowerCase(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.d("","-------------onComplete");
                if (task.isSuccessful())
                {
                    Log.d("","--------------Registered");
//                    UID = mAuth.getCurrentUser();

                    Intent intent = new Intent(SignupActivity.this, VerifyActivity.class);
                    startActivity(intent);
                }
                else
                {
                    FancyToast.makeText(getApplicationContext(),"Email address already exist. Try again!",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.Signup:
                RegisterUser();
                break;

            case R.id.Login:
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                break;
        }
    }
}
