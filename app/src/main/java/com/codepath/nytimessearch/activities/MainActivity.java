package com.codepath.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticlesArrayAdapter;
import com.codepath.nytimessearch.fragments.SettingsDialogFragment;
import com.codepath.nytimessearch.models.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements SettingsDialogFragment.SettingsDialogListener {

    @Bind(R.id.gvResults) GridView gvResults;
    @Bind(R.id.rlError) RelativeLayout rlError;
    @Bind(R.id.tvError) TextView tvError;
    @Bind(R.id.searchToolBar) Toolbar toolBar;

    private ArrayList<Article> articles;
    private ArticlesArrayAdapter adapter;

    private List<String> newsDeskList;
    private String beginDate;
    private String sort;

    // NY Times API Parameter keys
    private static final String API_KEY = "api-key";
    private static final String BEGIN_DATE = "begin_date";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String FQ = "fq";
    private static final String NEWS_DESK = "news_desk";
    private static final String PAGE = "page";
    private static final String Q = "q";
    private static final String SORT = "sort";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolBar);
        setupViews();
    }

    private void setupViews() {
        articles = new ArrayList<>();
        adapter = new ArticlesArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass in that article into intent
                i.putExtra(getString(R.string.url), article.getWebUrl());
                // launch the activity
                startActivity(i);
            }
        });
        // by default rlError is not visible
        rlError.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // search icon
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!isNetworkAvailable()) {
                    gvResults.setVisibility(View.INVISIBLE);
                    tvError.setText("Network is not currently available!");
                    rlError.setVisibility(View.VISIBLE);
                } else if (!isOnline()) {
                    gvResults.setVisibility(View.INVISIBLE);
                    tvError.setText("Device is not connected to the Internet!");
                    rlError.setVisibility(View.VISIBLE);
                } else {
                    // make sure to hide rlError
                    rlError.setVisibility(View.INVISIBLE);

                    // make sure to show gvResults
                    gvResults.setVisibility(View.VISIBLE);

                    // perform query here
                    onArticleSearch(query);

                    // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // settings icon
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showEditDialog();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private void onArticleSearch(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.add(API_KEY, getString(R.string.nytimes_api_key));
        params.add(PAGE, "0");
        params.add(Q, query);
        // add more from settings only if exists
        if (beginDate != null && !beginDate.trim().isEmpty())
            params.add(BEGIN_DATE, beginDate);
        String newsDeskQuery = buildNewsDeskQuery();
        if (!newsDeskQuery.isEmpty())
            params.add(FQ, newsDeskQuery);
        if (sort != null && !sort.trim().isEmpty())
            params.add(SORT, sort.toLowerCase());

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                JSONObject responseJson = response.optJSONObject(getString(R.string.response));
                if (responseJson != null) {
                    articleJsonResults = responseJson.optJSONArray(getString(R.string.docs));
                }
                adapter.clear();
                adapter.addAll(Article.fromJSONArray(articleJsonResults));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("bug", errorResponse.toString());
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment settingsDialogFragment = SettingsDialogFragment.newInstance();
        settingsDialogFragment.setNewsDesk(newsDeskList);
        settingsDialogFragment.setSort(sort);
        settingsDialogFragment.show(fm, "fragment_edit_settings");
    }

    private String buildNewsDeskQuery() {
        String newsDesk = "";

        if (newsDeskList != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : newsDeskList) {
                sb.append(DOUBLE_QUOTE).append(s).append(DOUBLE_QUOTE).append(" ");
            }
            newsDesk = sb.toString().trim();
        }
        if (!newsDesk.isEmpty())
            newsDesk = NEWS_DESK + ":(" + newsDesk + ")";
        return newsDesk;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void onDone(String beginDate, List<String> newsDeskList, String sort) {
        this.beginDate = beginDate;
        this.newsDeskList = newsDeskList;
        this.sort = sort;
    }
}