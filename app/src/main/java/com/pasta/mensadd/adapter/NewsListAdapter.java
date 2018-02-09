package com.pasta.mensadd.adapter;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.fragments.NewsFragment;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.News;

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
        public TextView mHeading;
        public TextView mText;
        public TextView mDate;
        public TextView mCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            mHeading = itemView.findViewById(R.id.newsHeading);
            mText = itemView.findViewById(R.id.newsText);
            mDate = itemView.findViewById(R.id.newsDate);
            mCategory = itemView.findViewById(R.id.newsCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*
            String mensaId;
            try {
                mensaId = mCanteens.get(getAdapterPosition()).getCode();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mFragment.getActivity().getApplicationContext());
            int priority = prefs.getInt("priority_"+mensaId, 0);
            priority += 1;
            prefs.edit().putInt("priority_"+mensaId, priority).apply();
            DataHolder.getInstance().getCanteen(mensaId).increasePriority();
            DataHolder.getInstance().sortCanteenList();
            NewsListAdapter.this.notifyDataSetChanged();
            FragmentController.showMealWeekFragment(mFragment.getFragmentManager(),mensaId);*/
        }
    }
}