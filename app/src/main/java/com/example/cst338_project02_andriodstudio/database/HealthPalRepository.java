package com.example.cst338_project02_andriodstudio.database;


import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.cst338_project02_andriodstudio.database.entities.HealthPal;
import com.example.cst338_project02_andriodstudio.MainActivity;
import com.example.cst338_project02_andriodstudio.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HealthPalRepository {
    private final HealthPalDAO healthPalDAO;
    private final UserDAO userDAO;

    private static HealthPalRepository repository;

    private HealthPalRepository(Application application){
        HealthPalDatabase db = HealthPalDatabase.getInstance(application);
        this.healthPalDAO = db.healthPalDAO();
        this.userDAO = db.userDAO();
    }

    public LiveData<List<HealthPal>> getAllLogsByUserIdLive(int userId) {
        return healthPalDAO.getAllLogsByUserId(userId);
    }

    public static synchronized HealthPalRepository getRepository(Application application) {
        if (repository == null) {
            repository = new HealthPalRepository(application);
        }
        return repository;
    }

    public ArrayList<HealthPal> getAllLogs(){
        Future<ArrayList<HealthPal>> future = HealthPalDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<HealthPal>>() {
                    @Override
                    public ArrayList<HealthPal> call() throws Exception {
                        return (ArrayList<HealthPal>) healthPalDAO.getAllRecords();
                    }
                });
        try{
            return future.get();
        }catch (InterruptedException | ExecutionException e){
            Log.i(MainActivity.TAG, "Problem when getting all HealthPalLogs in the repository");
        }
        return null;
    }

    public void insertHealthPal(HealthPal gymLog) {
        HealthPalDatabase.databaseWriteExecutor.execute(() ->
        {
            healthPalDAO.insert(gymLog);
        });
    }

    public void insertUser(User... user) {
        HealthPalDatabase.databaseWriteExecutor.execute(() ->
        {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }

    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId);
    }

    public ArrayList<HealthPal> getAllLogsByUserId(int loggedInUserId) {
        Future<ArrayList<HealthPal>> future = HealthPalDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<HealthPal>>() {
                    @Override
                    public ArrayList<HealthPal> call() throws Exception {
                        return (ArrayList<HealthPal>) healthPalDAO.getRecordsByUserId(loggedInUserId);
                    }
                });
        try{
            return future.get();
        }catch (InterruptedException | ExecutionException e){
            Log.i(MainActivity.TAG, "Problem when getting all HealthPalLogs in the repository");
        }
        return null;
    }
    public LiveData<List<User>> getAllUsersLive() {
        return userDAO.getAllUsers();
    }
    public void deleteUser(User user) {
        HealthPalDatabase.databaseWriteExecutor.execute(() -> {
            userDAO.delete(user);
        });
    }
}

