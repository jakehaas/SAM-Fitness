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
     * getAudioSampleFilePath(Context ctx)
     *
     * @param ctx A context to access the android filesystem
     */
    public static String getAudioSampleFilePath(Context ctx) {
        String mFileName = ctx.getCacheDir().getAbsolutePath();
        mFileName += "/sleep_audio.3gp";
        return mFileName;
    }

    public static boolean isServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
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
     * <p/>
     * Get the total average score for the user
     */
    public static float getTotalScore() {
        // TODO: Actually use the real numbers!
        return 74.0F;
    }

    /**
     * getStepScore()
     * <p/>
     * Get the step score for the user
     */
    public static float getStepScore() {
        // TODO: Actually use the real numbers!
        return 83.5F;
    }

    /**
     * getSleepScore()
     * <p/>
     * Get the sleep score for the user
     */
    public static float getSleepScore() {
        // TODO: Actually use the real numbers!
        return 96.8F;
    }

    /**
     * getMoodScore()
     * <p/>
     * Get the mood score for the user
     */
    public static float getMoodScore() {
        // TODO: Actually use the real numbers!
        return 89.1F;
    }
}
