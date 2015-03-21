/**
 * SleepService.java
 * Wellness-App-MQP
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class SleepService extends Service {
    private static final int SAMPLE_RATE = 1000;

    MediaRecorder mRecorder;
    SensorManager sensorMgr = null;
    Sensor lightSensor = null;
    float lightIntensity;

    Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SleepService", "onCreate");

        // Create the calibrateTimer
        timer = new Timer();

        // Set up light sensor
        sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        lightSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Set up media recorder
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Utils.getAudioSampleFilePath(this));
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            lightIntensity = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onDestroy() {
        Log.d("SleepService", "onDestroy");

        // Alert the user
        Toast.makeText(this, "Sleep Tracking Stopped", Toast.LENGTH_LONG).show();

        // Stop the calibrateTimer
        timer.cancel();

        // Stop the audio recorder
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.d("SleepService", "onStartCommand");

        // Alert the user
        Toast.makeText(this, "Sleep Tracking Started", Toast.LENGTH_LONG).show();

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("SleepService", "MediaRecorder prepare() failed");
        }

        // Start the audio recorder
        mRecorder.start();

        // Start the light sensor
        sensorMgr.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Start sampling the sensors
        sampleSensors();

        // Don't want to auto restart the service
        return START_NOT_STICKY;
    }

    /**
     * sampleSensors()
     * Gets light and sound data from sensors at a fixed rate and sends broadcast with the values in
     * it (reveiced in the SleepFragment)
     */
    private void sampleSensors() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent("SensorData");
                i.putExtra("maxAmplitude", Integer.toString(mRecorder.getMaxAmplitude()));
                i.putExtra("lightIntensity", Float.toString(lightIntensity));
                sendBroadcast(i);

            }
        }, 0, SAMPLE_RATE);
    }
}

