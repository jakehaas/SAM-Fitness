/**
 * StepsTaken.java
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

public class StepsTaken {
    private int id;
    String date;
    int steps;

    public StepsTaken() {
    }

    public StepsTaken(String date, int steps) {
	super();
	this.date = date;
	this.steps = steps;
    }

    // getters & setters

    @Override
    public String toString() {
	return "StepsTaken [id=" + id + ", date=" + date + ", steps=" + steps + "]";
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public int getSteps() {
	return steps;
    }

    public void setSteps(int steps) {
	this.steps = steps;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }
    // Will be used by the ArrayAdapter in the ListView
}
