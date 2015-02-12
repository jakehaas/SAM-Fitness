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
 * Copyright (c) 2013, 2014. Wellness-App-MQP. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import java.io.IOException;

import edu.wpi.wellnessapp.AudioService;

import com.threed.jpct.Logger;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.inputmethodservice.InputMethodService;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SleepFragment extends Fragment {

    private Button achivTestButton;
    private Button startButton;
    private Button stopButton;

    private TextView trackingStatus;
    private TextView lightSensorValue;
    private TextView audioValue;
    private boolean isTracking = false;

    private Context ctx;

    SensorManager sensorMgr = null;
    Sensor lightSensor = null;

    boolean mStartRecording = true;

    private final BroadcastReceiver recieveMessageFromService = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();

            String maxAmplitude = extras.getString("maxAmplitude");

            audioValue.setText("Audio Value: " + maxAmplitude);

            Log.d("maxAmplitude", maxAmplitude);

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

        //display
        displayDialog();
        isTracking = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        trackingStatus.setText("Tracking...");

        //light
        //sensorMgr.registerListener(lightlsn, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //vibrate
        Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);

        //audio clip
        Log.d("onClick:", "Starting service");
        //ctx.startService(new Intent(ctx, AudioService.class));
        getActivity().startService(new Intent(getActivity(), AudioService.class));

        //audioService.onRecord(mStartRecording);
        //audioValue.setText("Audio Value: " + Integer.toString(mRecorder.getMaxAmplitude()));
    }

    //stop sleep tracking
    public void stopSleepTracking(View view) {

        getActivity().stopService(new Intent(getActivity(), AudioService.class));

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        //display
        isTracking = false;
        trackingStatus.setText("Not Tracking...");
        lightSensorValue.setText("Light Sensor Value: ");
        audioValue.setText("Audio Value: ");

        //vibrate
        Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);

        //audio
        //mStartRecording = false;
        Log.d("onClick:", "Stopping service");
        //audioService.onRecord(mStartRecording);
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