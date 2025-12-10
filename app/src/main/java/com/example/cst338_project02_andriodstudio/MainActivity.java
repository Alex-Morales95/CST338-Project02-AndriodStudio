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

        if(loggedInUserId == -1){
            Intent i = LoginActivity.loginIntentFactory(getApplicationContext());
            startActivity(i);
            finish();
            return;
        }

        binding.logDisplayTextView.setMovementMethod(new ScrollingMovementMethod());



        binding.logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationFromDisplay();
                insertHealthPalRecord();

            }
        });

        binding.weightInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.addUserButton.setOnClickListener(v -> handleAddUser());
        binding.removeUserButton.setOnClickListener(v -> handleRemoveUser());
//        binding.viewUserLogsButton.setOnClickListener(v -> handleViewUserLogs());
    }



    private void loginUser(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_USERID_KEY,
                Context.MODE_PRIVATE);
        if(sharedPreferences.contains(SHARED_PREFERENCE_USERID_VALUE)) {
            loggedInUserId = sharedPreferences.getInt(SHARED_PREFERENCE_USERID_VALUE, LOGGED_OUT);
        }
        if(loggedInUserId == LOGGED_OUT & savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)){
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }
        if(loggedInUserId == LOGGED_OUT){
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }
        if(loggedInUserId == LOGGED_OUT){
            return;
        }
        LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, user -> {
            this.user = user;
            if (this.user != null) {

                invalidateOptionsMenu();
                if (this.user.isAdmin()) {
                    binding.adminControlsLayout.setVisibility(View.VISIBLE);
                    binding.userContentLayout.setVisibility(View.GONE);

                    binding.usernameWelcomeTextView.setVisibility(View.GONE);
                } else {
                    binding.adminControlsLayout.setVisibility(View.GONE);
                    binding.userContentLayout.setVisibility(View.VISIBLE);

                    binding.usernameWelcomeTextView.setVisibility(View.VISIBLE);
                    binding.usernameWelcomeTextView.setText("Lets Get Started, " + this.user.getUsername());
                }

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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logoutMenuItem);
        item.setVisible(true);
        if(user == null){
            return false;
        }
        item.setTitle(user.getUsername() + (user.isAdmin() ? " (Admin)" : "" ));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                showLogoutDialog();
                return false;
            }
        });
        return true;
    }

    private void showLogoutDialog(){
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


    static Intent mainActivityIntentFactory(Context context, int userId){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }

    private void insertHealthPalRecord(){
        if(mExercise.isEmpty()){
            return;
        }
        HealthPal log = new HealthPal(mExercise,mActivity,mHeight,loggedInUserId);
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

    private void getInformationFromDisplay(){
        mExercise = binding.weightInputEditText.getText().toString();
        try {
            mActivity = Double.parseDouble(binding.activityInputEditText.getText().toString());
        }catch (NumberFormatException e){
            Log.d("SEC_HEALTH_PAL", "Error reading value from Weight edit text.");
        }

        //mHeight = binding.repInputEditText.getText().toString();
        try {
            mHeight = Integer.parseInt(binding.heightInputEditText.getText().toString());
        }catch (NumberFormatException e){
            Log.d("SEC_HEALTH_PAL", "Error reading value from Reps edit text.");
        }

    }
    private void handleAddUser() {

            // Inflate the dialog layout
            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.add_user, null);

            // Get references to the EditTexts in the dialog
            android.widget.EditText usernameEditText = dialogView.findViewById(R.id.dialogUsernameEditText);
            android.widget.EditText passwordEditText = dialogView.findViewById(R.id.dialogPasswordEditText);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Add New User")
                    .setView(dialogView)
                    .setPositiveButton("Create", (dialog, which) -> {
                        String username = usernameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();

                        if (username.isEmpty() || password.isEmpty()) {
                            android.widget.Toast.makeText(this, "Username and password required", android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Optional: protect admin1 so it stays special
                        if (username.equals("admin1")) {
                            android.widget.Toast.makeText(this, "Username 'admin1' is reserved.", android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }

                        com.example.cst338_project02_andriodstudio.database.entities.User newUser =
                                new com.example.cst338_project02_andriodstudio.database.entities.User(username, password);

                        // Insert through your repository (same pattern as LoginActivity createAccount)
                        repository.insertUser(newUser);

                        android.widget.Toast.makeText(this, "User created!", android.widget.Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
    }
    private void handleRemoveUser(){
            LiveData<java.util.List<com.example.cst338_project02_andriodstudio.database.entities.User>> usersLive =
                    repository.getAllUsersLive();

            usersLive.observe(this, users -> {
                // Stop listening after first result to avoid repeated dialogs
                usersLive.removeObservers(this);

                if (users == null || users.isEmpty()) {
                    android.widget.Toast.makeText(this, "No users to delete", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                // Optional: filter out the main admin so they can't be deleted
                java.util.List<com.example.cst338_project02_andriodstudio.database.entities.User> filtered =
                        new java.util.ArrayList<>();
                for (com.example.cst338_project02_andriodstudio.database.entities.User u : users) {
                    if (u.getUsername().equals("admin1")) {
                        continue; // don't allow deleting the main admin account
                    }
                    filtered.add(u);
                }

                if (filtered.isEmpty()) {
                    android.widget.Toast.makeText(this, "No deletable users", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                // Build list of usernames for the dialog
                String[] usernames = new String[filtered.size()];
                for (int i = 0; i < filtered.size(); i++) {
                    usernames[i] = filtered.get(i).getUsername();
                }

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Select user to delete")
                        .setItems(usernames, (dialog, which) -> {
                            com.example.cst338_project02_andriodstudio.database.entities.User selected = filtered.get(which);

                            // Optional: don’t let logged-in user delete themselves
                            if (selected.getId() == loggedInUserId) {
                                android.widget.Toast.makeText(this, "You can't delete the logged-in user.", android.widget.Toast.LENGTH_SHORT).show();
                                return;
                            }

                            repository.deleteUser(selected);
                            android.widget.Toast.makeText(this, "Deleted " + selected.getUsername(), android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
    }
    private void handleViewUserLogs() {
    }
}