/**
 * SleepFragment.java
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

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class SleepFragment extends Fragment {
    // UI Elements
    private Button startButton;
    private Button stopButton;
    private TextView trackingStatus;
    private TextView lightSensorValue;
    private TextView audioValue;
    private TextView sleepTime;
    private TextView wakeTime;
    private TextView calLight;
    private TextView calSound;

    // Raw Sensor Data
    private int audioAmplitude;
    private float lightIntensity;

    // Calibrated Sensor Data
    private int calibratedLight;
    private int calibratedAmplitude;

    // Final Sleep Times
    private String fallAsleepTime;
    private String wakeUpTime;

    // Calibration
    private final int CALIBRATE_TIME = 10;
    private boolean isCalibrated = false;
    private CountDownTimer calibrateTimer;

    // Tracking Statuses
    private boolean isTracking = false;
    private boolean isAsleep = false;

    private final BroadcastReceiver recieveFromSleepService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("SensorData")) {
                Bundle extras = intent.getExtras();

                String maxAmplitudeIn = extras.getString("maxAmplitude");
                String lightIntensityIn = extras.getString("lightIntensity");

                audioValue.setText("Audio Value: " + maxAmplitudeIn);
                lightSensorValue.setText("Light Sensor Value: " + lightIntensityIn);

                audioAmplitude = Integer.parseInt(maxAmplitudeIn);
                lightIntensity = Float.parseFloat(lightIntensityIn);

                checkSleepStatus();
            }
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        IntentFilter intentFilter = new IntentFilter("SensorData");
        getActivity().getApplicationContext().registerReceiver(recieveFromSleepService, intentFilter);

        // Initialize UI elements
        trackingStatus = (TextView) view.findViewById(R.id.textViewTrackingStatus);
        lightSensorValue = (TextView) view.findViewById(R.id.textViewLightSensorValue);
        audioValue = (TextView) view.findViewById(R.id.textViewAudioValue);
        sleepTime = (TextView) view.findViewById(R.id.textViewSleepTime);
        wakeTime = (TextView) view.findViewById(R.id.textViewWakeTime);
        calLight = (TextView) view.findViewById(R.id.textViewCalLight);
        calSound = (TextView) view.findViewById(R.id.textViewCalAudio);

        // Set the current tracking status
        if (isTracking) {
            trackingStatus.setText("Tracking...");
        } else {
            trackingStatus.setText("Not Tracking...");
        }

        // Start Button
        this.startButton = (Button) view.findViewById(R.id.startSleepButton);
        this.startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startSleepTracking();
            }
        });

        // Stop Button
        this.stopButton = (Button) view.findViewById(R.id.stopSleepButton);
        this.stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSleepTracking();
            }
        });
        stopButton.setEnabled(false);

        return view;
    }

    public void startSleepTracking() {
        // Start service
        getActivity().startService(new Intent(getActivity(), SleepService.class));
        Log.d("SleepFragment", "Starting sleep service");

        isCalibrated = false;
        isTracking = true;

        displayDialog();

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        trackingStatus.setText("Tracking...");
        sleepTime.setText("Fell Asleep: ");
        wakeTime.setText("Woke Up: ");
        calLight.setText("Calibrated Light: ");
        calSound.setText("Calibrated Audio: ");



        calibrateSensors();
    }

    public void stopSleepTracking() {
        getActivity().stopService(new Intent(getActivity(), SleepService.class));
        Log.d("SleepFragment", "Stopping sleep service");

        //display
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        isTracking = false;
        trackingStatus.setText("Not Tracking...");
        lightSensorValue.setText("Light Sensor Value: ");
        audioValue.setText("Audio Value: ");
    }

    /**
     * displayDialog()
     *
     * Displays an alert informing the user
     * how sleep tracking works
     */
    private void displayDialog() {

        // Instantiate the AlertDialog Builder
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        // Set the alert attributes
        alert.setTitle("Sleep Tracking Started!");
        alert.setMessage("This app uses the light sensor and microphone to track your sleeping " +
                "patterns. Make sure sure not to keep your device too far away from while you sleep for best results.");

        // Define positive button response
        alert.setPositiveButton(R.string.sleep_track_alert_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Just close the dialog
                    }
                });

        // Set the icon of the alert
        alert.setIcon(android.R.drawable.ic_dialog_alert);

        // Show the alert to the user
        alert.show();
    }

    //calibrate the light/sound levels by getting avgs (get values every 1sec for 10sec period)
    private void calibrateSensors() {
        isCalibrated = true;
        calibratedLight = 0;
        calibratedAmplitude = 0;

        calibrateTimer = new CountDownTimer((CALIBRATE_TIME * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                calibratedLight += lightIntensity;
                calibratedAmplitude += audioAmplitude;
            }

            public void onFinish() {
                //calculate avgs
                calibratedLight = calibratedLight / CALIBRATE_TIME - 1;
                calibratedAmplitude = calibratedAmplitude / CALIBRATE_TIME - 1;
                calLight.setText("Calibrated Light: " + Integer.toString(calibratedLight));
                calSound.setText("Calibrated Audio: " + Integer.toString(calibratedAmplitude));
                Log.d("SleepMonitor", "Calibrated sensors");
                calibrateTimer.cancel();
            }
        }.start();
    }

    private void checkSleepStatus() {
        // If sound & light are calibrated, check to see if user is sleeping
        if (isCalibrated) {

            // Check to see if user fell asleep
            if (!isAsleep && (lightIntensity < calibratedLight && audioAmplitude < calibratedAmplitude)) {
                Log.d("SleepMonitor", "Fell Asleep:" + fallAsleepTime);

                isAsleep = true;
                fallAsleepTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                sleepTime.setText("Fell Asleep: " + fallAsleepTime);
            }

            // Check to see if user woke up
            if (isAsleep && (lightIntensity > calibratedLight || audioAmplitude > calibratedAmplitude)) {
                Log.d("SleepMonitor", "Woke Up:" + fallAsleepTime);

                isAsleep = false;
                wakeUpTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                wakeTime.setText("Woke Up: " + wakeUpTime);
            }
        }
    }
}