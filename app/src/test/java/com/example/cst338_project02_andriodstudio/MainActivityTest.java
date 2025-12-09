package com.example.cst338_project02_andriodstudio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainActivityTest {

    @Test
    public void calculateDailyGoals_averageMaintain_isReasonable() {
        MainActivity.DailyGoals goals =
                MainActivity.calculateDailyGoals("average", "maintain", 180.0, 70);

        assertEquals(2550.0, goals.caloriesGoal, 150.0);
        assertEquals(144.0, goals.proteinGoal, 15.0);
        assertEquals(72.0,  goals.fatGoal,     10.0);
        assertEquals(330.0, goals.carbsGoal,   80.0);
    }

    @Test
    public void calculateDailyGoals_lowLose_isReasonable() {
        MainActivity.DailyGoals goals =
                MainActivity.calculateDailyGoals("low", "lose", 150.0, 65);

        assertEquals(1300.0, goals.caloriesGoal, 150.0);
        assertEquals(120.0,  goals.proteinGoal,  15.0);
        assertEquals(60.0,   goals.fatGoal,      10.0);
        assertEquals(70.0,   goals.carbsGoal,    40.0);
    }
}
