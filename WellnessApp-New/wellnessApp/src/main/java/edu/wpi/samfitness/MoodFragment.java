/**
 * MoodFragment.java
 * Sam Fitness
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

package edu.wpi.samfitness;

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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import edu.wpi.wellnessapp.R;

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
                        "Cancel", "OK", saveMoodRunnable(), Utils.emptyRunnable());
            }
        });

        graphView.addSeries(moodDataSeries);

        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(20);

        Date now = new Date();
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(now.getTime() - 6 * 24 * 60 * 60 * 1000);
        graphView.getViewport().setMaxX(now.getTime());

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(100);

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

        updateGraphData();
        checkMoodAchievements();

        return view;
    }

    public Runnable saveMoodRunnable() {
        return new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), "Saved " + ratingBar.getRating(), Toast.LENGTH_LONG).show();

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
                int date = Integer.valueOf(dateFormat.format(calendar.getTime()));

                DatabaseHandler db = new DatabaseHandler(getActivity());
                db.addMood(new MoodTic(String.valueOf(date), ratingBar.getRating()));
                Utils.todaysMoodScore = db.getTodaysMoodAvg(date);
                updateGraphData();
                checkMoodAchievements();
                ratingBar.setRating(0.0F);
            }
        };
    }

    private void updateGraphData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);

        DatabaseHandler db = new DatabaseHandler(getActivity());

        Date now = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);

        float day1 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day1 = Utils.map(day1, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day2 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day2 = Utils.map(day2, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day3 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day3 = Utils.map(day3, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day4 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day4 = Utils.map(day4, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day5 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day5 = Utils.map(day5, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day6 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day6 = Utils.map(day6, 0.0F, 5.0F, 0.0F, 100.0F);
        calendar.add(Calendar.HOUR, -24);

        float day7 = db.getTodaysMoodAvg(Integer.valueOf(dateFormat.format(calendar.getTime())));
        day7 = Utils.map(day7, 0.0F, 5.0F, 0.0F, 100.0F);

        // Check to see if there is no data yet
        // For the jjoe64 graphview workaround
        if (day1 == 0.0F && day2 == 0.0F && day3 == 0.0F && day4 == 0.0F && day5 == 0.0F && day6 == 0.0F && day7 == 0.0F) {
            moodDataSeries.resetData(new DataPoint[]{
                    new DataPoint((now.getTime() - 6 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 5 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint((now.getTime() - 4 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 3 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint((now.getTime() - 2 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 1 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint(now.getTime(), 1)});

            moodDataSeries.setThickness(0);
            moodDataSeries.setColor(Color.WHITE);
        } else {
            moodDataSeries.resetData(new DataPoint[]{
                    new DataPoint((now.getTime() - 6 * 24 * 60 * 60 * 1000), day7),
                    new DataPoint((now.getTime() - 5 * 24 * 60 * 60 * 1000), day6),
                    new DataPoint((now.getTime() - 4 * 24 * 60 * 60 * 1000), day5),
                    new DataPoint((now.getTime() - 3 * 24 * 60 * 60 * 1000), day4),
                    new DataPoint((now.getTime() - 2 * 24 * 60 * 60 * 1000), day3),
                    new DataPoint((now.getTime() - 1 * 24 * 60 * 60 * 1000), day2),
                    new DataPoint(now.getTime(), day1)});

            moodDataSeries.setThickness(5);
            moodDataSeries.setColor(Color.argb(255, 0, 255, 0));
        }

    }

    private void checkMoodAchievements() {
        DatabaseHandler db = new DatabaseHandler(getActivity());

        float pastWeekMood = Utils.map(db.getWeeksMoodAvg(), 0.0F, 5.0F, 0.0F, 100.0F);

        Log.d("MoodFrag", "Checking mood achievements... Weeks Total: " + pastWeekMood);

        if (pastWeekMood >= 70.0F) {
            Utils.unlockAchievement(6, getActivity());
        }

        if (pastWeekMood >= 75.0F) {
            Utils.unlockAchievement(7, getActivity());
        }

        if (pastWeekMood >= 80.0F) {
            Utils.unlockAchievement(8, getActivity());
        }

        if (pastWeekMood >= 85.0F) {
            Utils.unlockAchievement(9, getActivity());
        }

        if (pastWeekMood >= 90.0F) {
            Utils.unlockAchievement(10, getActivity());
        }

        if (pastWeekMood >= 95.0F) {
            Utils.unlockAchievement(11, getActivity());
        }
    }

    private void startMoodTracking() {
        Intent intent = new Intent(getActivity(), MoodAlertService.class);
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


