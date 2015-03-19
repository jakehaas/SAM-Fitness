/**
 * MainActivity.java
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


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements TabListener {

    ViewPager viewPager;
    ActionBar actionBar;
    public static final int REQUEST_OAUTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                actionBar.setSelectedNavigationItem(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle(getString(R.string.app_name));

        ActionBar.Tab mainTab = actionBar.newTab();
        mainTab.setText("Main");
        mainTab.setTabListener(this);

        ActionBar.Tab sleepTab = actionBar.newTab();
        sleepTab.setText("Sleep");
        sleepTab.setTabListener(this);

        ActionBar.Tab stepTab = actionBar.newTab();
        stepTab.setText("Activity");
        stepTab.setTabListener(this);

        ActionBar.Tab moodTab = actionBar.newTab();
        moodTab.setText("Mood");
        moodTab.setTabListener(this);

        actionBar.addTab(mainTab);
        actionBar.addTab(sleepTab);
        actionBar.addTab(stepTab);
        actionBar.addTab(moodTab);

        // Auto start services
        if (!Utils.isServiceRunning(this, SleepService.class) && !Utils.isServiceRunning(this, MoodAlertService.class)) {
            this.startService(new Intent(this, SleepService.class));
            this.startService(new Intent(this, MoodAlertService.class));

            Utils.displayDialog(this, "Tracking Started!", "This app uses the light sensor, accelerometer and microphone to track your sleep and exercise " +
                            "patterns. Be sure to keep your device close to you while you sleep for best results.",
                    null, "OK", Utils.emptyRunnable(), null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {

            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.pager);
            fragment.onActivityResult(requestCode, resultCode, data);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
        viewPager.setCurrentItem(arg0.getPosition());
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
    }

}
