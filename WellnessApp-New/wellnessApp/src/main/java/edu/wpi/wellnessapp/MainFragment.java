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

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    private String ANIMATION_VIDEO_PATH;

    private VideoView videoView;

    private TextView activityCircle;
    private TextView sleepCircle;
    private TextView moodCircle;
    private TextView mainCircle;

    private Button helpButton;
    private Button achievButton;

    private PopupWindow helpPopup;
    private PopupWindow achievPopup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ANIMATION_VIDEO_PATH = "android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.avatar_anims;
        videoView = (VideoView) rootView.findViewById(R.id.mainAvatar);

        activityCircle = (TextView) rootView.findViewById(R.id.activityCircle);
        sleepCircle = (TextView) rootView.findViewById(R.id.sleepCircle);
        moodCircle = (TextView) rootView.findViewById(R.id.moodCircle);
        mainCircle = (TextView) rootView.findViewById(R.id.mainCircle);

        helpButton = (Button) rootView.findViewById(R.id.help_button);
        achievButton = (Button) rootView.findViewById(R.id.achiev_button);

        activityCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(1);

                return true;
            }
        });

        sleepCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(2);

                return true;
            }
        });

        moodCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(3);

                return true;
            }
        });


        updateCircles();


        helpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpPopup(v);
            }
        });

        achievButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAchievPopup(v);
            }
        });

        videoView.setMediaController(new MediaController(rootView.getContext().getApplicationContext()));

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                return true;
            }
        });

        animateAvatar();

        return rootView;
    }

    private void showHelpPopup(View anchorView) {
        LayoutInflater mInflater = LayoutInflater.from(anchorView.getContext().getApplicationContext());

        View popupView = mInflater.inflate(R.layout.help_popup, null);
        helpPopup = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


        Button closeHelpButton = (Button) popupView.findViewById(R.id.closeButton);
        closeHelpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                helpPopup.dismiss();
            }
        });

        helpPopup.setFocusable(true);

        helpPopup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void showAchievPopup(View anchorView) {
        LayoutInflater mInflater = LayoutInflater.from(anchorView.getContext().getApplicationContext());

        View popupView = mInflater.inflate(R.layout.achiev_popup, null);
        achievPopup = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        Button closeHelpButton = (Button) popupView.findViewById(R.id.closeButton);
        TableLayout achievTable = (TableLayout) popupView.findViewById(R.id.achiev_table);

        ArrayList<Achievement> achievList = Utils.parseAchievementXML(getResources().openRawResource(R.raw.achievements));
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("achiev_status", 0);

        for (Achievement a : achievList) {
            TableRow row = new TableRow(popupView.getContext());
            TextView textView = new TextView(popupView.getContext());

            String color;

            if (sharedPreferences.getInt(String.valueOf(a.getId()), 0) == 1) {
                color = "green";
            } else {
                color = "red";
            }

            textView.setText(Html.fromHtml("<font color=\"" + color + "\"><b>" + a.getName() + "</b><br />" + a.getDescription() + "</font><br />"));
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);

            row.addView(textView);
            achievTable.addView(row);
        }


        closeHelpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                achievPopup.dismiss();
            }
        });

        // If the PopupWindow should be focusable
        achievPopup.setFocusable(true);

        achievPopup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void animateAvatar() {
        videoView.stopPlayback();
        videoView.setVideoURI(Uri.parse(ANIMATION_VIDEO_PATH));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                videoView.seekTo(150);
                videoView.start();
            }
        });
        videoView.requestFocus();
        videoView.start();
    }

    private void updateCircles() {
        activityCircle.setText(Html.fromHtml("<b>" + Utils.getStepScore() + "</b><br />Steps"));
        moodCircle.setText(Html.fromHtml("<b>" + Utils.getMoodScore() + "</b><br />Happiness"));
        sleepCircle.setText(Html.fromHtml("<b>" + Utils.getSleepScore() + "</b><br />Hours"));
        mainCircle.setText(Html.fromHtml("<b>" + Utils.getTotalScore() + "</b><br />Total Score"));
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateCircles();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }
}