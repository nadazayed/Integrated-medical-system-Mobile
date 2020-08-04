package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.shashank.sony.fancytoastlib.FancyToast;
import com.example.myapplication.helper.GMailSender;
public class ContactActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText etContent, etEmail;
    String etRecipient;
    Button btnSend;

    @Override

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.
                Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        etEmail = (EditText) findViewById(R.id.etEmail);  // Taking user Mail
        etContent = (EditText) findViewById(R.id.etContent);  // Taking user msg
        etRecipient = "spprtwecare@gmail.com";
        btnSend = (Button) findViewById(R.id.btnSend);
        flag:  btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }

        });

    }

    private void sendMessage()
    {
        GMailSender sender = new GMailSender();

        try {
            sender.sendPlainTextEmail(
                    "587",
                    "smtp.gmail.com",
                    "spprtwecare@gmail.com",
                    "00000wecare",
                    "spprtwecare@gmail.com",
                    "WeCare Client Problem",
                    "Email: "+etEmail.getText().toString()+"\n"+etContent.getText().toString());

            FancyToast.makeText(this,"Message Sent, We will get back to you soon",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                Intent intent = new Intent(ContactActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
