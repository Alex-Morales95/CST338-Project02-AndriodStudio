package com.example.cst338_project02_andriodstudio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.cst338_project02_andriodstudio.database.HealthPalRepository;
import com.example.cst338_project02_andriodstudio.database.entities.User;
import com.example.cst338_project02_andriodstudio.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private HealthPalRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = HealthPalRepository.getRepository(getApplication());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });

        binding.createAccountButton.setOnClickListener(v -> {
            String username = binding.userNameLoginEditText.getText().toString().trim();
            String password = binding.passwordLoginEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter a username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(username, password);
            repository.insertUser(newUser);
            Toast.makeText(this, "Account created! You can now log in.", Toast.LENGTH_SHORT).show();

            binding.userNameLoginEditText.setText("");
            binding.passwordLoginEditText.setText("");
        });
    }

    private void verifyUser() {
        String username = binding.userNameLoginEditText.getText().toString().trim();
        if (username.isEmpty()) {
            ToastMaker("Username should not be blank");
            return;
        }

        LiveData<User> live = repository.getUserByUserName(username);
        live.observe(this, user -> {
            live.removeObservers(this);

            if (user == null) {
                ToastMaker(username + " is not a valid username");
                binding.userNameLoginEditText.setSelection(0);
                return;
            }

            String password = binding.passwordLoginEditText.getText().toString();
            if (password.equals(user.getPassword())) {
                SharedPreferences sp = getApplicationContext()
                        .getSharedPreferences(MainActivity.SHARED_PREFERENCE_USERID_KEY, MODE_PRIVATE);
                sp.edit().putInt(MainActivity.SHARED_PREFERENCE_USERID_VALUE, user.getId()).apply();

                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), user.getId()));
                finish();
            } else {
                ToastMaker("Invalid password");
                binding.passwordLoginEditText.setSelection(0);
            }
        });
    }

    private void ToastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent loginIntentFactory(Context context){
        return new Intent(context, LoginActivity.class);
    }
}

