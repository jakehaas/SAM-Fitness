/**
 * MainFragment.java
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
import android.hardware.Sensor;
import android.hardware.SensorManager;
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

    private Button achivTestButton;
    private Button startButton;
    private Button stopButton;

    private TextView trackingStatus;
    private TextView lightSensorValue;
    private TextView audioValue;
    private TextView sleepTime;
    private TextView wakeTime;
    private TextView calLight;
    private TextView calSound;

    private Context ctx;

    SensorManager sensorMgr = null;
    Sensor lightSensor = null;

    private int amplitude;
    private float lightIntensity;
    private int calibratedLight;
    private int calibratedAmplitude;
    private final int CALIBRATE_TIME = 10;
    private String fallAsleepTime;
    private String wakeUpTime;

    boolean mStartRecording = true;
    CountDownTimer timer;
    private boolean isAsleep = false;
    private boolean isCalibrated = false;
    private boolean isTracking = false;

    private final BroadcastReceiver recieveMessageFromService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();

            String maxAmplitudeIn = extras.getString("maxAmplitude");
            String lightIntensityIn = extras.getString("lightIntensity");

            audioValue.setText("Audio Value: " + maxAmplitudeIn);
            lightSensorValue.setText("Light Sensor Value: " + lightIntensityIn);

            amplitude = Integer.parseInt(maxAmplitudeIn);
            lightIntensity = Float.parseFloat(lightIntensityIn);

            //Log.d("maxAmplitude", maxAmplitudeIn);
            //Log.d("lightIntensity", lightIntensityIn);

            //if sound & light are calibrated, check to see if sleeping is occuring
            if(isCalibrated) {

                //check to see if fell asleep
                if (!isAsleep && (lightIntensity < calibratedLight && amplitude < calibratedAmplitude)) {
                    isAsleep = true;
                    fallAsleepTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("Fell Asleep:", fallAsleepTime);

                    sleepTime.setText("Fell Asleep: " + fallAsleepTime);
                }

                //check to see if woke up
                if (isAsleep && (lightIntensity > calibratedLight || amplitude > calibratedAmplitude)) {
                    isAsleep = false;
                    wakeUpTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    Log.d("Woke Up:", fallAsleepTime);

                    wakeTime.setText("Woke Up: " + wakeUpTime);
                }
            }
            if(!isCalibrated){ //calibrate the light/sound levels by getting avgs (get values every 1sec for 10sec period)
                isCalibrated = true;
                calibratedLight = 0;
                calibratedAmplitude = 0;

                timer = new CountDownTimer((CALIBRATE_TIME*1000), 1000){
                    public void onTick(long millisUntilFinished) {
                        calibratedLight += lightIntensity;
                        calibratedAmplitude += amplitude;
                        Log.d("AddLight", "AddLight " + Float.toString(lightIntensity) + " = " + Float.toString(calibratedLight));
                        Log.d("AddAmp", "AddAmp " + Integer.toString(amplitude) + " = " + Integer.toString(calibratedAmplitude));
                    }
                    public void onFinish(){
                        //calculate avgs
                        Log.d("TotalLight","Total Light: " + Float.toString(calibratedLight));
                        Log.d("TotalAmp", "Total Amp " + Integer.toString(calibratedAmplitude));
                        calibratedLight = calibratedLight / CALIBRATE_TIME-1;
                        calibratedAmplitude = calibratedAmplitude / CALIBRATE_TIME-1;
                        calLight.setText("Calibrated Light: " + Integer.toString(calibratedLight));
                        calSound.setText("Calibrated Audio: " + Integer.toString(calibratedAmplitude));
                        Log.d("Calibrated.", "Calibrated.");
                        timer.cancel();
                    }
                }.start();
            }
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        ctx = getActivity().getApplicationContext();

        IntentFilter intentFilter = new IntentFilter("NewMessage");
        ctx.registerReceiver(recieveMessageFromService, intentFilter);

        sensorMgr = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

        trackingStatus = (TextView) view.findViewById(R.id.textViewTrackingStatus);
        lightSensorValue = (TextView) view.findViewById(R.id.textViewLightSensorValue);
        audioValue = (TextView) view.findViewById(R.id.textViewAudioValue);
        sleepTime = (TextView) view.findViewById(R.id.textViewSleepTime);
        wakeTime = (TextView) view.findViewById(R.id.textViewWakeTime);
        calLight = (TextView) view.findViewById(R.id.textViewCalLight);
        calSound = (TextView) view.findViewById(R.id.textViewCalAudio);

        //checks to see if the app was previously tracking sleep when opened
        if (isTracking) {
            trackingStatus.setText("Tracking...");
        } else {
            trackingStatus.setText("Not Tracking...");
        }

        //achievement button test
        this.achivTestButton = (Button) view.findViewById(R.id.testButton);
        this.achivTestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("NewServiceMessage");
                ctx.sendBroadcast(i);
            }
        });

        //Start Button
        this.startButton = (Button) view.findViewById(R.id.startSleepButton);
        this.startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startSleepTracking(v);
            }
        });

        //Stop Button
        this.stopButton = (Button) view.findViewById(R.id.stopSleepButton);
        this.stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSleepTracking(v);
            }
        });
        stopButton.setEnabled(false);
        return view;
    }

    //begins sleep tracking
    public void startSleepTracking(View view)
    {
        mStartRecording = true;
        isCalibrated = false;

        //display
        displayDialog();
        isTracking = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        trackingStatus.setText("Tracking...");
        sleepTime.setText("Fell Asleep: ");
        wakeTime.setText("Woke Up: ");
        calLight.setText("Calibrated Light: ");
        calSound.setText("Calibrated Audio: ");

        //vibrate
        //Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        //vibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);

        //start service
        Log.d("onClick:", "Starting service");
        getActivity().startService(new Intent(getActivity(), AudioService.class));
    }

    //stop sleep tracking
    public void stopSleepTracking(View view) {

        getActivity().stopService(new Intent(getActivity(), AudioService.class));
        Log.d("onClick:", "Stopping service");

        //display
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        isTracking = false;
        trackingStatus.setText("Not Tracking...");
        lightSensorValue.setText("Light Sensor Value: ");
        audioValue.setText("Audio Value: ");

        //vibrate
        //Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        //vibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
    }

    /**
     * displayDialog()
     *
     * Displays an alert informing the user that they
     * earned a new achievement
     */
    private void displayDialog() {

        // Instantiate the AlertDialog Builder
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        // Set the alert attributes
        alert.setTitle("Tracking Started!");
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

    //light sensor
    /*
    SensorEventListener lightlsn = new SensorEventListener() {
    	
		@Override
		public void onSensorChanged(SensorEvent event) {
		    lightIntensity = event.values[0];
		    if(isTracking){
		    	lightSensorValue.setText("Light Sensor Value: " + Float.toString(lightIntensity));
		    	//audioValue.setText("Audio Value: " + Integer.toString(getAudioLevel()));
		    }
		    else{
		    	lightSensorValue.setText("Light Sensor Value: ");
		    	audioValue.setText("Audio Value: ");
		    }
		    // displays light value in real time
		}
	
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // TODO Auto-generated method stub
		}
    };
    */

    /*public void checkSleepStartTime() {
        if (lightIntensity < 11 || f < 30) {
            String timeString = Utils.getTimeString();
        }
    }*/

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }
}