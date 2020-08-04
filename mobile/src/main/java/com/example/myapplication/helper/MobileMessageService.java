package com.example.myapplication.helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.EmergencyActivity;
import com.example.myapplication.HomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MobileMessageService  extends WearableListenerService {

    public static final String TAG = "MobileMessageService";
    private  SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db;
    private Map<String, Object> userHashMap;

    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/my_path")) {

            //Registering Shared Preferences
            sharedPref = getSharedPreferences("BACKUP", MODE_PRIVATE);
            editor = sharedPref.edit();
            final String message = new String(messageEvent.getData());
     //       Log.e(TAG, "Message received : " + message);
            char flag = message.charAt(0);
            final String newMessage;
            switch (flag) {
                case 'B':

                    Intent homeActivity = new Intent(this, HomeActivity.class);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(homeActivity);
                    break;
                case 'H':
                    newMessage = message.substring(1); //Truncating the flag "H" and adding the value to the shared pref..
               //     Log.e(TAG, "Current HR value is: " + newMessage);
                    editor.putString("HR", newMessage).apply();


                    break;
                case 'S':
                    newMessage = message.substring(1);  //Truncating the flag "S" and adding the value to the shared pref..
                    editor.putString("Steps", newMessage).apply();
                    break;

                case 'E':
                    //Start Emergency Activity...
                    Intent emergencyActivity = new Intent(this, EmergencyActivity.class);
                    emergencyActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    emergencyActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(emergencyActivity);
                    break;
                case 'U':
                    userHashMap = new HashMap<>();
                    newMessage = message.substring(1);
                    RetrieveData(sharedPref.getString("UID", "NULL"));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            insertSteps(newMessage);
                        }
                    }, 20 * 1000);
                    break;


                default  :
                    Log.e(TAG, "Undefined.." );

            }

        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }


    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String time =(dateFormat.format(date));
        return time;
    }



    private void RetrieveData(String UID) {
        db.collection("Patient").document(UID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userHashMap = (Map<String, Object>) documentSnapshot.getData().get("steps");
                        Log.e(TAG, "Retrieved data:"+userHashMap);
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


    private void insertSteps(String newMessage) {

        db = FirebaseFirestore.getInstance();
        String time = getTime();
        userHashMap.put(time,newMessage);
        new Thread(new Runnable() {
            public void run() {
                db.collection("Patient").document(sharedPref.getString("UID","NULL")).update("steps",userHashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.e(TAG, "Success!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                e.printStackTrace();
                            }
                        });
            }
        }).start();
    }




}
