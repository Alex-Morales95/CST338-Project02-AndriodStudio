package com.example.cst338_project02_andriodstudio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.cst338_project02_andriodstudio.database.HealthPalRepository;
import com.example.cst338_project02_andriodstudio.database.entities.HealthPal;
import com.example.cst338_project02_andriodstudio.database.entities.User;
import com.example.cst338_project02_andriodstudio.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_USER_ID = "com.example.cst338_project02_andriodstudio.MAIN_ACTIVITY_USER_ID";
    private static final int LOGGED_OUT = -1;
    static final String SHARED_PREFERENCE_USERID_KEY = "com.example.cst338_project02_andriodstudio.SHARED_PREFERENCE_USERID_KEY";
    static final String SHARED_PREFERENCE_USERID_VALUE = "com.example.cst338_project02_andriodstudio.SHARED_PREFERENCE_USERID_VALUE";
    private static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.cst338_project02_andriodstudio.SAVED_INSTANCE_STATE_USERID_KEY";
    private ActivityMainBinding binding;
    private HealthPalRepository repository;
    public static final String TAG = "SEC_HEALTH_PAL";
    String mExercise = "";
    double mActivity = 0.0;
    int mHeight = 0;
    private int loggedInUserId = -1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.cst338_project02_andriodstudio.databinding.ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        repository = HealthPalRepository.getRepository(getApplication());
        loginUser(savedInstanceState);

        if (loggedInUserId == -1) {
            Intent i = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(i);
            finish();
            return;
        }

        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());

        binding.logButton.setOnClickListener(v -> {
            String activityLevelText = binding.activityInputEditText.getText().toString().trim();
            String goalText = binding.weightInputEditText.getText().toString().trim();
            String heightText = binding.heightInputEditText.getText().toString().trim();
            String weightNumberText = binding.currentWeightInputEditText.getText().toString().trim();

            int height;
            double weight;
            height = Integer.parseInt(heightText);
            weight = Double.parseDouble(weightNumberText);


            DailyGoals goals = calculateDailyGoals(activityLevelText, goalText, weight, height);

            SharedPreferences goalPrefs = getApplicationContext()
                    .getSharedPreferences("GOALS_PREFS", MODE_PRIVATE);

            String prefix = "USER_" + loggedInUserId + "_";

            goalPrefs.edit()
                    .putFloat(prefix + "CAL_GOAL", (float) goals.caloriesGoal)
                    .putFloat(prefix + "PROTEIN_GOAL", (float) goals.proteinGoal)
                    .putFloat(prefix + "CARBS_GOAL", (float) goals.carbsGoal)
                    .putFloat(prefix + "FAT_GOAL", (float) goals.fatGoal)
                    .apply();

            Intent intent = TrackingActivity.trackingIntentFactory(
                    MainActivity.this,
                    loggedInUserId,
                    goals.caloriesGoal,
                    goals.proteinGoal,
                    goals.carbsGoal,
                    goals.fatGoal
            );
            startActivity(intent);
        });

        binding.weightInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loginUser(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_USERID_KEY,
                Context.MODE_PRIVATE);
        if (sharedPreferences.contains(SHARED_PREFERENCE_USERID_VALUE)) {
            loggedInUserId = sharedPreferences.getInt(SHARED_PREFERENCE_USERID_VALUE, LOGGED_OUT);
        }
        if (loggedInUserId == LOGGED_OUT & savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)) {
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }
        if (loggedInUserId == LOGGED_OUT) {
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }
        if (loggedInUserId == LOGGED_OUT) {
            return;
        }
        LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {
                invalidateOptionsMenu();
            } else {
                logout();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_USERID_KEY, loggedInUserId);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_USERID_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(MainActivity.SHARED_PREFERENCE_USERID_VALUE, loggedInUserId);
        sharedPrefEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if (user == null) {
            return false;
        }
        item.setTitle(user.getUsername());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showLogoutDialog();
                return false;
            }
        });
        return true;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog alertDialog = alertBuilder.create();

        alertBuilder.setMessage("Logout?");
        alertBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    private void logout() {
        SharedPreferences sp = getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCE_USERID_KEY, Context.MODE_PRIVATE);
        sp.edit().remove(SHARED_PREFERENCE_USERID_VALUE).apply();

        getIntent().removeExtra(MAIN_ACTIVITY_USER_ID);

        loggedInUserId = -1;
        user = null;

        Intent i = LoginActivity.loginIntentFactory(getApplicationContext());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void onLogoutClick(View view) {
        logout();
    }


    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    private void insertHealthPalRecord() {
        if (mExercise.isEmpty()) {
            return;
        }
        HealthPal log = new HealthPal(mExercise, mActivity, mHeight, loggedInUserId);
        repository.insertHealthPal(log);
    }

    private void observeUserLogs() {
        repository.getAllLogsByUserIdLive(loggedInUserId).observe(this, logs -> {
            if (logs == null || logs.isEmpty()) {
                binding.logDisplayTextView.setText(R.string.nothing_to_show_time_to_hit_the_gym);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (HealthPal log : logs) {
                sb.append(log.toString());
            }
            binding.logDisplayTextView.setText(sb.toString());
        });
    }

    private void getInformationFromDisplay() {
        mExercise = binding.weightInputEditText.getText().toString();
        try {
            mActivity = Double.parseDouble(binding.activityInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("SEC_HEALTH_PAL", "Error reading value from Weight edit text.");
        }


        try {
            mHeight = Integer.parseInt(binding.heightInputEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.d("SEC_HEALTH_PAL", "Error reading value from Reps edit text.");
        }

    }

    private static class DailyGoals {
        double caloriesGoal;
        double proteinGoal;
        double carbsGoal;
        double fatGoal;

        DailyGoals(double caloriesGoal, double proteinGoal, double carbsGoal, double fatGoal) {
            this.caloriesGoal = caloriesGoal;
            this.proteinGoal = proteinGoal;
            this.carbsGoal = carbsGoal;
            this.fatGoal = fatGoal;
        }
    }

    private DailyGoals calculateDailyGoals(String activityLevelText,
                                           String goalText,
                                           double weight,
                                           int height) {

        double weightKg = weight * 0.453592;
        double heightCm = height * 2.54;

        int age = 30;

        double bmr = 10.0 * weightKg + 6.25 * heightCm - 5.0 * age - 78.0;

        double activityMultiplier;
        String level = activityLevelText.toLowerCase();

        if (level.equals("low")) {
            activityMultiplier = 1.2;
        } else if (level.equals("average")) {
            activityMultiplier = 1.5;
        } else if (level.equals("high")) {
            activityMultiplier = 1.75;
        } else {
            activityMultiplier = 1.2;
        }

        double maintenanceCalories = bmr * activityMultiplier;

        double caloriesGoal;
        String goal = goalText.toLowerCase();

        if (goal.equals("lose")) {
            caloriesGoal = maintenanceCalories - 500.0;
        } else if (goal.equals("gain")) {
            caloriesGoal = maintenanceCalories + 300.0;
        } else {
            caloriesGoal = maintenanceCalories;
        }

        double proteinGoalGrams = 0.8 * weight;
        double fatGoalGrams = 0.4 * weight;

        double caloriesFromProtein = proteinGoalGrams * 4.0;
        double caloriesFromFat = fatGoalGrams * 9.0;

        double caloriesForCarbs = caloriesGoal - (caloriesFromProtein + caloriesFromFat);
        if (caloriesForCarbs < 0) {
            caloriesForCarbs = 0;
        }
        double carbsGoalGrams = caloriesForCarbs / 4.0;

        double roundedCalories = Math.round(caloriesGoal);
        double roundedProtein = Math.round(proteinGoalGrams);
        double roundedCarbs = Math.round(carbsGoalGrams);
        double roundedFat = Math.round(fatGoalGrams);

        return new DailyGoals(roundedCalories, roundedProtein, roundedCarbs, roundedFat);
    }
}