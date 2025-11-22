package com.example.cst338_project02_andriodstudio.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cst338_project02_andriodstudio.database.entities.HealthPal;

import java.util.List;

@Dao
public interface HealthPalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HealthPal healthPal);

    @Query("SELECT * FROM " + HealthPalDatabase.HEALTH_PAL_TABLE + " ORDER BY date DESC")
    List<HealthPal> getAllRecords();

    @Query("SELECT * FROM " + HealthPalDatabase.HEALTH_PAL_TABLE + " WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<HealthPal>> getAllLogsByUserId(int userId);

    @Query("SELECT * FROM " + HealthPalDatabase.HEALTH_PAL_TABLE + " WHERE userId = :loggedInUserId ORDER BY date DESC")
    List<HealthPal> getRecordsByUserId(int loggedInUserId);
}

