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
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import java.io.IOException;
import java.lang.reflect.Field;


public class MainFragment extends Fragment {
    private static MainFragment master = null;

    //private RGBColor back = new RGBColor(37, 37, 37);

    private boolean canShowSettingPopup = true;
    private PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

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
}