package com.kaamel.nytimessearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by kaamel on 9/19/17.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {



    List<? extends Article> articles;
    Context context;
    public SearchResultsAdapter(SearchActivity context, List<? extends Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom spinner_sort_order_item
        View articleView;
        if (viewType == 0)
            articleView = inflater.inflate(R.layout.item_article, parent, false);
        else
            articleView = inflater.inflate(R.layout.item_article_no_thumbnail, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = articles.get(position);
        if (article.getThumbNail() != null) {
            Glide.with(context)
                    .load("https://static01.nyt.com/" + article.getThumbNail())
                    .placeholder(R.drawable.nyt_logo)
                    .error(R.drawable.ic_error_outline_deep_orange_a200_48dp)
                    .into(holder.ivTumbnail);
        }
        if (article.getHeadline() != null) {
            holder.tvHeadline.setText(article.getHeadline());
            holder.tvHeadline.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvHeadline.setVisibility(View.GONE);
        }
        holder.tvSnippet.setText(Html.fromHtml(article.getSnippet()));

    }

    List<Article> getArticles() {
        return (List<Article>) articles;
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return articles.get(position).getThumbNail() == null?1:0;
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvHeadline;
        public TextView tvSnippet;
        public ImageView ivTumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
            tvSnippet = (TextView) itemView.findViewById(R.id.tvSnippet);
            ivTumbnail = itemView.findViewById(R.id.ivThumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    ((SearchActivity) context).onArticleClicked(position);
                }
            });
        }
    }
}
