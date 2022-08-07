package com.zibran.newsapp.activities;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.zibran.newsapp.R;
import com.zibran.newsapp.databinding.ActivityLoginBinding;
import com.zibran.newsapp.utils.Util;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{4,}" +                // at least 4 characters
                    "$");
    private final int RC_SIGN_IN = 101;
    String emailInput, passwordInput;
    ActivityLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Dialog dialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to prevent the keyboard to be opened automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Initialize Objects*/
        initObjects();
        /* get User Info*/
        getLoginInfo();

        binding.signUpTv.setOnClickListener(this);
        binding.loginWithGoogle.setOnClickListener(this);

    }

    private void initObjects() {
        dialog = Util.getLoadingDialog(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();

    }


    private void signIn() {
        if (Util.isInternetConnected(this)) {
            dialog.dismiss();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                startActivity(new Intent(this, NewsFeedActivity.class));
                finish();
            } catch (ApiException e) {

                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            }

        }
    }


    private void getLoginInfo() {
        binding.signInBtn.setOnClickListener(view -> {
            dialog.show();
            if (!validateEmailAddress() | !validatePassword()) {
                dialog.dismiss();
            } else {

                if (Util.isInternetConnected(this)) {
                    auth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, NewsFeedActivity.class));
                            finish();
                        }
                        dialog.dismiss();

                    }).addOnFailureListener(e -> Toast.makeText(this, "No User found.", Toast.LENGTH_SHORT).show());


                } else {
                    Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
                    dialog.dismiss();
                }


            }


        });
    }


    private boolean validateEmailAddress() {

        // Extract input from EditText
        emailInput = binding.loginEmailEdt.getText().toString().trim();

        // if the email input field is empty
        if (emailInput.isEmpty()) {
            binding.loginEmailEdt.setError("Field can not be empty");
            return false;
        }

        // Matching the input email to a predefined email pattern
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            binding.loginEmailEdt.setError("Please enter a valid email address");
            return false;
        } else {
            binding.loginEmailEdt.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        passwordInput = binding.loginPasswordEdt.getText().toString().trim();
        // if password field is empty
        // it will display error message "Field can not be empty"
        if (passwordInput.isEmpty()) {
            binding.loginPasswordEdt.setError("Field can not be empty");
            return false;
        }

        // if password does not matches to the pattern
        // it will display an error message "Password is too weak"
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            binding.loginPasswordEdt.setError("Password is too weak");
            return false;
        } else {
            binding.loginPasswordEdt.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_up_tv) {
            startActivity(new Intent(this, SignUpScreen.class));
        } else if (view.getId() == R.id.login_with_google) {
            dialog.show();
            signIn();
        }

    }
}