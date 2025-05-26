package com.example.quicknotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quicknotes.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DataBaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DataBaseHelper(this);

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signupEmail.getText().toString().trim();
                String password = binding.signupPassword.getText().toString().trim();
                String confirmPassword = binding.signupConfirm.getText().toString().trim();

                // Check if any of the fields are empty (email, password, or confirm password)
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    // Display a toast message if any field is empty
                    Toast.makeText(SignupActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                // Check if the email format is valid using a built-in pattern matcher for email addresses
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // Display a toast message if the email format is invalid
                    Toast.makeText(SignupActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
                // Check if the password matches the confirm password field
                else if (!password.equals(confirmPassword)) {
                    // Display a toast message if the passwords do not match
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Check if a user already exists with the entered email in the database
                    Boolean checkUserEmail = databaseHelper.checkEmail(email);

                    if (checkUserEmail) {
                        // Display a toast message if the email is already registered
                        Toast.makeText(SignupActivity.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    } else {
                        // Try to insert the new user into the database
                        Boolean insert = databaseHelper.insertData(email, password);

                        // Choose the appropriate message based on whether the insert was successful or not
                        String message = insert ? "Signup Successful!" : "Signup Failed!";

                        // Display the result message in a toast
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();

                        // If signup is successful, navigate to the MainActivity screen
                        if (insert) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                }

            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}