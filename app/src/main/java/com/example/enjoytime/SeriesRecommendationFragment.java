package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SeriesRecommendationFragment extends Fragment implements SeriesAdapter.OnSeriesClickListener {

    // Google's public test video
    private static final String BIG_BUCK_BUNNY_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_series_recommendation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.series_recommendation_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Series> series = new ArrayList<>();
        series.add(new Series("士兵突击", R.drawable.soldiers_sortie_poster));
        series.add(new Series("亮剑", R.drawable.drawing_sword_poster));
        series.add(new Series("我的团长我的团", R.drawable.my_chief_my_regiment_poster));

        SeriesAdapter seriesAdapter = new SeriesAdapter(series, this);
        recyclerView.setAdapter(seriesAdapter);
    }

    @Override
    public void onSeriesClick(String seriesTitle) {
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_URL, BIG_BUCK_BUNNY_URL);
        startActivity(intent);
    }
}
