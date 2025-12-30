package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesActivity extends AppCompatActivity implements SeriesAdapter.OnSeriesClickListener {

    private SeriesAdapter adapter;
    private List<Series> allSeries;
    private Map<String, String> seriesVideoUrls = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        RecyclerView recyclerView = findViewById(R.id.series_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        allSeries = new ArrayList<>();
        allSeries.add(new Series("士兵突击", R.drawable.soldiers_sortie_poster));
        allSeries.add(new Series("亮剑", R.drawable.drawing_sword_poster));
        allSeries.add(new Series("我的团长我的团", R.drawable.my_chief_my_regiment_poster));

        // Assign different videos to each series
        seriesVideoUrls.put("士兵突击", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
        seriesVideoUrls.put("亮剑", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4");
        seriesVideoUrls.put("我的团长我的团", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4");

        adapter = new SeriesAdapter(new ArrayList<>(allSeries), this);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.series_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<Series> filteredList = new ArrayList<>();
        for (Series series : allSeries) {
            if (series.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(series);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onSeriesClick(String seriesTitle) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        String videoUrl = seriesVideoUrls.getOrDefault(seriesTitle, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        intent.putExtra(VideoPlayerActivity.VIDEO_URL, videoUrl);
        startActivity(intent);
    }
}
