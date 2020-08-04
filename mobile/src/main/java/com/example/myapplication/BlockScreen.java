package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.helper.SliderAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BlockScreen extends AppCompatActivity implements View.OnClickListener
{
    SharedPreferences pref;

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayer;
    private SliderAdapter slideAdapter;
    protected Handler myHandler;
    TextView textview;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_screen);
        sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
        editor = sharedPref.edit();

        pref = getApplicationContext().getSharedPreferences("BACKUP", 0); // 0 - for private mode

        findViewById(R.id.btn).setOnClickListener(this);
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayer = (LinearLayout) findViewById(R.id.dotsLayout);

        slideAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(slideAdapter);

        textview = findViewById(R.id.textview);
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });

        String message = "Block"+","+sharedPref.getString("PatientName","Empty Name")+","+sharedPref.getString("UID","Empty UID")+","+sharedPref.getString("MedicalProvider","Empty MedicalProvider");
        textview.setText(message);
        new NewThread("/my_path", message).start();
        Log.e("Block Screen", "Successfully sent: "+message );

    }

    public void messageText(String newinfo) {
        if (newinfo.compareTo("") != 0) {
            textview.append("\n" + newinfo);
        }
    }

    public void sendmessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn:
                String message = "Block"+","+sharedPref.getString("PatientName","Empty Name")+","+sharedPref.getString("UID","Empty UID")+","+sharedPref.getString("MedicalProvider","Empty MedicalProvider");
                new NewThread("/my_path", message).start();
                Log.e("Block Screen", "Successfully sent: "+message );
                break;
        }
    }

    class NewThread extends Thread {
        String path;
        String message;

        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(BlockScreen.this).sendMessage(node.getId(), path, message.getBytes());

                    try {
                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage("");

                    } catch (ExecutionException exception) {

                        //TO DO: Handle the exception//


                    } catch (InterruptedException exception) {
                    }

                }

            } catch (ExecutionException exception) {

                //TO DO: Handle the exception//

            } catch (InterruptedException exception) {

                //TO DO: Handle the exception//
            }

        }
    }
}
