package com.codepath.nytimessearch.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ArticlesResponse {

    @SerializedName("")
    List<Article> articleList;

    public ArticlesResponse() {
        articleList = new ArrayList<>();
    }

    public List<Article> getArticleList() {
        return articleList;
    }
}
