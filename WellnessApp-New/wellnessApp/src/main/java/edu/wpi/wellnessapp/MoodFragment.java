/**
 * MainFragment.java
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

import android.content.Intent;
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
    private Button startStopMoodButton;
    private Button setMoodButton;

    private boolean isTracking = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_mood, container, false);

        isTracking = Utils.isServiceRunning(getActivity(), MoodAlertService.class);

        startStopMoodButton = (Button) view.findViewById(R.id.startMoodButton);
        setMoodButton = (Button) view.findViewById(R.id.setMoodButton);

        if (isTracking) {
            startStopMoodButton.setText("Stop Mood Tracking");
            setMoodButton.setEnabled(true);
        }
        else {
            startStopMoodButton.setText("Start Mood Tracking");
            setMoodButton.setEnabled(false);
        }

        startStopMoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    stopMoodTracking();
                    startStopMoodButton.setText("Start Mood Tracking");
                } else {
                    startMoodTracking();
                    startStopMoodButton.setText("Stop Mood Tracking");
                }
            }
        });

        setMoodButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoodPopup(v);
            }
        });

        return view;
    }

    private void startMoodTracking() {
        getActivity().startService(new Intent(getActivity(), MoodAlertService.class));
        isTracking = true;
        setMoodButton.setEnabled(true);
    }

    private void stopMoodTracking() {
        getActivity().stopService(new Intent(getActivity(), MoodAlertService.class));
        isTracking = false;
        setMoodButton.setEnabled(false);
    }

    public void showMoodPopup(View anchorView) {
        LayoutInflater mInflater = LayoutInflater.from(anchorView.getContext().getApplicationContext());

        final View popupView = mInflater.inflate(R.layout.mood_popup, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, 0, 0);

        final TextView popupTitle = (TextView) popupView.findViewById(R.id.popupTitle);
        popupTitle.setText("Set your Mood!");

        final Button setMoodConfirmButton = (Button) popupView.findViewById(R.id.set_mood_button);
        setMoodConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingBar rb = (RatingBar) popupView.findViewById(R.id.ratingBar1);
                popupTitle.setText("Saved " + rb.getRating() + " !");

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

        popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL, anchorView.getWidth(),
                anchorView.getHeight());
    }

}
