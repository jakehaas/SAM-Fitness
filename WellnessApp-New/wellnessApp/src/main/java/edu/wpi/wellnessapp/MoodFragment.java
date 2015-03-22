/**
 * MoodFragment.java
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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MoodFragment extends Fragment {
    private Button startStopMoodButton;
    private Button setMoodButton;

    private GraphView graphView;
    LineGraphSeries<DataPoint> moodDataSeries;

    private Spinner alertHourSpinner;
    private float alertTime = 6.0F;

    private RatingBar ratingBar;

    private boolean isTracking = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moodDataSeries = new LineGraphSeries<DataPoint>();

        /*moodDataSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mood, container, false);

        isTracking = Utils.isServiceRunning(getActivity(), MoodAlertService.class);

        startStopMoodButton = (Button) view.findViewById(R.id.startMoodButton);
        setMoodButton = (Button) view.findViewById(R.id.setMoodButton);
        ratingBar = (RatingBar) view.findViewById(R.id.moodRatingBar);
        graphView = (GraphView) view.findViewById(R.id.moodGraph);
        alertHourSpinner = (Spinner) view.findViewById(R.id.alert_hour_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.mood_alert_times, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertHourSpinner.setAdapter(adapter);
        alertHourSpinner.setOnItemSelectedListener(new AlertTimeSelectedListener());
        alertHourSpinner.setSelection(3);

        if (isTracking) {
            startStopMoodButton.setText("Stop Mood Tracking");
            setMoodButton.setEnabled(true);
            ratingBar.setEnabled(true);
            alertHourSpinner.setEnabled(false);
        } else {
            startStopMoodButton.setText("Start Mood Tracking");
            setMoodButton.setEnabled(false);
            ratingBar.setEnabled(false);
            alertHourSpinner.setEnabled(true);
        }

        startStopMoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    stopMoodTracking();
                    startStopMoodButton.setText("Start Mood Tracking");
                } else {
                    startMoodTracking();
                    startStopMoodButton.setText("Stop Mood Tracking");
                }
            }
        });

        setMoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.displayDialog(getActivity(), "Save Mood Rating?", "Are you sure you want save " + ratingBar.getRating() + " as your current mood?",
                        "Cancel", "OK", saveRatingRunnable(), Utils.emptyRunnable());
            }
        });

        graphView.addSeries(moodDataSeries);

        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(20);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    SimpleDateFormat hoursDateFormat = new SimpleDateFormat("MMM dd");

                    return hoursDateFormat.format(value);
                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        return view;
    }

    public Runnable saveRatingRunnable() {
        return new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), "Saved " + ratingBar.getRating(), Toast.LENGTH_LONG).show();

                Calendar c = Calendar.getInstance();
                int date = c.get(Calendar.DATE);

                DatabaseHandler db = new DatabaseHandler(getActivity());
                db.addMood(new MoodTic(String.valueOf(date), ratingBar.getRating()));
                Utils.todaysMoodScore = db.getTodaysMoodAvg();
                ratingBar.setRating(0.0F);
            }
        };
    }

    private void updateGraphData() {

    }

    private void startMoodTracking() {
        Intent intent =  new Intent(getActivity(), MoodAlertService.class);
        getActivity().startService(intent);
        isTracking = true;
        setMoodButton.setEnabled(true);
        ratingBar.setEnabled(true);
        alertHourSpinner.setEnabled(false);
    }

    private void stopMoodTracking() {
        getActivity().stopService(new Intent(getActivity(), MoodAlertService.class));
        isTracking = false;
        setMoodButton.setEnabled(false);
        ratingBar.setEnabled(false);
        alertHourSpinner.setEnabled(true);
    }

    private class AlertTimeSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            MoodAlertService.NOTIFY_RATE = Integer.valueOf(parent.getItemAtPosition(pos).toString()) * 60000 * 60;
            Log.d("AlertTimeSelected", "User set new notify rate! -- " + MoodAlertService.NOTIFY_RATE);
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}


