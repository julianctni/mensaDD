package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.database.repository.NewsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsViewModel extends ViewModel {

    private NewsRepository mNewsRepository;
    private LiveData<List<News>> news;
    private boolean isRefreshing;

    public NewsViewModel(NewsRepository newsRepository) {
        mNewsRepository = newsRepository;
        news = newsRepository.getAllNews();
    }

    public LiveData<List<News>> getAllNews() {
        this.refreshNews();
        return news;
    }

    private void refreshNews() {
        isRefreshing = true;
        mNewsRepository.refreshNews((responseType, message) -> {
            try {
                JSONArray json = new JSONObject(message).getJSONArray("news");
                for (int i = 0; i < json.length(); i++) {
                    JSONObject news = json.getJSONObject(i);
                    String id = news.getString("id");
                    String heading = news.getString("heading");
                    String date = news.getString("date");
                    String category = news.getString("category");
                    String textShort = news.getString("content");
                    String newsLink = news.getString("link");

                    News n = new News(id, category, date, heading, textShort, newsLink);
                    mNewsRepository.insertNews(n);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isRefreshing = false;
        });
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }
}
