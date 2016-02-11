package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, ArrayList<Article> objects) {
        super(context, R.layout.support_simple_spinner_dropdown_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);

        // check to see if existing view is reused
        // not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        // find the imageView
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);

        // clear out recycled image from convertView from last time
        ivImage.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        String thumbNail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbNail)) {
            Picasso.with(getContext()).load(thumbNail).into(ivImage);
        }

        return convertView;
    }
}
