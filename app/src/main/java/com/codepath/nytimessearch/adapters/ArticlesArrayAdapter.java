package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticlesArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TEXT_IMAGE = 1;
    private final int TEXT = 2;

    private Context context;
    private List<Article> articleList;

    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArticlesArrayAdapter(List<Article> articleList) {
        this.articleList = articleList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case TEXT:
                View txtView = inflater.inflate(R.layout.item_article_text, parent, false);
                viewHolder = new TextViewHolder(txtView);
                break;
            case TEXT_IMAGE:
                View txtImgView = inflater.inflate(R.layout.item_article_text_image, parent, false);
                viewHolder = new ImageTextViewHolder(txtImgView);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.item_article_text_image, parent, false);
                viewHolder = new ImageTextViewHolder(defaultView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = articleList.get(position);
        String headLine = article.getHeadline();

        switch (viewHolder.getItemViewType()) {
            case TEXT:
                TextViewHolder txtViewHolder = (TextViewHolder) viewHolder;
                txtViewHolder.tvTitle.setText(headLine);
                break;
            case TEXT_IMAGE:
                ImageTextViewHolder imgTxtViewHolder = (ImageTextViewHolder) viewHolder;
                imgTxtViewHolder.tvTitle.setText(headLine);
                imgTxtViewHolder.ivImage.setImageResource(0);
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                Picasso.with(context)
                        .load(article.getThumbNail())
                        .into(imgTxtViewHolder.ivImage);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = articleList.get(position);
        if (article.getThumbNail().trim().isEmpty())
            return TEXT; // -> TextViewHolder
        else
            return TEXT_IMAGE; // -> ImageTextViewHolder
    }

    public void clearArticles() {
        this.articleList.clear();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    // for the item with both thumbnail(image) and title
    static class ImageTextViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivImageInImgTxtView) ImageView ivImage;
        @Bind(R.id.tvTitleInImgTxtView) TextView tvTitle;

        public ImageTextViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    // for the item with title only
    static class TextViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvTitleInTxtView) TextView tvTitle;

        public TextViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}
