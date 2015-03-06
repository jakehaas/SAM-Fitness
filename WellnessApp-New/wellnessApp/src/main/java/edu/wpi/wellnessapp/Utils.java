/**
 * Utils.java
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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.DecimalFormat;

public class Utils {

    /**
     * tryParseInt(String value)
     *
     * @param value The string to try and parse into an integer
     */
    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * getAudioSampleFilePath(Context context)
     *
     * @param context A context to access the android filesystem
     */
    public static String getAudioSampleFilePath(Context context) {
        String mFileName = context.getCacheDir().getAbsolutePath();
        mFileName += "/sleep_audio.3gp";
        return mFileName;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean displayDialog(Activity act, String Title, String ConfirmText,
                                        String CancelBtn, String OkBtn, final Runnable aProcedure,
                                        final Runnable bProcedure) {

        AlertDialog dialog = new AlertDialog.Builder(act).create();

        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        aProcedure.run();
                    }
                });

        if (CancelBtn != null) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int buttonId) {
                            bProcedure.run();
                        }
                    });
        }

        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();

        return true;
    }

    public static Runnable emptyRunnable() {
        return new Runnable() {
            public void run() {}
        };
    }


    /**
     * getTotalScore()
     *
     * Get the total average score for the user
     */
    public static String getTotalScore() {
        int RECOMMENDED_STEP_COUNT = 13000;
        float RECOMMENDED_SLEEP_HOURS = 9.0F;
        float RECOMMENDED_MOOD_SCORE = 8.5F;

        float MAX_TOTAL_SCORE = 100.0F;

        float weightedSteps;
        float weightedHours;
        float weightedMood;

        if (todaysSteps >= RECOMMENDED_STEP_COUNT) {
            weightedSteps = 100.0F;
        } else {
            weightedSteps = Math.round(map((float)todaysSteps, 0.0F, (float)RECOMMENDED_STEP_COUNT, 0.0F, MAX_TOTAL_SCORE));
        }

        if (todaysSleepHours >= RECOMMENDED_SLEEP_HOURS) {
            weightedHours = 100.0F;
        } else {
            weightedHours = Math.round(map(todaysSleepHours, 0.0F, RECOMMENDED_SLEEP_HOURS, 0.0F, MAX_TOTAL_SCORE));
        }

        if (todaysMoodScore >= RECOMMENDED_MOOD_SCORE) {
            weightedMood = 100.0F;
        } else {
            weightedMood = Math.round(map(todaysSleepHours, 0.0F, RECOMMENDED_SLEEP_HOURS, 0.0F, MAX_TOTAL_SCORE));
        }

        float weightedTotal = ((weightedSteps + weightedHours + weightedMood) / 3.0F);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(weightedTotal);
    }

    /**
     * getStepScore()
     *
     * Get the step score for the user
     */
    public static double todaysSteps = 0.0D;
    public static String getStepScore() {
        return String.valueOf((int) Math.round(todaysSteps));
    }

    /**
     * getSleepScore()
     *
     * Get the sleep score for the user
     */
    public static float todaysSleepHours = 0.0F;
    public static String getSleepScore() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(todaysSleepHours);
    }

    /**
     * getMoodScore()
     *
     * Get the mood score for the user
     */
    public static float todaysMoodScore = 0.0F;
    public static String getMoodScore() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(todaysMoodScore);
    }

    /**
     * map(float x, float in_min, float in_max, float out_min, float out_max)
     *
     * Map a value from one range into another
     */
    public static float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
