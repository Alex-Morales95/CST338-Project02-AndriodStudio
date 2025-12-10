package com.example.cst338_project02_andriodstudio.database.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_project02_andriodstudio.database.HealthPalDatabase;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = HealthPalDatabase.HEALTH_PAL_TABLE)
public class HealthPal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "weightGoal")
    private String weightGoal;

    @ColumnInfo(name = "activityLevel")
    private double activityLevel;

    @ColumnInfo(name = "height")
    private int height;

//    @ColumnInfo(name = "date")
    private LocalDateTime date;

    private int userId;

    public HealthPal(){}

    public HealthPal(String weight, double activity, int height, int userId) {
        this.weightGoal = weight;
        this.activityLevel = activity;
        this.height = height;
        this.userId = userId;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return   weightGoal + '\n' +
                "activityLevel: " + activityLevel + '\n' +
                "Height: " + height + '\n' +
                "date: " + date.toString() + '\n' +
                "+ + + + + + + +\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HealthPal healthPal = (HealthPal) o;
        return id == healthPal.id && Double.compare(activityLevel, healthPal.activityLevel) == 0 && height == healthPal.height && userId == healthPal.userId && Objects.equals(weightGoal, healthPal.weightGoal) && Objects.equals(date, healthPal.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weightGoal, activityLevel, height, date, userId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weight) {
        this.weightGoal = weight;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(double activity) {
        this.activityLevel = activity ;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

