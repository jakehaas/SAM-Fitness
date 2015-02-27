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
    private TextView duration;

    // Raw Sensor Data
    private int audioAmplitude;
    private float lightIntensity;

    // Calibrated Sensor Data (defaults set if left uncalibrated)
    private float calibratedLight = 14;
    private int calibratedAmplitude = 200;
    private int calibratedSleepHour = 8;
    private int calibratedWakeHour = 11;

    // Final Sleep Times
    private String fallAsleepTime;
    private String wakeUpTime;
    private int sleepHour;
    private int sleepMin;
    private int sleepSec;
    private String sleepAmPm;
    private int wakeHour;
    private int wakeMin;
    private int wakeSec;
    private String wakeAmPm;

    // Calibration
    private final int CALIBRATE_TIME = 10;
    private boolean isCalibrated = false;
    private CountDownTimer calibrateTimer;
    private float avgBy = 0;

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
        duration = (TextView) view.findViewById(R.id.textViewDuration);

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
        calibratedLight = 0;
        calibratedAmplitude = 0;
        avgBy = -1; //first light value is 0 and needs to be disregarded

        calibrateTimer = new CountDownTimer((CALIBRATE_TIME * 1000), 1000) {
            public void onTick(long millisUntilFinished) {
                //add up total values for averaging
                calibratedLight += lightIntensity;
                calibratedAmplitude += audioAmplitude;
                avgBy++;
            }

            public void onFinish() {
                //calculate avgs
                calibratedLight = Math.round(calibratedLight / avgBy);
                calibratedAmplitude = calibratedAmplitude / Math.round(avgBy);

                //after averages are calculated, add in some margin of noise/light
                calibratedLight += 15;
                calibratedAmplitude += 150;

                calLight.setText("Calibrated Light: " + Float.toString(calibratedLight));
                calSound.setText("Calibrated Audio: " + Integer.toString(calibratedAmplitude));
                Log.d("SleepMonitor", "Calibrated sensors");
                isCalibrated = true;
                calibrateTimer.cancel();
            }
        }.start();
    }

    private void checkSleepStatus() {
        // If sound & light are calibrated, check to see if user is sleeping


        if (isCalibrated) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            String amPm = getAmPm();

            // Check all conditions to see if user fell asleep
            if (!isAsleep && SleepHourCheck(hour, amPm) && SleepLightCheck() && SleepAudioCheck()) {
                Log.d("SleepMonitor", "Fell Asleep:" + fallAsleepTime);

                isAsleep = true;

                fallAsleepTime = getTime('S');
                sleepTime.setText("Fell Asleep: " + fallAsleepTime);
            }

            // Check to see if user woke up
            if (isAsleep && (!SleepHourCheck(hour, amPm) || !SleepLightCheck() || !SleepAudioCheck())) {
                Log.d("SleepMonitor", "Woke Up:" + fallAsleepTime);

                isAsleep = false;

                wakeUpTime = getTime('W');
                wakeTime.setText("Woke Up: " + wakeUpTime);

                duration.setText("Duration: " + getDuration());


            }
        }
    }

    //get the time in HH:MM:SS format, takes in a char if the sleep/wake variables need to be reset
    private String getTime(char set){
        Log.d("getTime", "Getting current time");
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        Log.d("CurrentTime", Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(seconds) + getAmPm());

        //if the new sleep time is gotten, set it globally
        if(set == 'S') {
            sleepHour = hour;
            sleepMin = minute;
            sleepSec = seconds;
            sleepAmPm = getAmPm();
            Log.d("SetSleepTime", Integer.toString(sleepHour) + ":" + Integer.toString(sleepMin) + ":" + Integer.toString(sleepSec) + sleepAmPm);
        }

        //if the new wake time is gotten, set it globally
        if(set == 'W'){
            wakeHour = hour;
            wakeMin = minute;
            wakeSec = seconds;
            wakeAmPm = getAmPm();
            Log.d("SetWakeTime", Integer.toString(wakeHour) + ":" + Integer.toString(wakeMin) + ":" + Integer.toString(wakeSec) + wakeAmPm);
        }

        return Integer.toString(hour) + ":" + Integer.toString(minute) + ":" + Integer.toString(seconds) + getAmPm();
    }

    //Check to see if time of day AM or PM
    private String getAmPm(){
        Calendar c = Calendar.getInstance();
        int am_pm = c.get(Calendar.AM_PM);
        String amPm;

        if(am_pm == 0)
            amPm = "AM";
        else
            amPm = "PM";

        return amPm;
    }

    //Check to see if hour is between valid sleeping hours
    private boolean SleepHourCheck(int hour, String amPm){
        if((hour >= calibratedSleepHour && amPm.equals("PM")) || (hour <= calibratedSleepHour && amPm.equals("AM"))){
            return true;
        }
        return false;
    }

    //check to see if light is below valid level
    private boolean SleepLightCheck(){
        if(lightIntensity < calibratedLight){
            return true;
        }
       return false;
    }

    //check to see if audio is below valid level
    private boolean SleepAudioCheck(){
        if(audioAmplitude < calibratedAmplitude){
            return true;
        }
        return false;
    }

    //gets duration of sleep to wake time
    private String getDuration(){
        int hourDuration;
        int minDuration;
        int secDuration;

        //both AM or both PM: simply subtract
        if((sleepAmPm.equals("PM") && wakeAmPm.equals("PM")) || (sleepAmPm.equals("AM") && wakeAmPm.equals("AM"))){
            hourDuration = Math.abs(sleepHour - wakeHour);
            minDuration = Math.abs(sleepMin - wakeMin);
            secDuration = Math.abs(sleepSec - wakeSec);
        }
        //crossed over midnight: have to take day change into account
        else {
            hourDuration = (12 - sleepHour) + wakeHour;
            minDuration = (60 - sleepMin) + wakeMin;
            secDuration = (60 - sleepSec) + wakeSec;

            //add appropriate minute/second conversions
            if (secDuration >= 60) {
                secDuration -= 60;
                minDuration += 1;
            }

            if (minDuration >= 60) {
                minDuration -= 60;
                hourDuration += 1;
            }
        }

        //make sure a full hour/minute has changed
        if((sleepSec >= wakeSec) && minDuration == 1){
            minDuration--;
            secDuration = (60 - sleepSec) + wakeSec;
        }
        if((sleepMin >= wakeMin) && hourDuration == 1){
            hourDuration--;
            minDuration = (60 - sleepMin) + wakeMin;
        }

        return Integer.toString(hourDuration) + ":" + Integer.toString(minDuration) + ":" + Integer.toString(secDuration);
    }
}