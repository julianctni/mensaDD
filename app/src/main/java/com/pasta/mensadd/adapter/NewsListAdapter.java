package com.pasta.mensadd.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.fragments.NewsFragment;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.News;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private ArrayList<News> mNews;
    private NewsFragment mFragment;

    public NewsListAdapter(ArrayList<News> items, NewsFragment fragment) {
        mNews = items;
        mFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_news_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News item = mNews.get(position);
        holder.mHeading.setText(item.getHeading());
        holder.mText.setText(item.getContentShort());
        holder.mDate.setText(item.getDate());
        holder.mCategory.setText(item.getCategory());
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mHeading;
        TextView mText;
        TextView mDate;
        TextView mCategory;
        Button mShareButton;
        Button mDetailsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeading = itemView.findViewById(R.id.newsHeading);
            mText = itemView.findViewById(R.id.newsText);
            mDate = itemView.findViewById(R.id.newsDate);
            mCategory = itemView.findViewById(R.id.newsCategory);
            mShareButton = itemView.findViewById(R.id.newsShareButton);
            mDetailsButton = itemView.findViewById(R.id.newsDetailsButton);
            mShareButton.setOnClickListener(this);
            mDetailsButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.newsShareButton){
                shareNews(mNews.get(getAdapterPosition()));
            } else if (v.getId() == R.id.newsDetailsButton){
                openDetailsInBrowser(mNews.get(getAdapterPosition()));
            }

        }


    }

    private void shareNews(News news) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String shareText = news.getHeading() + "\n\n" + news.getContentShort() + "\n\n" + news.getLink() + "\n\n#news #mensaDD";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");
        mFragment.getActivity().startActivity(Intent.createChooser(shareIntent, mFragment.getString(R.string.content_share)));
    }

    private void openDetailsInBrowser(News news) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getLink()));
        if (intent.resolveActivity(mFragment.getActivity().getPackageManager()) != null) {
            mFragment.getActivity().startActivity(intent);
        }

    }
}