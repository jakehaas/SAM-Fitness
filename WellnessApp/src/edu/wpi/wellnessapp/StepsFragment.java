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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


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
     
        /*graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                	Calendar c = Calendar.getInstance(); 
                	int minutes = c.get(Calendar.MINUTE);
                	int hours = c.get(Calendar.HOUR);
                	StringBuilder fullDate = new StringBuilder();
                	tmpMinutes = minutes;
                	fullDate.append(hours);
                	fullDate.append("-");
                	fullDate.append(tmpMinutes);
                	return fullDate.toString();
                }
                return null; // let graphview generate Y-axis label for us
            }
        });
        */
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
	//    graphView.setScrollable(true);
	//    graphView.setScalable(true);
    	
	    
	    
	    
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
    			
    			if (idCounter == 1) {
    		    	
    		    	
    		    /*	fullDate.append(hours);
    		    	fullDate.append("-");
    		    	fullDate.append(tmpMinutes);
    		     	db.addStepsTaken(new StepsTaken(fullDate.toString(), numSteps));
    		     	StepsTaken stepsTaken0 = db.getStepsTaken(idCounter);
    		    	*/	
    		     	
    		    //	dataArray.add(new GraphViewData(tmpMinutes, stepsTaken0.steps));
    		        exampleSeries = new LineGraphSeries<DataPoint>();
    		        exampleSeries.appendData(new DataPoint(tmpMinutes, /*stepsTaken0.steps*/ numSteps), false, 5);
    		        graphView.addSeries(exampleSeries); // data
    		
    		        try {

    		        	RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.graph1);

    		        	layout.addView(graphView);
    		        } 
    		        catch (NullPointerException e) {
    		    	    // something to handle the NPE.
    		        }
    		        numSteps = 1;
    		        tmpMinutes = minutes;
    		        idCounter++;
    		       
    			}
    			else{ 
    				/*fullDate.append(hours);
    				fullDate.append("-");
    				fullDate.append(tmpMinutes);
    				db.addStepsTaken(new StepsTaken(fullDate.toString(), numSteps));
    				StepsTaken stepsTaken0 = db.getStepsTaken(idCounter);  
    				*/  			
    				//exampleSeries.resetData(dataArray.toArray(new GraphViewData[idCounter]));
    				//graphView.setViewPort((tmpMinutes - 5), tmpMinutes);
    				idCounter++;
    			
    				exampleSeries.appendData(new DataPoint(tmpMinutes, /*stepsTaken0.steps*/ numSteps), false, 5);
    				//int viewPort = tmpMinutes - 5;
    				//graphView.setViewPort(viewPort, tmpMinutes);
    				//graphView.redrawAll();
    
    				numSteps = 1;
    				tmpMinutes = minutes;
    			}

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

    private void populateGraphView(View view, int numSteps, int minutes) {
    	DatabaseHandler db = new DatabaseHandler(getActivity());
    	
    	StepsTaken stepsTaken0 = db.getStepsTaken(idCounter);
  
    	System.out.println(stepsTaken0.steps);
    	stepsTaken0.setSteps(stepsTaken0.steps + numSteps);
    	System.out.println(stepsTaken0.steps);
    	db.updateStepsTaken(stepsTaken0);
    	
    	//exampleSeries.resetData(new GraphViewData[] { new GraphViewData(minutes, stepsTaken0.steps)});
    	
    }

}
