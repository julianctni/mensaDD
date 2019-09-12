package com.pasta.mensadd.database.entity;

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
    private String heading;
    private String contentShort;

    public News(String id, String category, String date, String heading, String contentShort, String link) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.heading = heading;
        this.contentShort = contentShort;
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getHeading() {
        return heading;
    }

    public String getContentShort() {
        return contentShort;
    }

    public String getLink() {
        return link;
    }

    public String getId() { return id; }


}
