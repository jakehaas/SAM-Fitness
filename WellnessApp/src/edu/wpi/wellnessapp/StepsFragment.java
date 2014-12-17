/**
 * StepsFragment.java
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

//import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

/*import android.app.ActionBar.LayoutParams;
 import android.content.Context;
 import android.database.Cursor;
 import android.database.sqlite.SQLiteDatabase;
 */

public class StepsFragment extends Fragment {

    private SensorManager sensorManager;

    // Values to calculate number of steps
    private float previousX;
    private float currentX;
    
    private float previousY;
    private float currentY;
    
    private float previousZ;
    private float currentZ;
    
    private int numSteps;
    private int threshold;

    private TextView textViewSteps;
    
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

	// StepsTaken stepsTaken = db.getStepsTaken(2);
	// System.out.println(stepsTaken.toString());
	view = inflater.inflate(R.layout.fragment_step, container, false);
	
	previousX = 0;
	currentX = 0;
	
	previousY = 0;
	currentY = 0;
	
	previousZ = 0;
	currentZ = 0;
	
	numSteps = 0;
	threshold = 10;

	textViewSteps = (TextView) view.findViewById(R.id.textSteps);
	populateGraphView(view);
	enableAccelerometerListening();

	return view;
    }

    private void enableAccelerometerListening() {
	// Initialize the sensor manager
	sensorManager = (SensorManager) getActivity().getSystemService(
		Context.SENSOR_SERVICE);
	sensorManager.registerListener(sensorEventListener,
		sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Event handler for accelerometer events
    private SensorEventListener sensorEventListener = new SensorEventListener() {
	// Listens for change in acceleration, displays, and computes the steps
	public void onSensorChanged(SensorEvent event) {
	    // Gather the values from accelerometer
	    float x = event.values[0];
	    float y = event.values[1];
	    float z = event.values[2];

	    currentX = x;
	    currentY = y;
	    currentZ = z;
	    
	    if ( Math.sqrt( ((currentX * currentX) + (currentY * currentY) + (currentZ * currentZ)) - 
		 ((previousX * previousX) + (previousY * previousY) + (previousZ * previousZ)) ) >  threshold) {
		
		numSteps++;
		textViewSteps.setText(String.valueOf(numSteps));
	    }

	    /*
	    // Measure if a step is taken
	    if (Math.abs(currentX - previousX) > threshold || 
		Math.abs(currentY - previousY) > threshold || 
		Math.abs(currentZ - previousZ) > threshold) {
		
		numSteps++;
		textViewSteps.setText(String.valueOf(numSteps));
	    }*/

	    // // Measure if a step is taken
	    // if(Math.abs(currentY - previousY) > threshold){
	    // numSteps++;
	    // textViewSteps.setText(String.valueOf(numSteps));
	    // }

	    // Display the values

	    // Store the previous values
	    previousX = x;
	    previousY = y;
	    previousZ = z;
	    
	    if (Utils.tryParseInt((String) textViewSteps.getText())) {
    	    	if (Integer.parseInt((String) textViewSteps.getText()) >= 10) {
    	    	    if (!AchievementList.UNLOCKED_FIRST_STEPS) {
    	    		new Achievement(view, 1);
    	    		AchievementList.UNLOCKED_FIRST_STEPS = true;
    	    	    }
    	    	}
	    }
	    
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // empty - required by class
	}
    }; // ends private inner class sensorEventListener

    private void populateGraphView(View view) {

	// Cursor c = db.rawQuery("Select * from stepsTaken" , null);
	// String collumn = c.getString(1);
	DatabaseHandler db = new DatabaseHandler(getActivity());
	db.addStepsTaken(new StepsTaken("9-10-14", 3500));
	db.addStepsTaken(new StepsTaken("9-10-15", 3530));
	db.addStepsTaken(new StepsTaken("9-10-16", 3670));
	db.addStepsTaken(new StepsTaken("9-10-17", 4300));
	db.addStepsTaken(new StepsTaken("9-10-18", 2380));
	db.addStepsTaken(new StepsTaken("9-10-19", 3150));

	StepsTaken stepsTaken0 = db.getStepsTaken(1);
	StepsTaken stepsTaken1 = db.getStepsTaken(2);
	StepsTaken stepsTaken2 = db.getStepsTaken(3);
	StepsTaken stepsTaken3 = db.getStepsTaken(4);
	StepsTaken stepsTaken4 = db.getStepsTaken(5);
	StepsTaken stepsTaken5 = db.getStepsTaken(6);
	// System.out.println(stepsTaken.toString());
	GraphViewSeries exampleSeries = new GraphViewSeries(
		new GraphViewData[] { new GraphViewData(1, stepsTaken0.steps),
            			new GraphViewData(2, stepsTaken1.steps),
            			new GraphViewData(3, stepsTaken2.steps),
            			new GraphViewData(4, stepsTaken3.steps),
            			new GraphViewData(5, stepsTaken4.steps),
            			new GraphViewData(6, stepsTaken5.steps) });

	LineGraphView graphView = new LineGraphView(getActivity() // context
		, "Steps Taken\n" // heading
	);
	graphView.addSeries(exampleSeries); // data
	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
	graphView.setHorizontalLabels(new String[] { stepsTaken0.date,
		stepsTaken1.date, stepsTaken2.date, stepsTaken3.date,
		stepsTaken4.date, stepsTaken5.date });

	graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
	graphView.getGraphViewStyle().setTextSize(20);
	
	try {

	    RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.graph1);

	    layout.addView(graphView);
	} catch (NullPointerException e) {
	    // something to handle the NPE.
	}
    }

}
