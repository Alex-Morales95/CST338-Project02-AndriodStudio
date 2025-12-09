package com.example.cst338_project02_andriodstudio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cst338_project02_andriodstudio.databinding.ActivityTrackingBinding;

public class TrackingActivity extends AppCompatActivity {

    private static final String USER_ID_KEY = "TrackingActivity.USER_ID";
    private static final String CAL_GOAL_KEY = "TrackingActivity.CAL_GOAL";
    private static final String PROTEIN_GOAL_KEY = "TrackingActivity.PROTEIN_GOAL";
    private static final String CARBS_GOAL_KEY = "TrackingActivity.CARBS_GOAL";
    private static final String FAT_GOAL_KEY = "TrackingActivity.FAT_GOAL";

    private ActivityTrackingBinding binding;

    private int userId;
    private double caloriesGoal;
    private double proteinGoal;
    private double carbsGoal;
    private double fatGoal;
    private SharedPreferences goalPrefs;

    private double currentCalories = 0;
    private double currentProtein = 0;
    private double currentCarbs = 0;
    private double currentFat = 0;


    public static Intent trackingIntentFactory(Context context,
                                               int userId,
                                               double caloriesGoal,
                                               double proteinGoal,
                                               double carbsGoal,
                                               double fatGoal) {
        Intent intent = new Intent(context, TrackingActivity.class);
        intent.putExtra(USER_ID_KEY, userId);
        intent.putExtra(CAL_GOAL_KEY, caloriesGoal);
        intent.putExtra(PROTEIN_GOAL_KEY, proteinGoal);
        intent.putExtra(CARBS_GOAL_KEY, carbsGoal);
        intent.putExtra(FAT_GOAL_KEY, fatGoal);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        goalPrefs = getApplicationContext()
                .getSharedPreferences("GOALS_PREFS", MODE_PRIVATE);

        Intent intent = getIntent();
        userId = intent.getIntExtra(USER_ID_KEY, -1);
        caloriesGoal = intent.getDoubleExtra(CAL_GOAL_KEY, 0);
        proteinGoal = intent.getDoubleExtra(PROTEIN_GOAL_KEY, 0);
        carbsGoal = intent.getDoubleExtra(CARBS_GOAL_KEY, 0);
        fatGoal = intent.getDoubleExtra(FAT_GOAL_KEY, 0);

        loadCurrentProgressFromPrefs();

        binding.proteinGoalTextView.setText(String.format("Goal: %.0f g", proteinGoal));
        binding.carbsGoalTextView.setText(String.format("Goal: %.0f g", carbsGoal));
        binding.fatGoalTextView.setText(String.format("Goal: %.0f g", fatGoal));
        binding.caloriesGoalTextView.setText(String.format("Goal: %.0f", caloriesGoal));

        updateProgressSummary();

        binding.trackProgressButton.setOnClickListener(v -> trackProgress());

        binding.resetProgressButton.setOnClickListener(v -> {
            currentCalories = 0;
            currentProtein = 0;
            currentCarbs = 0;
            currentFat = 0;
            saveCurrentProgressToPrefs();
            updateProgressSummary();
        });

        binding.createNewGoalButton.setOnClickListener(v -> {
            Intent mainIntent = MainActivity.mainActivityIntentFactory(
                    TrackingActivity.this,
                    userId
            );
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();
        });

        // TODO: WIRE STEP COUNT BUTTON
        binding.openStepCounterButton.setOnClickListener(v -> {

        });

        binding.logoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getApplicationContext()
                    .getSharedPreferences(MainActivity.SHARED_PREFERENCE_USERID_KEY, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(MainActivity.SHARED_PREFERENCE_USERID_VALUE);
            editor.apply();

            getIntent().removeExtra(USER_ID_KEY);

            Intent loginIntent = LoginActivity.loginIntentFactory(getApplicationContext());
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(loginIntent);
            finish();
        });
    }

    private void trackProgress() {
        double newProtein = parseDoubleOrZero(binding.proteinInputEditText.getText().toString());
        double newCarbs   = parseDoubleOrZero(binding.carbsInputEditText.getText().toString());
        double newFat     = parseDoubleOrZero(binding.fatInputEditText.getText().toString());
        double newCalories= parseDoubleOrZero(binding.caloriesInputEditText.getText().toString());

        currentProtein  += newProtein;
        currentCarbs    += newCarbs;
        currentFat      += newFat;
        currentCalories += newCalories;

        binding.proteinInputEditText.setText("");
        binding.carbsInputEditText.setText("");
        binding.fatInputEditText.setText("");
        binding.caloriesInputEditText.setText("");

        saveCurrentProgressToPrefs();

        updateProgressSummary();
    }

    private double parseDoubleOrZero(String s) {
        if (s == null || s.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void updateProgressSummary() {
        String summary = String.format(
                "Calories: %.0f / %.0f\n" +
                        "Protein: %.0f g / %.0f g\n" +
                        "Carbs: %.0f g / %.0f g\n" +
                        "Fat: %.0f g / %.0f g",
                currentCalories, caloriesGoal,
                currentProtein, proteinGoal,
                currentCarbs, carbsGoal,
                currentFat, fatGoal
        );
        binding.progressSummaryTextView.setText(summary);
    }

    private void loadCurrentProgressFromPrefs() {
        if (userId == -1 || goalPrefs == null) return;

        String prefix = "USER_" + userId + "_";
        currentCalories = goalPrefs.getFloat(prefix + "CURRENT_CAL", 0f);
        currentProtein  = goalPrefs.getFloat(prefix + "CURRENT_PROTEIN", 0f);
        currentCarbs    = goalPrefs.getFloat(prefix + "CURRENT_CARBS", 0f);
        currentFat      = goalPrefs.getFloat(prefix + "CURRENT_FAT", 0f);
    }

    private void saveCurrentProgressToPrefs() {
        if (userId == -1 || goalPrefs == null) return;

        String prefix = "USER_" + userId + "_";
        goalPrefs.edit()
                .putFloat(prefix + "CURRENT_CAL", (float) currentCalories)
                .putFloat(prefix + "CURRENT_PROTEIN", (float) currentProtein)
                .putFloat(prefix + "CURRENT_CARBS", (float) currentCarbs)
                .putFloat(prefix + "CURRENT_FAT", (float) currentFat)
                .apply();
    }
}