package com.zibran.newsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.zibran.newsapp.R;
import com.zibran.newsapp.databinding.NewsFeedLayoutBinding;
import com.zibran.newsapp.models.NewsArticle;
import com.zibran.newsapp.models.NewsSource;
import com.zibran.newsapp.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    List<NewsArticle> newsArticles = new ArrayList<>();
    Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.news_feed_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        holder.setData(position);

    }

    public void updateList(List<NewsArticle> articleList) {
        newsArticles.clear();
        newsArticles.addAll(articleList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsArticles.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        NewsFeedLayoutBinding binding;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = NewsFeedLayoutBinding.bind(itemView);
        }

        public void setData(int position) {

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();

            binding.time.setText(Util.convertTimeToText(newsArticles.get(position).getPublishedAt()));
            binding.title.setText(newsArticles.get(position).getTitle());
            NewsSource source = newsArticles.get(position).getSource();
            binding.sourceName.setText(source.getName());
            binding.description.setText(newsArticles.get(position).getDescription());
            Glide.with(context).load(newsArticles.get(position).getUrlToImage()).placeholder(circularProgressDrawable).into(binding.imageView2);
        }
    }
}
