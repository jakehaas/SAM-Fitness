package edu.wpi.wellnessapp;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements TabListener {

    ViewPager viewPager;
    ActionBar actionBar;

    Avatar avatar;

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
	actionBar.setTitle("Wellness App");

	ActionBar.Tab mainTab = actionBar.newTab();
	mainTab.setText("Main");
	mainTab.setTabListener(this);

	ActionBar.Tab stepTap = actionBar.newTab();
	stepTap.setText("Exercise");
	stepTap.setTabListener(this);

	ActionBar.Tab sleepTab = actionBar.newTab();
	sleepTab.setText("Sleep");
	sleepTab.setTabListener(this);

	ActionBar.Tab moodTab = actionBar.newTab();
	moodTab.setText("Mood");
	moodTab.setTabListener(this);

	actionBar.addTab(mainTab);
	actionBar.addTab(stepTap);
	actionBar.addTab(sleepTab);
	actionBar.addTab(moodTab);

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
