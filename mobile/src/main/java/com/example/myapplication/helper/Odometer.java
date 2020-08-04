package com.example.myapplication.helper;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class Odometer extends Activity {
    String FILENAME = "gpsspeedoOdom";

    public double getDistance() {
        double dist=0;

        try {
            FileInputStream fos = openFileInput(FILENAME);
            byte output[] = null; // convert to a long
            fos.read(output);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return dist;
    }

    public void saveDistance(Double dist) {


        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            long long_dist = Double.doubleToRawLongBits(dist);

            byte[] bArray = new byte[8];
            ByteBuffer bBuffer = ByteBuffer.wrap(bArray);
            LongBuffer lBuffer = bBuffer.asLongBuffer();
            lBuffer.put(0, long_dist);


            fos.write(bArray);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}