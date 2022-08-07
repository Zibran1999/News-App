package com.zibran.newsapp.activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zibran.newsapp.R;
import com.zibran.newsapp.databinding.ActivitySignUpScreenBinding;
import com.zibran.newsapp.utils.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{4,}" +                // at least 4 characters
                    "$");

    ActivitySignUpScreenBinding binding;
    String name, emailInput, passwordInput, numberInput;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to prevent the keyboard to be opened automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivitySignUpScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Initialize Objects*/
        initObjects();

        binding.signInTV.setOnClickListener(this);
        binding.signUpBtn.setOnClickListener(this);
    }

    private void initObjects() {
        db = FirebaseFirestore.getInstance();
        dialog = Util.getLoadingDialog(this);
        mAuth = FirebaseAuth.getInstance();

    }

    private void SignUp() {
        if (!validateName() | !validateEmailAddress() | !validatePassword() | !validatePhoneNumber()) {
            return;
        } else {

            if (Util.isInternetConnected(this)) {
                dialog.show();
                mAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", emailInput);
                        user.put("password", passwordInput);
                        user.put("number", numberInput);

                        documentReference = db.collection("users").document(userId);

                        documentReference.set(user)
                                .addOnSuccessListener(documentReference -> {
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                    }
                });

            } else {
                dialog.dismiss();
                Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
            }


        }

    }

    private boolean validateName() {

        // Extract input from EditText
        name = binding.name.getText().toString().trim();

        // if the email input field is empty
        if (name.isEmpty()) {
            binding.name.setError("Field can not be empty");
            return false;
        } else {
            binding.name.setError(null);
            return true;
        }
    }

    private boolean validateEmailAddress() {

        // Extract input from EditText
        emailInput = binding.email.getText().toString().trim();

        // if the email input field is empty
        if (emailInput.isEmpty()) {
            binding.email.setError("Field can not be empty");
            return false;
        }

        // Matching the input email to a predefined email pattern
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            binding.email.setError("Please enter a valid email address");
            return false;
        } else {
            binding.email.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {

        // Extract input from EditText
        numberInput = binding.number.getText().toString().trim();

        // if the email input field is empty
        if (numberInput.isEmpty()) {
            binding.number.setError("Field can not be empty");
            return false;
        }

        // Matching the input email to a predefined email pattern
        else if (!Patterns.PHONE.matcher(numberInput).matches()) {
            binding.number.setError("Please enter a valid number");
            return false;
        } else {
            binding.number.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        passwordInput = binding.password.getText().toString().trim();
        // if password field is empty
        // it will display error message "Field can not be empty"
        if (passwordInput.isEmpty()) {
            binding.password.setError("Field can not be empty");
            return false;
        }

        // if password does not matches to the pattern
        // it will display an error message "Password is too weak"
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            binding.password.setError("Password is too weak");
            return false;
        } else {
            binding.password.setError(null);
            return true;
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_btn) {
            SignUp();
        } else if (view.getId() == R.id.signInTV) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}