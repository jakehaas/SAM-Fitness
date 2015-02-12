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
 * Copyright (c) 2013, 2014. Wellness-App-MQP. All Right Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

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
     * getFilePath(Context ctx)
     *
     * @param ctx A context to access the android filesystem
     */
    public static String getFilePath(Context ctx) {
        String mFileName = ctx.getCacheDir().getAbsolutePath();
        mFileName += "/sleep_audio.3gp";
        return mFileName;
    }

    /**
     * getTotalScore()
     *
     * Get the total average score for the user
     */
    public static float getTotalScore()
    {
        // TODO: Actually use the real numbers!
        return 70.0F;
    }
}
