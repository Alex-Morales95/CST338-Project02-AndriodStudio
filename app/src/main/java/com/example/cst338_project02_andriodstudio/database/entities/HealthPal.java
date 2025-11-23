package com.example.cst338_project02_andriodstudio.database.entities;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cst338_project02_andriodstudio.database.HealthPalDatabase;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity(tableName = HealthPalDatabase.HEALTH_PAL_TABLE)
public class HealthPal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String exercise;

    private double weight;

    private int reps;

    private LocalDateTime date;

    private int userId;

    public HealthPal(String exercise, double weight, int reps, int userId) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
        this.userId = userId;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return exercise + '\n' +
                "weight: " + weight + '\n' +
                "Height: " + reps + '\n' +
                "date: " + date.toString() + '\n' +
                "+ + + + + + + +\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HealthPal healthPal = (HealthPal) o;
        return id == healthPal.id && Double.compare(weight, healthPal.weight) == 0 && reps == healthPal.reps && userId == healthPal.userId && Objects.equals(exercise, healthPal.exercise) && Objects.equals(date, healthPal.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exercise, weight, reps, date, userId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
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