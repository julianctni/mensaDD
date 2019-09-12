package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.database.repository.NewsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private NewsRepository newsRepository;
    private LiveData<List<News>> news;
    private boolean isRefreshing;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        newsRepository = new NewsRepository(application);
        news = newsRepository.getAllNews();
    }

    public LiveData<List<News>> getAllNews() {
        this.refreshNews();
        return news;
    }

    private void refreshNews() {
        isRefreshing = true;
        newsRepository.refreshNews((responseType, message) -> {
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
                    newsRepository.insertNews(n);
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
