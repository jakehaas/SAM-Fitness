package edu.wpi.wellnessapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class Achievement {

    Context ctx = null;
    int achievementID = -1;

    public Achievement(View v, int achievID) {
	ctx = v.getContext();
	achievementID = achievID;
	
	// Update the database with a record of this achiev
	if (updateDatabase()) {
	    // If the database update was successful, show the alert
	    displayDialog();
	} else {
	    // Could not save record of achiev. Handle properly -- (maybe try again every 10 secs for 1 min, then fail?)
	}
	
    }

    private void displayDialog() {
	if (ctx == null)
	{
	    // Show error ----- TODO
	    return;
	}
	
	AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

	alert.setTitle("Achievement Unlocked!");
	alert.setMessage("Congrats!");

	alert.setPositiveButton(R.string.achiv_view,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// Load achiev view
		    }
		});

	alert.setNegativeButton(R.string.achiv_close,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// Just close the popup...
		    }
		});

	alert.setIcon(android.R.drawable.ic_dialog_alert);
	alert.show();
    }
    
    private boolean updateDatabase() {
	return true; // TMP FOR NOW UNTIL WE HAVE A REAL DATABASE HANDLER
    }

}
