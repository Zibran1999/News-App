package com.zibran.newsapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.zibran.newsapp.R;
import com.zibran.newsapp.adapters.NewsAdapter;
import com.zibran.newsapp.databinding.ActivityNewsFeedBinding;
import com.zibran.newsapp.interfaces.ApiInterface;
import com.zibran.newsapp.models.NewsApiModel;
import com.zibran.newsapp.models.NewsArticle;
import com.zibran.newsapp.utils.Util;
import com.zibran.newsapp.webService.ApiWebServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    Dialog dialog;
    ActivityNewsFeedBinding binding;
    MaterialAlertDialogBuilder builder;
    NewsAdapter newsAdapter;
    List<NewsArticle> newsArticles;
    ApiInterface apiInterface;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // to prevent the keyboard to be opened automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        /* Initialize Objects*/
        initObjects();
        /* fetch News */
        fetchNews("Tech");
        binding.logOut.setOnClickListener(this);
        binding.searchFeed.setFocusable(false);
        binding.searchFeed.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void initObjects() {
        dialog = Util.getLoadingDialog(this);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        builder = new MaterialAlertDialogBuilder(this);
        newsAdapter = new NewsAdapter(this);
        newsArticles = new ArrayList<>();
        apiInterface = ApiWebServices.getApiInterface();
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


    }

    private void fetchNews(String keyword) {
        dialog.show();
        if (Util.isInternetConnected(this)) {
            Call<NewsApiModel> call = apiInterface.getNewsFromNewsAPI(keyword, date, "popularity", "b3dd4ade458a4e73b0faf138c35807d3");
            call.enqueue(new Callback<NewsApiModel>() {
                @Override
                public void onResponse(@NonNull Call<NewsApiModel> call, @NonNull Response<NewsApiModel> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            newsArticles.clear();
                            newsArticles.addAll(response.body().getArticles());
                            newsAdapter.updateList(newsArticles);
                            binding.newsFeedRv.setAdapter(newsAdapter);
                        } else {
                            Toast.makeText(NewsFeedActivity.this, "No data found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<NewsApiModel> call, @NonNull Throwable t) {
                    dialog.dismiss();

                }
            });
        } else {
            Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
        }


    }

    private void logoutUser() {
        if (Util.isInternetConnected(this)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    dialog.dismiss();
                });
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                dialog.dismiss();

            } else {
                dialog.dismiss();
            }
        } else {
            dialog.dismiss();
            Snackbar.make(binding.getRoot(), "Please Connect to the internet to proceed further.", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        moveTaskToBack(true);
        System.exit(0);
    }

    @Override
    public void onClick(View view) {
        builder.setTitle("Sign out")
                .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_warning_24))
                .setMessage("Do you want to sign out?")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    dialog.show();
                    logoutUser();
                }).setNegativeButton("NO", (dialogInterface, i) -> {
                }).show();

    }
}