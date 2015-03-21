/**
 * Mood.java
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

public class MoodTic {
    private int id;
    String date;
    float mood;

    public MoodTic() {}

    public MoodTic(String date, float mood) {
        super();
        this.date = date;
        this.mood = mood;
    }

    @Override
    public String toString() {
        return "Mood [id=" + id + ", date=" + date + ", mood=" + mood + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMood() {
        return mood;
    }

    public void setMood(float mood) {
        this.mood = mood;
    }
}
