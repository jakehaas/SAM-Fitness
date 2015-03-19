/**
 * Achievement.java
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

public class Achievement {
    private int id;
    private String name;
    private String description;

    /**
     * Get the id of this achievement.
     *
     * @return The current id of this achievement.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of this achievement.
     *
     * @return The current name of this achievement.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of this achievement.
     *
     * @return The current description of this achievement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Changes the id of this achievement.
     *
     * @param id The new id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Changes the name of this achievement.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Changes the description of this achievement.
     *
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}