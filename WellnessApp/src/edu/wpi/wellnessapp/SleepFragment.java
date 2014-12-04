/**
 * SleepFragment.java
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SleepFragment extends Fragment {

    private Button closeButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	View view = inflater.inflate(R.layout.fragment_sleep, container, false);
	
	this.closeButton = (Button) view.findViewById(R.id.button1);
	this.closeButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		new Achievement(v, 0);
	    }
	});

	return view;
    }

}
