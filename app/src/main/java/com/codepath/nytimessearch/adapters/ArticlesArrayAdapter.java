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

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticlesArrayAdapter extends ArrayAdapter<Article> {

    public ArticlesArrayAdapter(Context context, ArrayList<Article> objects) {
        super(context, R.layout.content_main, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        ViewHolder viewHolder = null;

        // check to see if existing view is reused
        // not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(article.getHeadline());

        // reset image
        viewHolder.ivImage.setImageResource(R.drawable.ic_action_pic);

        String thumbNail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbNail)) {
            Picasso.with(getContext()).load(thumbNail).into(viewHolder.ivImage);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.ivImage) ImageView ivImage;
        @Bind(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
