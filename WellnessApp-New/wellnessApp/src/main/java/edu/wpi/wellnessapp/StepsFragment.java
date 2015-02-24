/**
 * StepsFragment.java
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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class StepsFragment extends Fragment {

    private SensorManager sensorManager;

    // Values to calculate number of steps
    private float previousX;
    private float currentX;

    private float previousY;
    private float currentY;

    private float previousZ;
    private float currentZ;
    private int idCounter;
    private int numSteps;
    private int threshold;
    private int tmpMinutes;
    GraphView graphView;
  //  private List<GraphViewData> dataArray = new ArrayList<GraphViewData>();
    LineGraphSeries<DataPoint> exampleSeries;


    private TextView textViewSteps;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

    	View view = inflater.inflate(R.layout.fragment_step, container, false);
    	DatabaseHandler db = new DatabaseHandler(getActivity());




    	//final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(getActivity());
    	Calendar c = Calendar.getInstance();
    	tmpMinutes = c.get(Calendar.MINUTE);

    	previousX = 0;
    	currentX = 0;

    	previousY = 0;
    	currentY = 0;

    	previousZ = 0;
    	currentZ = 0;

    	numSteps = 0;
    	threshold = 7;
    	idCounter = 1;
    	textViewSteps = (TextView) view.findViewById(R.id.textSteps);

        graphView = new GraphView(getActivity());
    	graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
	    graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
	    graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
	    graphView.getGridLabelRenderer().setTextSize(20);

	    graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
	        @Override
	        public String formatLabel(double value, boolean isValueX) {
	        if (isValueX) {
	        	SimpleDateFormat hoursDateFormat = new SimpleDateFormat("h:mm");
            	return hoursDateFormat.format(value);

	        	//return dateTimeFormatter.format(new Date((long) value*1000));
	        } else {
	            // show currency for y values
	            return super.formatLabel(value, isValueX);
	        }
	        }
	    });

	    exampleSeries = new LineGraphSeries<DataPoint>();
	    graphView.addSeries(exampleSeries); // data

        try {

        	RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.graph1);

        	layout.addView(graphView);
        }
        catch (NullPointerException e) {
    	    // something to handle the NPE.
        }

    	//populateGraphView(view, 0);
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

	    DatabaseHandler db = new DatabaseHandler(getActivity());
    	Calendar c = Calendar.getInstance();
    	int minutes = c.get(Calendar.MINUTE);
    	int hours = c.get(Calendar.HOUR);
    	StringBuilder fullDate = new StringBuilder();

    	if (minutes != tmpMinutes) {
    				long now = new Date().getTime();

    				fullDate.append(hours);
    				fullDate.append("-");
    				fullDate.append(tmpMinutes);
    				db.addStepsTaken(new StepsTaken(fullDate.toString(), numSteps));
    				StepsTaken stepsTakenObject = db.getStepsTaken(idCounter);

    				idCounter++;

    				exampleSeries.appendData(new DataPoint(now, stepsTakenObject.steps), false, 5);
    				numSteps = 0;
    				textViewSteps.setText(String.valueOf(numSteps));
    				tmpMinutes = minutes;


    	}


	    if ( Math.sqrt( ((currentX * currentX) + (currentY * currentY) + (currentZ * currentZ)) -
		 ((previousX * previousX) + (previousY * previousY) + (previousZ * previousZ)) ) >  threshold) {


	    		numSteps++;
				//populateGraphView(getView(), numSteps, minutes);
	    		textViewSteps.setText(String.valueOf(numSteps));
	    }


	    // Display the values

	    // Store the previous values
	    previousX = x;
	    previousY = y;
	    previousZ = z;

	    if (Utils.tryParseInt((String) textViewSteps.getText())) {
    	    	if (Integer.parseInt((String) textViewSteps.getText()) >= 10) {
    	    	    if (!AchievementList.UNLOCKED_FIRST_STEPS) {
    	    		new Achievement(getView(), 1);
    	    		AchievementList.UNLOCKED_FIRST_STEPS = true;
    	    	    }
    	    	}
	    }

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // empty - required by class
	}
    }; // ends private inner class sensorEventListener


}

