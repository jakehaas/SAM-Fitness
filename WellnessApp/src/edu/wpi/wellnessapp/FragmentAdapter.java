package edu.wpi.wellnessapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class FragmentAdapter extends FragmentStatePagerAdapter {

    static MainFragment mainFragment;
    static StepsFragment stepFragment;
    static SleepFragment sleepFragment;
    static MoodFragment moodFragment;

    public FragmentAdapter(FragmentManager fm) {
	super(fm);
	mainFragment = new MainFragment();
	stepFragment = new StepsFragment();
	sleepFragment = new SleepFragment();
	moodFragment = new MoodFragment();
    }

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

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return 4;
    }

}
