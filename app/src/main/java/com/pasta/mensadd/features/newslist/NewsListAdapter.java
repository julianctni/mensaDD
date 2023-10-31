package com.pasta.mensadd.features.newslist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.domain.news.News;


public class NewsListAdapter extends ListAdapter<News, NewsListAdapter.ViewHolder> {

    private final Context mContext;

    private static final DiffUtil.ItemCallback<News> DIFF_CALLBACK = new DiffUtil.ItemCallback<News>() {
        @Override
        public boolean areItemsTheSame(@NonNull News o, @NonNull News n) {
            return o.getId().equals(n.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull News o, @NonNull News n) {
            return o.getTitle().equals(n.getTitle()) &&
                    o.getCategory().equals(n.getCategory()) &&
                    o.getContent().equals(n.getContent());
        }
    };

    public NewsListAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_news_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News item = getItem(position);
        holder.mHeading.setText(item.getTitle());
        holder.mText.setText(item.getContent());
        holder.mDate.setText(item.getDate());
        holder.mCategory.setText(item.getCategory());
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mHeading;
        TextView mText;
        TextView mDate;
        TextView mCategory;
        Button mShareButton;
        Button mDetailsButton;

        ViewHolder(View itemView) {
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
                shareNews(getItem(getBindingAdapterPosition()));
            } else if (v.getId() == R.id.newsDetailsButton){
                openDetailsInBrowser(getItem(getBindingAdapterPosition()));
            }

        }


    }

    private void shareNews(News news) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String shareText = news.getTitle() + "\n\n" + news.getContent() + "\n\n" + news.getLink() + "\n\n#news #mensaDD";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");
        if (mContext != null)
            mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.content_share)));
    }

    private void openDetailsInBrowser(News news) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getLink()));
        if (mContext != null) {
            mContext.startActivity(intent);
        }

    }
}