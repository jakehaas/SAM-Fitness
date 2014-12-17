/**
 * Achievement.java
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class Achievement {
    // The context for the alert
    Context ctx = null;
    
    // Which achievement is this?
    int achievementID = -1;

    /**
     * Achievement Constructor
     * Achievement(View v, int achievID)
     * 
     * @param v 	The view that the alert is added to
     * @param achievID	Which achievement
     */
    public Achievement(View v, int achievID) {
	// Get the context from the view
	ctx = v.getContext();
	
	// Assign achievement
	achievementID = achievID;
	
	// Update the database with a record of this achievement
	if (updateDatabase()) {
	    // If the database update was successful, show the alert
	    displayDialog();
	} else {
	    // Could not save record of achievement. 
	    // TODO Handle properly -- (maybe try again every 10 secs for 1 min, then fail?)
	}
	
    }

    /**
     * displayDialog()
     * 
     * Displays an alert informing the user that they
     * earned a new achievement
     */
    private void displayDialog() {
	if (ctx == null)
	{
	    // TODO -- SHOW ERROR
	    return;
	}
	
	// Instantiate the AlertDialog Builder
	AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

	// Set the alert attributes
	alert.setTitle("Achievement Unlocked -- " + getAchievmentAlertTitleText(achievementID));
	alert.setMessage(getAchievmentAlertDescText(achievementID));

	// Define positive button response
	alert.setPositiveButton(R.string.achiv_view,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// TODO -- Load achievement view
		    }
		});

	// Define negative button response
	alert.setNegativeButton(R.string.achiv_close,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// Just close the popup...
		    }
		});

	// Set the icon of the alert
	alert.setIcon(android.R.drawable.ic_dialog_alert);
	
	// Show the alert to the user
	alert.show();
    }
    
    private String getAchievmentAlertDescText(int achievID) {
	
	if (achievID == 1) {
	    return "Congrats! You have unlocked the achievment: " + getAchievmentAlertTitleText(achievID);
	} else {
	    return "Congrats!";
	}
    }
    
    private String getAchievmentAlertTitleText(int achievID) {
	
	if (achievID == 1) {
	    return "First Steps!";
	} else {
	    return "Achievement Name!";
	}
    }
    
    /**
     * updateDatabase()
     * 
     * Update the achievement database
     */
    private boolean updateDatabase() {
	return true;
	// TODO -- IMPLEMENT DATABASE HANDLER
    }

}
