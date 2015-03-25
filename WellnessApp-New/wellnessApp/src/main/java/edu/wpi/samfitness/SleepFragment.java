/**
 * SleepFragment.java
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import edu.wpi.wellnessapp.R;

public class SleepFragment extends Fragment {
    // UI Elements
    private Button calibrateButton;
    private Button stopStartButton;
    private Button moreSessionInfo;
    private TextView trackingStatus;
    private TextView todaysHours;
    private TextView todaysEfficiency;

    // Graph
    private GraphView graphView;
    LineGraphSeries<DataPoint> sleepDataSeries;

    // Raw Sensor Data
    private int audioAmplitude;
    private float lightIntensity;

    // Final Sleep Times
    private String fallAsleepTime = "";
    private String wakeUpTime = "";
    private int sleepHour;
    private int sleepMin;
    private String sleepAmPm;
    private int wakeHour;
    private int wakeMin;
    private String wakeAmPm;
    private float totalDuration = 0.0F;

    // Calibrated Sensor Data (defaults set if left uncalibrated)
    private float calibratedLight = 15;
    private int calibratedAmplitude = 300;
    private int calibratedSleepHour = 8;
    private int calibratedWakeHour = 12;

    // Calibration
    private final int CALIBRATE_TIME = 10;
    private final int NOISE_MARGIN = 300;
    private final int LIGHT_MARGIN = 10;
    private CountDownTimer calibrateTimer;
    private float avgBy = 0;

    // Tracking Statuses
    private boolean isTracking = true;
    private boolean isAsleep = false;
    private int numWakeups = 0;
    private int efficiency;

    private DatabaseHandler db;

    private final BroadcastReceiver recieveFromSleepService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("SensorData")) {
                Bundle extras = intent.getExtras();

                try {
                    String maxAmplitudeIn = extras.getString("maxAmplitude");
                    String lightIntensityIn = extras.getString("lightIntensity");

                    audioAmplitude = Integer.parseInt(maxAmplitudeIn);
                    lightIntensity = Float.parseFloat(lightIntensityIn);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkSleepStatus();

                updateGraphData();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sleepDataSeries = new LineGraphSeries<DataPoint>();
    }

    /**
     * onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
     * Creates the sleep fragment tab
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState previous state
     * @return view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sleep, container, false);

        db = new DatabaseHandler(getActivity());

        IntentFilter intentFilter = new IntentFilter("SensorData");
        getActivity().getApplicationContext().registerReceiver(recieveFromSleepService, intentFilter);

        isTracking = Utils.isServiceRunning(getActivity(), SleepService.class);

        // Initialize UI elements
        todaysHours = (TextView) view.findViewById(R.id.textViewTodaysHours);
        todaysEfficiency = (TextView) view.findViewById(R.id.textViewEfficiency);
        trackingStatus = (TextView) view.findViewById(R.id.textViewTrackingStatus);
        graphView = (GraphView) view.findViewById(R.id.sleepGraph);
        calibrateButton = (Button) view.findViewById(R.id.calibrateButton);
        stopStartButton = (Button) view.findViewById(R.id.stopStartSleepButton);
        moreSessionInfo = (Button) view.findViewById(R.id.moreSleepSessionInfo);

        calibrateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    Utils.displayDialog(getActivity(), "Cannot Calibrate...", "You must have sleep tracking enabled to calibrate the sensors.",
                            null, "OK", Utils.emptyRunnable(), null);
                } else {
                    calibrateSensors();
                }
            }
        });

        if (isTracking) {
            trackingStatus.setText("Tracking...");
            stopStartButton.setText("Stop Sleep Tracking");
            calibrateButton.setEnabled(true);
        } else {
            trackingStatus.setText("Not Tracking...");
            stopStartButton.setText("Start Sleep Tracking");
            calibrateButton.setEnabled(false);
        }

        stopStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    Utils.displayDialog(getActivity(), "Stop Sleep Tracking?", "Are you SURE you want to stop sleep tracking?",
                            "Cancel", "OK", stopSleepRunnable(), Utils.emptyRunnable());
                } else {
                    startSleepTracking();
                    stopStartButton.setText("Stop Sleep Tracking");
                }
            }
        });

        moreSessionInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fallAsleepTime.equals("") || wakeUpTime.equals("")) {
                    Utils.displayDialog(getActivity(), "Last Sleep Session Information",
                            "You must have at least 1 sleep session to view stats.", null, "OK", Utils.emptyRunnable(), null);
                } else {
                    Utils.displayDialog(getActivity(), "Detailed Sleep Stats",
                            "Last Time Fell Asleep: " + fallAsleepTime
                                    + "\nLast Time Woke Up: " + wakeUpTime
                                    + "\nTotal Time Asleep: " + getHoursTodayFormatted() + " Hours"
                                    + "\nNumber of Wake Ups: " + Integer.toString(numWakeups) + "\n"
                                    + "\nCalibrated Audio Level: " + Integer.toString(calibratedAmplitude)
                                    + "\nCalibrated Light Level: " + Float.toString(calibratedLight),
                            null, "OK", Utils.emptyRunnable(), null);
                }
            }
        });

        graphView.addSeries(sleepDataSeries);

        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graphView.getGridLabelRenderer().setGridColor(Color.LTGRAY);
        graphView.getGridLabelRenderer().setTextSize(20);

        Date now = new Date();
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(now.getTime() - 6 * 24 * 60 * 60 * 1000);
        graphView.getViewport().setMaxX(now.getTime());

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

        todaysHours.setText("Hours Slept Today: " + getHoursTodayFormatted());

        return view;
    }

    private void updateGraphData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);

        Date now = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);

        Utils.todaysSleepHours = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        //todaysHours.setText("Hours Slept Today: " + Utils.todaysSleepHours);

        float day1 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day2 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day3 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day4 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day5 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day6 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        calendar.add(Calendar.HOUR, -24);

        float day7 = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));

        // Check to see if there is no data yet
        // For the jjoe64 graphview workaround

        if (day1 > 0.0F && day2 == 0.0F && day3 == 0.0F && day4 == 0.0F && day5 == 0.0F && day6 == 0.0F && day7 == 0.0F) {
            sleepDataSeries.resetData(new DataPoint[]{
                    new DataPoint((now.getTime() - 6 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint((now.getTime() - 5 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint((now.getTime() - 4 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint((now.getTime() - 3 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint((now.getTime() - 2 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint((now.getTime() - 1 * 24 * 60 * 60 * 1000), 0),
                    new DataPoint(now.getTime(), day1)});

            sleepDataSeries.setThickness(5);
            sleepDataSeries.setColor(Color.argb(255, 0, 79, 255));
        } else if (day1 == 0.0F && day2 == 0.0F && day3 == 0.0F && day4 == 0.0F && day5 == 0.0F && day6 == 0.0F && day7 == 0.0F) {
            sleepDataSeries.resetData(new DataPoint[]{
                    new DataPoint((now.getTime() - 6 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 5 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint((now.getTime() - 4 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 3 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint((now.getTime() - 2 * 24 * 60 * 60 * 1000), 1),
                    new DataPoint((now.getTime() - 1 * 24 * 60 * 60 * 1000), 2),
                    new DataPoint(now.getTime(), 1)});

            sleepDataSeries.setThickness(0);
            sleepDataSeries.setColor(Color.WHITE);
        } else {
            sleepDataSeries.resetData(new DataPoint[]{
                    new DataPoint((now.getTime() - 6 * 24 * 60 * 60 * 1000), day7),
                    new DataPoint((now.getTime() - 5 * 24 * 60 * 60 * 1000), day6),
                    new DataPoint((now.getTime() - 4 * 24 * 60 * 60 * 1000), day5),
                    new DataPoint((now.getTime() - 3 * 24 * 60 * 60 * 1000), day4),
                    new DataPoint((now.getTime() - 2 * 24 * 60 * 60 * 1000), day3),
                    new DataPoint((now.getTime() - 1 * 24 * 60 * 60 * 1000), day2),
                    new DataPoint(now.getTime(), day1)});

            sleepDataSeries.setThickness(5);
            sleepDataSeries.setColor(Color.argb(255, 0, 79, 255));
        }

    }

    /**
     * stopSleepRunnable()
     * Procedure to stop sleep tracking that can be called from static classes
     */
    private Runnable stopSleepRunnable() {
        return new Runnable() {
            public void run() {
                stopSleepTracking();
                stopStartButton.setText("Start Sleep Tracking");
            }
        };
    }

    /**
     * startSleepTracking()
     * Begins tracking sleep
     */
    private void startSleepTracking() {
        // Start service
        getActivity().startService(new Intent(getActivity(), SleepService.class));

        isTracking = true;

        Log.d("SleepFragment", "Starting sleep service");

        Utils.displayDialog(getActivity(), "Sleep Tracking Started!", "This app uses the light sensor and microphone to track your sleeping " +
                        "patterns. Be sure to keep your device close to you while you sleep for best results.",
                null, "OK", Utils.emptyRunnable(), null);

        trackingStatus.setText("Tracking...");
        totalDuration = 0.0F;
        calibrateButton.setEnabled(true);

        if (isTracking) {
            calibrateSensors();
        } else {
            checkSleepStatus();
        }
    }

    /**
     * stopSleepTracking()
     * Stops tracking sleep
     */
    private void stopSleepTracking() {
        getActivity().stopService(new Intent(getActivity(), SleepService.class));
        Log.d("SleepFragment", "Stopping sleep service");

        isTracking = false;

        trackingStatus.setText("Not Tracking...");
        calibrateButton.setEnabled(false);

        checkSleepAchievements();
    }

    /**
     * calibrateSensors()
     * Calibrates the light/sound levels by getting avgs (get values every 1sec for 10sec period) and
     * adding a preset margin to allow for movement/daybreak
     */
    private void calibrateSensors() {

        trackingStatus.setText("Calibrating...");
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
                calibratedLight += LIGHT_MARGIN;
                calibratedAmplitude += NOISE_MARGIN;

                Log.d("SleepMonitor", "Calibrated sensors");

                checkSleepStatus();

                if (isTracking) {
                    trackingStatus.setText("Tracking...");
                    stopStartButton.setText("Stop Sleep Tracking");
                    calibrateButton.setEnabled(true);
                } else {
                    trackingStatus.setText("Not Tracking...");
                    stopStartButton.setText("Start Sleep Tracking");
                    calibrateButton.setEnabled(false);
                }

                calibrateTimer.cancel();
            }
        }.start();
    }

    /**
     * checkSleepStatus()
     * Checks time and light/sound levels to see if the user falls within all sleep thresholds,
     * sets isAsleep equal to true or false and sets fallAsleepTime and wakeUpTime
     */
    private void checkSleepStatus() {

        //Time check first
        if (!SleepHourCheck()) {
            Log.d("StopSleepTracking:", "Outside hour range.");
            stopSleepTracking();
        } else {
            // Check all conditions to see if user fell asleep
            if (!isAsleep && SleepHourCheck() && SleepLightCheck() && SleepAudioCheck()) {
                Log.d("SleepMonitor", "Fell Asleep:" + fallAsleepTime);

                isAsleep = true;

                fallAsleepTime = getTime('S');
            }

            // Check to see if user woke up
            if (isAsleep && (!SleepHourCheck() || !SleepLightCheck() || !SleepAudioCheck())) {
                Log.d("SleepMonitor", "Woke Up:" + fallAsleepTime);

                isAsleep = false;
                wakeUpTime = getTime('W');

                numWakeups++;

                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
                int date = Integer.valueOf(dateFormat.format(calendar.getTime()));

                totalDuration = getDuration();
                Log.d("getDuration", "Adding " + Float.toString(getDuration()));
                db.addHoursSlept(new HoursSlept(String.valueOf(date), Float.valueOf(totalDuration)));

                Utils.todaysSleepHours = db.getTodaysSleepTotal(date);

                todaysHours.setText("Hours Slept Today: " + getHoursTodayFormatted());
                todaysEfficiency.setText("Today's Efficiency: " + getEfficiency());
            }
        }
    }

    /**
     * getTime(char set)
     * Gets the time in HH:MM:SS format, takes in a char if the sleep/wake variables need to be reset
     *
     * @param set flag if the sleep/wake times need to be updated
     * @return current time in HH:MM:SS format
     */
    private String getTime(char set) {
        Log.d("getTime", "Getting current time");
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        //12:00 AM is treated as hour 0
        if (hour == 0) {
            hour = 12;
        }

        Log.d("CurrentTime", Integer.toString(hour) + ":" + Integer.toString(minute) + getAmPm());

        //if the new sleep time is gotten, set it globally
        if (set == 'S') {
            sleepHour = hour;
            sleepMin = minute;
            sleepAmPm = getAmPm();
            Log.d("SetSleepTime", Integer.toString(sleepHour) + ":" + Integer.toString(sleepMin) + sleepAmPm);
        }

        //if the new wake time is gotten, set it globally
        if (set == 'W') {
            wakeHour = hour;
            wakeMin = minute;
            wakeAmPm = getAmPm();
            Log.d("SetWakeTime", Integer.toString(wakeHour) + ":" + Integer.toString(wakeMin) + wakeAmPm);
        }

        return Integer.toString(hour) + ":" + Integer.toString(minute) + getAmPm();
    }


    private String getHoursTodayFormatted() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
        int date = Integer.valueOf(dateFormat.format(calendar.getTime()));

        float todaysHours = db.getTodaysSleepTotal(date);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return String.valueOf(decimalFormat.format(todaysHours));
    }

    /**
     * getAmPm()
     * Checks to see if time of day is AM or PM
     *
     * @return string containing either "AM" or "PM"
     */
    private String getAmPm() {
        Calendar c = Calendar.getInstance();
        int am_pm = c.get(Calendar.AM_PM);
        String amPm;

        if (am_pm == 0)
            amPm = "AM";
        else
            amPm = "PM";

        return amPm;
    }

    /**
     * SleepHourCheck()
     * Checks to see if the current hour is between the valid sleeping hours
     *
     * @return true if hour is valid, false if hour is not valid
     */
    private boolean SleepHourCheck() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        String amPm = getAmPm();

        if (hour == 0) {
            hour = 12;
        }

        if (hour == 12 && amPm.equals("PM")) {
            return false;
        }

        if (hour == 12 && amPm.equals("AM")) {
            return true;
        }

        if ((hour >= calibratedSleepHour && amPm.equals("PM")) || (hour <= calibratedWakeHour && amPm.equals("AM"))) {
            return true;
        }
        return false;
    }

    /**
     * SleepLightCheck()
     * Checks to see if the light level is below the valid sleeping light level
     *
     * @return true if light level is valid, false if light level is not valid
     */
    private boolean SleepLightCheck() {
        if (lightIntensity < calibratedLight) {
            return true;
        }
        return false;
    }

    //check to see if audio is below valid level

    /**
     * SleepAudioCheck()
     * Checks to see if the sound level is below the valid sleeping sound level
     *
     * @return true if sound level is valid, false if sound level is not valid
     */
    private boolean SleepAudioCheck() {
        if (audioAmplitude < calibratedAmplitude) {
            return true;
        }
        return false;
    }

    //gets duration of sleep to wake time

    /**
     * getDuration()
     * Calculates the duration of time slept based on the time the user fell asleep, woke up, and previous
     * duration during the night
     *
     * @return duration of sleep for a night
     */
    private float getDuration() {
        Log.d("getDuration", "Getting current duration...");
        int newHours;
        int newMins;
        double duration;

        //both AM or both PM: simply subtract
        if ((sleepAmPm.equals("PM") && wakeAmPm.equals("PM")) || (sleepAmPm.equals("AM") && wakeAmPm.equals("AM"))) {
            newHours = Math.abs(sleepHour - wakeHour);
            newMins = Math.abs(sleepMin - wakeMin);
        }
        //crossed over midnight: have to take day change into account
        else {
            //take into account waking up in the midnight hour
            if(wakeHour == 12 && wakeAmPm.equals("AM")){
                wakeHour = 0;
            }

            newHours = (12 - sleepHour) + wakeHour - 1;
            newMins = (60 - sleepMin) + wakeMin;
        }

        //check for full hour
        if(newHours == 1 && sleepMin > wakeMin){
            newHours--;
            newMins = (60 - sleepMin) + wakeMin;
        }

        //add appropriate minutes
        if (newMins >= 60) {
            newMins -= 60;
            newHours += 1;
        }

        //convert to hours and partial hours
        duration = newHours + (newMins / 60.0);

        return (float)duration;
    }

    /**
     * getEfficiency()
     * Gets the overall efficiency of the current sleep session based on the amount of time
     * slept and the number of wakeups
     *
     * @return efficiency on scale of 0-100
     */
    private int getEfficiency() {
//gets current sleep duration from db to calculate efficiency
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
        Date currentDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        int formattedCurrentDate = Integer.valueOf(dateFormat.format(calendar.getTime()));
        float currentDuration = db.getTodaysMoodAvg(formattedCurrentDate);

        //based on avg 2 wakeups per hour, each wakeup resulting in a -0.625% efficiency (Source: FitBit)
        double expectedWakeups = (double)currentDuration * 2;        double extraWakeups = numWakeups - expectedWakeups;

        if (extraWakeups <= 0) {
            extraWakeups = 1;
        }

        //if user has been sleeping less than an hour, it's much more inefficient to wake up so penalty is higher
        if(expectedWakeups == 0){
            efficiency = 100 - (int) Math.floor(extraWakeups * 6.25);
        }
        else {
            efficiency = 100 - (int) Math.ceil(extraWakeups * 0.625);
        }

        if (efficiency < 0) {
            efficiency = 0;
        }

        Log.d("GetEfficiency", "Efficiency: " + Integer.toString(efficiency));

        return efficiency;
    }

    private void checkSleepAchievements() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);

        Date now = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(now);

        float pastWeekHours = db.getWeeksSleepTotal();
        float allTimeHours = db.getAllTimeSleepTotal();
        float todaysHours = db.getTodaysSleepTotal(Integer.valueOf(dateFormat.format(calendar.getTime())));
        Log.d("SleepFrag", "Checking sleep achievements... Today's Hours: " + todaysHours + " pastWeekHours: " + pastWeekHours + " all time: " + allTimeHours);

        if (todaysHours > 0.0F) {
            Utils.unlockAchievement(12, getActivity());
        }

        if (todaysHours >= 8.0F) {
            Utils.unlockAchievement(13, getActivity());
        }

        if (pastWeekHours >= 56.0F) {
            Utils.unlockAchievement(14, getActivity());
        }

        if (allTimeHours >= 1000.0F) {
            Utils.unlockAchievement(15, getActivity());
        }

        if (allTimeHours >= 2500.0F) {
            Utils.unlockAchievement(16, getActivity());
        }

        if (allTimeHours >= 5000.0F) {
            Utils.unlockAchievement(17, getActivity());
        }
    }
}