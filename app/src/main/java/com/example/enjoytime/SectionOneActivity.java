package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SectionOneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_one);

        View moviesModule = findViewById(R.id.movies_module);
        View seriesModule = findViewById(R.id.series_module);

        moviesModule.setOnClickListener(v -> {
            Intent intent = new Intent(SectionOneActivity.this, MoviesActivity.class);
            startActivity(intent);
        });

        seriesModule.setOnClickListener(v -> {
            Intent intent = new Intent(SectionOneActivity.this, SeriesActivity.class);
            startActivity(intent);
        });
    }
}