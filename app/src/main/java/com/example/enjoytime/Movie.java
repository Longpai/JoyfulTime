package com.example.enjoytime;

public class Movie {
    private final String title;
    private final int posterResId;

    public Movie(String title, int posterResId) {
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
