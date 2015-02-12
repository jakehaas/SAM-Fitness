/**
 * FragmentAdapter.java
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class FragmentAdapter extends FragmentStatePagerAdapter {

    static MainFragment mainFragment;
    static StepsFragment stepFragment;
    static SleepFragment sleepFragment;
    static MoodFragment moodFragment;

    /**
     * FragmentAdapter Constructor
     * FragmentAdapter(FragmentManager fm)
     * 
     * @param fm 	The FragmentManager used to switch
     * 			between fragments on the tabbed pane
     */
    public FragmentAdapter(FragmentManager fm) {
	super(fm);
	mainFragment = new MainFragment();
	stepFragment = new StepsFragment();
	sleepFragment = new SleepFragment();
	moodFragment = new MoodFragment();
    }

    /**
     * Fragment getItem(int arg0)
     * 
     * Returns the currently selected tab fragment
     * 
     * @param arg0 	The currently selected tab
     */
    @Override
    public Fragment getItem(int arg0) {
	Fragment fragment = null;

	switch (arg0) {
    	case 0:
    	    fragment = mainFragment;
    	    break;
    	case 1:
    	    fragment = stepFragment;
    	    break;
    	case 2:
    	    fragment = sleepFragment;
    	    break;
    	case 3:
    	    fragment = moodFragment;
    	    break;
	}

	return fragment;
    }

    /**
     * int getCount()
     * 
     * Returns the total number of available tabs for the tabbed pane
     */
    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return 4;
    }

}
