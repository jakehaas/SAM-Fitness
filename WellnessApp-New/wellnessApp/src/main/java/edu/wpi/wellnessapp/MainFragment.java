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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.VideoView;


public class MainFragment extends Fragment {
    private VideoView videoView;

    private boolean canShowSettingPopup = true;
    private PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        videoView = (VideoView) rootView.findViewById(R.id.mainAvatar);

        Button settingButton = (Button) rootView.findViewById(R.id.settings_button);
        settingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canShowSettingPopup) {
                    showSettingsPopup(v);
                    canShowSettingPopup = false;
                } else {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                        popupWindow = null;
                        canShowSettingPopup = true;
                    }
                }
            }
        });


        String videoUrl = "android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.avatar_walk_in;

        videoView.setMediaController(new MediaController(rootView.getContext().getApplicationContext()));

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                return true;
            }
        });

        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.requestFocus();


        return rootView;
    }

    public void showSettingsPopup(View anchorView) {
        LayoutInflater mInflater;
        Context context = anchorView.getContext().getApplicationContext();
        mInflater = LayoutInflater.from(context);

        final View popupView = mInflater.inflate(R.layout.settings_popup, null);
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


        final Button setMoodConfirmButton = (Button) popupView.findViewById(R.id.set_mood_button);
        setMoodConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMoodConfirmButton.setText("Close!");
                setMoodConfirmButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        popupWindow = null;
                        canShowSettingPopup = true;
                    }
                });
            }
        });

    }

    public void playAvatarAnimation(int animationID) {
        videoView.start();
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
        Log.d("On Resume", "On Resume Called");

        playAvatarAnimation(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("On Pause", "On Pause Called");

        videoView.pause();
    }


    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "onActivityResult Called");

        videoView.start();
    }


    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //Appearance.setup(getActivity());
        Log.d("OnViewStateRestored", "Function called");
        //Log.d(sLogTag, "Populating graph view at onViewStateRestored");
        //TODO: Populate graph function
        //checkSteps = new InsertAndVerifyDataTask();
        //checkSteps.execute();
        //new InsertAndVerifyDataTask().execute();
        //Log.i(sLogTag, "Populated graph view at onViewStateRestored");

        videoView.start();
    }
    */
}