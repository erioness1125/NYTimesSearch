package com.codepath.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article {

    private String webUrl;
    private String headline;
    private String thumbNail;

    private static final String HEADLINE = "headline";
    private static final String MAIN = "main";
    private static final String MULTIMEDIA = "multimedia";
    private static final String URL = "url";
    private static final String WEB_URL = "web_url";

    public Article(JSONObject articleJson) {
        try {
            this.webUrl = articleJson.optString(WEB_URL);

            JSONObject headLineJson = articleJson.optJSONObject(HEADLINE);
            if (headLineJson != null)
                this.headline = headLineJson.optString(MAIN);

            JSONArray multiMediaArr = articleJson.optJSONArray(MULTIMEDIA);
            if (multiMediaArr != null && multiMediaArr.length() > 0) {
                JSONObject multiMediaJson = multiMediaArr.getJSONObject(0);
                String thumbNailUrl = multiMediaJson.optString(URL);
                if (!thumbNailUrl.trim().isEmpty())
                    this.thumbNail = "http://www.nytimes.com/" + thumbNailUrl;
                else
                    this.thumbNail = "";
            } else
                this.thumbNail = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject articleJson = array.optJSONObject(i);
            if (articleJson != null)
                results.add(new Article(articleJson));
        }

        return results;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }
}
