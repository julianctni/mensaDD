package com.pasta.mensadd.domain.news;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by julian on 09.02.18.
 */

@Entity(tableName = "table_news")
public class News {

    @NonNull
    @PrimaryKey
    private String id;
    private String link;
    private String category;
    private String date;
    private String title;
    private String content;

    public News(String id, String category, String date,
                String title, String content, String link) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.title = title;
        this.content = content;
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public String getId() {
        return id;
    }


}
