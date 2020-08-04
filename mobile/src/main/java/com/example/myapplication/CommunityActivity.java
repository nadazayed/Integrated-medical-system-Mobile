package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;

public class CommunityActivity extends AppCompatActivity implements View.OnClickListener
{
    CardView cardview;
    LinearLayout.LayoutParams layoutparams;
    TextView textview1, textview2;
    LinearLayout layout, split;
    Context context;
    EditText qusestion;
    String a, q;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Firebase def
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

         context = getApplicationContext();

        layout = (LinearLayout) findViewById(R.id.linear);

        quest ();
    }

    void add()
    {
        qusestion = findViewById(R.id.qus);
        final String qus = qusestion.getText().toString();

        Map<String, Object> p = new HashMap<>();
        p.put("question", qus);

        db.collection("Community").add(p)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                FancyToast.makeText(getApplicationContext(),"Posted.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                qusestion.setText("");
                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public void quest ()
    {
        db.collection("Community")
                .orderBy("question").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.e("doc", document.getId() + " => " + document.getData());
                                // {question=x, answer=}
                                String question = String.valueOf(document.getData().get("question"));

                                String answer = String.valueOf(document.getData().get("answer")); //whole answers
                                answer = answer.substring(1,answer.length()-1); // {}
                                answer = answer.replaceAll("=",":");

                                String each [] = answer.split("//");
                                String ans = "";
                                if (each.length>1)
                                {
                                  Log.e("length", String.valueOf(each.length));

                                for (int i=0; i<each.length; i++)
                                    {
                                        String temp = each[i].replaceAll("//,","");
                                        ans = ans + "\n\n" +temp;
                                    }

                                    CreateCardViewProgrammatically(question,ans);
                                }
                                else
                                {
                                    answer = answer.replaceAll("//,","");
                                    CreateCardViewProgrammatically(question,answer);
                                }

                            }
                        }

                        else {Log.w("ERR", "Error getting documents.", task.getException());}

                    }
                });
    }

    public void CreateCardViewProgrammatically(String q, String ans)
    {
        layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(10, 0, 10, 20);

        split = new LinearLayout(context);
        split.setOrientation(LinearLayout.VERTICAL);
        split.setLayoutParams(layoutparams);

        cardview = new CardView(context);
        cardview.setLayoutParams(layoutparams);
        cardview.setRadius(15);
        cardview.setPadding(50, 5, 50, 5);
        cardview.setBackgroundResource(R.drawable.stroke_card);
        cardview.setMaxCardElevation(30);
        cardview.setMaxCardElevation(6);


        textview1 = new TextView(context);
        textview1.setLayoutParams(layoutparams);
        textview1.setText(q);
        textview1.setTypeface(null, Typeface.BOLD);
        textview1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textview1.setTextColor(Color.WHITE);
        textview1.setPadding(25,5,25,5);
        textview1.setGravity(Gravity.CENTER);

        split.addView(textview1);


        textview2 = new TextView(context);
        textview2.setLayoutParams(layoutparams);
        textview2.setText(ans);
        textview2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textview2.setTextColor(Color.WHITE);
        textview2.setPadding(25,5,25,5);
        textview2.setGravity(Gravity.CENTER);

        split.addView(textview2);


        cardview.addView(split);

        layout.addView(cardview);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.post:
                add();
                break;

            case R.id.back:
                Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
