/**
 * StepsTaken.java
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

public class HoursSlept {
    private int id;
    String date;
    int hoursSlept;

    public HoursSlept() {
    }

    public HoursSlept(String date, int hoursSlept) {
	super();
	this.date = date;
	this.hoursSlept = hoursSlept;
    }

    // getters & setters

    @Override
    public String toString() {
	return "HoursSlept [id=" + id + ", date=" + date + ", hoursSlept=" + hoursSlept + "]";
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public int getHoursSlept() {
	return hoursSlept;
    }

    public void setHoursSlept(int hoursSlept) {
	this.hoursSlept = hoursSlept;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }
    // Will be used by the ArrayAdapter in the ListView
}
