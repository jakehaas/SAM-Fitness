package edu.wpi.wellnessapp;

public class Utils {

    public Utils() {
	// TODO Auto-generated constructor stub
    }

    public static boolean tryParseInt(String value) {
	try {
	    Integer.parseInt(value);
	    return true;
	} catch (NumberFormatException nfe) {
	    return false;
	}
    }

}
