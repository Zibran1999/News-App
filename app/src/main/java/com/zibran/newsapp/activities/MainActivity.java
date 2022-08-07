package com.zibran.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zibran.newsapp.databinding.ActivityMainBinding;
import com.zibran.newsapp.utils.Util;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SplashScreen.installSplashScreen(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (Util.isInternetConnected(this)) {
            checkGoogleSignedInUser();
        } else {
            Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
        }


    }

    private void checkGoogleSignedInUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(this, NewsFeedActivity.class));
            finish();

        } else {
            checkSignedInUser();
        }
    }

    private void checkSignedInUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, NewsFeedActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));

        }
        finish();
    }
}