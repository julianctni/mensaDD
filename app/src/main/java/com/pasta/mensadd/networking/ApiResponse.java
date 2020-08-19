package com.pasta.mensadd.networking;

import java.util.List;

public class ApiResponse<T> {
    private long scrapedAt;
    private List<T> data;

    public ApiResponse(long scrapedAt, List<T> data) {
        this.scrapedAt = scrapedAt;
        this.data = data;
    }

    public long getScrapedAt() {
        return scrapedAt;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
