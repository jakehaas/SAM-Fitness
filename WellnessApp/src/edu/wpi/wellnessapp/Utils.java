/**
 * Utils.java
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public Utils() {
	// TODO Auto-generated constructor stub
    }

    /**
     * tryParseInt(String value)
     * 
     * @param value
     *            The string to try and parse into an integer
     */
    public static boolean tryParseInt(String value) {
	try {
	    Integer.parseInt(value);
	    return true;
	} catch (NumberFormatException nfe) {
	    return false;
	}
    }

    public String getTimeString() {
	SimpleDateFormat formatter = new SimpleDateFormat("MM dd - HH:mm");
	Date curDate = new Date(System.currentTimeMillis());
	String timeString = formatter.format(curDate);
	return timeString;
    }

    public String getDateString() {
	SimpleDateFormat formatter = new SimpleDateFormat("MM dd");
	Date curDate = new Date(System.currentTimeMillis());
	String timeString = formatter.format(curDate);
	return timeString;
    }

}
