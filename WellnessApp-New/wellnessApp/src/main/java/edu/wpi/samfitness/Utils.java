/**
 * Utils.java
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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.wpi.wellnessapp.R;

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
            public void run() {
            }
        };
    }


    /**
     * getTotalScore()
     * <p/>
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
            weightedSteps = Math.round(map((float) todaysSteps, 0.0F, (float) RECOMMENDED_STEP_COUNT, 0.0F, MAX_TOTAL_SCORE));
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
     * <p/>
     * Get the step score for the user
     */
    public static double todaysSteps = 0.0D;

    public static String getStepScore() {
        return String.valueOf((int) Math.round(todaysSteps));
    }

    /**
     * getSleepScore()
     * <p/>
     * Get the sleep score for the user
     */
    public static float todaysSleepHours = 0.0F;

    public static String getSleepScore() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(map(todaysSleepHours, 0.0F, 8.5F, 0.0F, 100.0F));
    }

    /**
     * getMoodScore()
     * <p/>
     * Get the mood score for the user
     */
    public static float todaysMoodScore = 0.0F;

    public static String getMoodScore() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return decimalFormat.format(map(todaysMoodScore, 0.0F, 5.0F, 0.0F, 100.0F));
    }

    /**
     * map(float x, float in_min, float in_max, float out_min, float out_max)
     * <p/>
     * Map a value from one range into another
     */
    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /**
     * ArrayList<Achievement> parseAchievementXML(InputStream inputXML)
     * <p/>
     * Parse the achievement XML file into an ArrayList
     */
    public static ArrayList<Achievement> parseAchievementXML(InputStream inputXML) {
        XmlPullParserFactory pullParserFactory;
        ArrayList<Achievement> achievementsList = null;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputXML, null);

            int eventType = parser.getEventType();
            Achievement currentAchievement = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name;

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        achievementsList = new ArrayList<Achievement>();
                        break;

                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equals("achievement")) {
                            currentAchievement = new Achievement();
                        } else if (currentAchievement != null) {
                            if (name.equals("id")) {
                                String id = parser.nextText();
                                if (Utils.tryParseInt(id)) {
                                    currentAchievement.setId(Integer.parseInt(id));
                                } else {
                                    throw new XmlPullParserException("Error parsing Achievements List.");
                                }
                            } else if (name.equals("name")) {
                                currentAchievement.setName(parser.nextText());
                            } else if (name.equals("description")) {
                                currentAchievement.setDescription(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("achievement") && currentAchievement != null) {
                            achievementsList.add(currentAchievement);
                        }
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return achievementsList;
    }


    public static void unlockAchievement(int id, Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("achiev_status", 0);
        ArrayList<Achievement> achievList = Utils.parseAchievementXML(activity.getResources().openRawResource(R.raw.achievements));

        if (sharedPreferences.getInt(String.valueOf(achievList.get(id).getId()), 0) == 0) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

            sharedPreferencesEditor.putInt(String.valueOf(id), 1);

            sharedPreferencesEditor.commit();

            displayDialog(activity, "Achievement Unlocked -- " + achievList.get(id).getName(), achievList.get(id).getDescription(), null, "OK", emptyRunnable(), null);
        }
    }

    public static int getScreenWidthInPX(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    public static int getScreenHeightInPX(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static void fakeData(Activity activity) {
        DatabaseHandler db = new DatabaseHandler(activity);

        db.addHoursSlept(new HoursSlept("3222015", 8.0F));
        db.addHoursSlept(new HoursSlept("3212015", 0.0F));
        db.addHoursSlept(new HoursSlept("3202015", 0.0F));
        db.addHoursSlept(new HoursSlept("3192015", 0.0F));
        db.addHoursSlept(new HoursSlept("3182015", 0.0F));
        db.addHoursSlept(new HoursSlept("3172015", 0.0F));
        db.addHoursSlept(new HoursSlept("3162015", 0.0F));

        db.addMood(new MoodTic("3222015", 4.0F));
        db.addMood(new MoodTic("3212015", 5.0F));
        db.addMood(new MoodTic("3202015", 4.5F));
        db.addMood(new MoodTic("3192015", 3.5F));
        db.addMood(new MoodTic("3182015", 4.0F));
        db.addMood(new MoodTic("3172015", 4.5F));
        db.addMood(new MoodTic("3162015", 5.0F));

        todaysSteps = 100000000;
    }

}
