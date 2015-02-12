/**
 * MoodFragment.java
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

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

public class MoodFragment extends Fragment {

    // The context for the alert
    Context ctx = null;

    private Button setMoodButton;

    View view;
    // View popupView;
    ViewGroup vg;

    LayoutInflater li;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {

	view = inflater.inflate(R.layout.fragment_mood, container, false);
	// popupView = inflater.inflate(R.layout.mood_popup, container, false);
	vg = container;
	li = inflater;

	this.setMoodButton = (Button) view.findViewById(R.id.button1);
	this.setMoodButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// new Achievement(v, 0);
		showPopup(v);
	    }
	});

	ctx = view.getContext();
	return view;
    }

    public void showPopup(View anchorView) {
	
	LayoutInflater mInflater;
	Context context = anchorView.getContext().getApplicationContext();
	mInflater = LayoutInflater.from(context);

	final View popupView = mInflater.inflate(R.layout.mood_popup, null);

	final PopupWindow popupWindow = new PopupWindow(popupView, 400, 400);

	final TextView tv = (TextView) popupView.findViewById(R.id.textView1);
	tv.setText("Set your Mood!");

	final Button setMoodConfirmButton = (Button) popupView.findViewById(R.id.set_mood_button);
	setMoodConfirmButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		RatingBar rb = (RatingBar) popupView.findViewById(R.id.ratingBar1);
		tv.setText("Saved " + rb.getRating()  + " !");
		
		setMoodConfirmButton.setText("Close!");
		setMoodConfirmButton.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			popupWindow.dismiss();
		    }
		});
	    }
	});
	


	// If the PopupWindow should be focusable
	popupWindow.setFocusable(true);

	// If you need the PopupWindow to dismiss when when touched outside
	popupWindow.setBackgroundDrawable(new ColorDrawable());

	int location[] = new int[2];

	// Get the View's(the one that was clicked in the Fragment) location
	anchorView.getLocationOnScreen(location);

	// Using location, the PopupWindow will be displayed right under
	// anchorView
	popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0],
		location[1] + anchorView.getHeight());

    }

}
