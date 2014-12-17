/**
 * SleepFragment.java
 * Wellness-App-MQP
 * 
 * @version     1.0.0
 * 
 * @author      Jake Haas
 * @author	Evan Safford
 * @author	Nate Ford
 * @author	Haley Andrews
 * 
 * Copyright (c) 2013, 2014. Wellness-App-MQP. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SleepFragment extends Fragment {

    private Button achivTestButton;
    private Button startButton;
    private Button stopButton;
    
    private TextView trackingStatus;
    
    private boolean isTracking = false;
    
    private Context ctx;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View view = inflater.inflate(R.layout.fragment_sleep, container, false);
	ctx = getActivity().getApplicationContext();
	
	trackingStatus = (TextView) view.findViewById(R.id.textViewTrackingStatus);
	
	if (isTracking) {
	    trackingStatus.setText("Tracking...");
	} else {
	    trackingStatus.setText("Not Tracking...");
	}
	
	this.achivTestButton = (Button) view.findViewById(R.id.testButton);
	this.achivTestButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		new Achievement(v, 0);
	    }
	});
	
	this.startButton = (Button) view.findViewById(R.id.startSleepButton);
	this.startButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		startSleepTracking(v);
	    }
	});
	
	this.stopButton = (Button) view.findViewById(R.id.stopSleepButton);
	this.stopButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		stopSleepTracking(v);
	    }
	});

	return view;
    }
    
    public void startSleepTracking(View view) {
	displayDialog();
	
	isTracking = true;
	trackingStatus.setText("Tracking...");
	
	Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
	vibrator.vibrate(new long[]{100,10,100,1000},-1);
    }
    
    public void stopSleepTracking(View view) {
	isTracking = false;
	trackingStatus.setText("Not Tracking...");
	
	Vibrator vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
	vibrator.vibrate(new long[]{100,10,100,1000},-1);
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

}
