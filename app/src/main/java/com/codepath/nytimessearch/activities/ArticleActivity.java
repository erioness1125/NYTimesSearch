package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codepath.nytimessearch.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity {

    @Bind(R.id.wvArticle) WebView wvArticle;
    @Bind(R.id.tbArticle) Toolbar toolbar;

    private Intent shareIntent;
    private ShareActionProvider miShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra(getString(R.string.url));
        wvArticle.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wvArticle.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);

        // search icon
        MenuItem shareItem = menu.findItem(R.id.action_share);

        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        // Attach share event to the menu item provider
        miShareAction.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }
}
