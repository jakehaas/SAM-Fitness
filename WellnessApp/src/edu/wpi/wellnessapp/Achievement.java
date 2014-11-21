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
	displayDialog();
    }
    
    public void displayDialog()
    {
	AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
	
	alert.setTitle("Achievement Unlocked!");
	alert.setMessage("Congrats!");
	
	alert.setPositiveButton(R.string.achiv_view,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// continue with delete
		    }
		});
	
	alert.setNegativeButton(R.string.achiv_close,
		new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			// do nothing
		    }
		}).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

}
