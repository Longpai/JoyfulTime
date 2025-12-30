package com.example.enjoytime;

public class Series {
    private final String title;
    private final int posterResId;

    public Series(String title, int posterResId) {
        this.title = title;
        this.posterResId = posterResId;
    }

    public String getTitle() {
        return title;
    }

    public int getPosterResId() {
        return posterResId;
    }
}
