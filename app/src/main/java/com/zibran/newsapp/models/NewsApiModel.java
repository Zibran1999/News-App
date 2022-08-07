package com.zibran.newsapp.models;

import java.io.Serializable;
import java.util.List;

public class NewsApiModel {

    private String status;

    private Integer totalResults;

    private List<NewsArticle> articles = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<NewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticle> articles) {
        this.articles = articles;
    }

}

