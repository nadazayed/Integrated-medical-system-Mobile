package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class HeartActivity extends AppCompatActivity implements View.OnClickListener
{

    protected Handler myHandler;
    protected TextView textview;
    private SharedPreferences sharedPref;
    public static final String path = "/my_path";
    public static final String TAG = "HeartActivity";
    private Map<String, Object> userHashMap;
    private FirebaseFirestore db;
    private String currentDate;
    private TextInputEditText history;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
        userHashMap = new HashMap<>();
        db = FirebaseFirestore.getInstance();
        getCurrentDate();
        textview = (TextView) findViewById(R.id.hr);
        history = (TextInputEditText) findViewById(R.id.history);
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                messageText(stuff.getString("messageText"));
                return true;
            }
        });




        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Doing a periodic timer for getting the most recent HR values..
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                new NewThread(path,"HeartRate").start();
               runOnUiThread(new Runnable() {

                                 @Override
                                 public void run() {
                                     textview.setText(sharedPref.getString("HR",""));
                                    // Log.e(TAG, "HR: "+sharedPref.getString("HR","NULL"));
                                 }
                             });

            }
        }, 0, 3000);


    }


    @Override
    protected void onResume() {
        super.onResume();
        RetrieveData(sharedPref.getString("UID","NULL"));
        FancyToast.makeText(getApplicationContext(),"Please wait..",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                history.setText("");
                history.setTextSize(25);

                if(userHashMap.size() == 0) {
                    history.setText("No readings available.");
                }


                for(Map.Entry i : userHashMap.entrySet()) {

                    if( (i.getKey()).toString().contains(currentDate)) {
                        Log.e(TAG, "" +i.getValue().toString());
                        history.append(i.getKey().toString()+"\t\t\t\t\t\t\t"+i.getValue().toString()+"\n");

                    }



                }

            }
        }, 5*1000);



    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                Intent intent = new Intent(HeartActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
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

    private void RetrieveData(String UID) {
        db.collection("Patient").document(UID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userHashMap = (Map<String, Object>) documentSnapshot.getData().get("hr");


                    }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                e.printStackTrace();
            }
        });
    }





   private void getCurrentDate() {
       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
       LocalDateTime now = LocalDateTime.now();
       currentDate=dtf.format(now);
       Log.e(TAG, "current date: "+currentDate);

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
                            Wearable.getMessageClient(HeartActivity.this).sendMessage(node.getId(), path, message.getBytes());

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
