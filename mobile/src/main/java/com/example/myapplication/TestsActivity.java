package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestsActivity extends AppCompatActivity implements View.OnClickListener
{

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser UID;

    TextInputEditText viw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        UID = mAuth.getCurrentUser();

        viw = findViewById(R.id.viw);

        getTests ();
    }

    public void getTests ()
    {
        db.collection("Patient").document(UID.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if (document.getData().containsKey("tests"))
                        {
                            Log.e("EXIST","TESTS");
                            String all = String.valueOf(document.getData().get("tests"));
                            String trim = all.substring(1,(all.length()-1));
                            String each [] = trim.split(",");
                            for (int i=0; i<each.length; i++)
                            {
                                String data[] = each[i].split("=");
                                String name = data[0];
                                String desc = data[1];

                                viw.setText("Name: "+name+"\n"+"desc: "+desc +"\n\n");
                            }
                        }
                    } else { Log.d("getTests", "No such document"); }
                } else { Log.d("getTests", "get failed with ", task.getException()); }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(TestsActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
