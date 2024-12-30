package com.example.minimalhome.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minimalhome.R;
import com.example.minimalhome.ui.home.HomeActivity;
import com.example.minimalhome.service.AuthService;
import com.example.minimalhome.util.JwtUtil;
import com.example.minimalhome.util.PreferencesUtil;

class LoginActivity extends AppCompatActivity {

    // UI Elements
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private ProgressBar progressBar;

    // Service for Authentication
    private AuthService authService;

    // Utilities
    private JwtUtil jwtUtil;
    private PreferencesUtil preferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);

        // Initialize services and utilities
        authService = new AuthService();
        preferencesUtil = new PreferencesUtil(this);
        jwtUtil = new JwtUtil();

        // Set up login button click listener
        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt login
            attemptLogin(username, password);
        });

        // Set up register link click listener
        registerLink.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin(String username, String password) {
        // Show loading state
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        try {
            authService.login(username, password, new AuthService.AuthCallback() {
                @Override
                public void onSuccess(String token) {
                    // Save token and navigate to home
                    preferencesUtil.saveAuthToken(token);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish(); // Close login activity
                }

                @Override
                public void onError(String message) {
                    // Show error message
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onComplete() {
                    // Reset loading state
                    runOnUiThread(() -> {
                        loginButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    });
                }
            });
        } catch (Exception e) {
            // Handle unexpected errors
            Toast.makeText(this, "Network error occurred", Toast.LENGTH_LONG).show();
            loginButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}









































