package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enjoytime.adapter.FoodRecommendationAdapter;
import com.example.enjoytime.databinding.ActivityFoodRecommendationBinding;
import com.example.enjoytime.model.FoodRecommendation;
import com.example.enjoytime.api.AmapApiHelper;

import java.util.List;

/**
 * 美食推荐页面
 */
public class FoodRecommendationActivity extends AppCompatActivity {

    private ActivityFoodRecommendationBinding binding;
    private FoodRecommendationAdapter adapter;
    private AmapApiHelper apiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化API Helper
        apiHelper = new AmapApiHelper(this);

        // 设置RecyclerView
        adapter = new FoodRecommendationAdapter();
        binding.foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.foodRecyclerView.setAdapter(adapter);

        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener(v -> {
            String city = binding.cityEditText.getText().toString().trim();
            String keywords = binding.keywordsEditText.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if (keywords.isEmpty()) {
                keywords = "美食";
            }

            loadFoodRecommendations(city, keywords);
        });

        // 初始化时加载默认城市的美食推荐火锅
        loadFoodRecommendations("重庆", "美食");

        // 设置返回导航
        setupNavigation();
        setupBackNavigation();
    }

    private void loadFoodRecommendations(String city, String keywords) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchButton.setEnabled(false);

        // 使用线程执行网络请求
        new Thread(() -> {
            try {
                List<FoodRecommendation> results = apiHelper.searchFoodRestaurants(city, keywords, null);

                if (results.isEmpty()) {
                    // 如果查询结果为空，使用模拟数据
                    results = AmapApiHelper.getMockFoodRecommendations();
                }

                final List<FoodRecommendation> finalResults = results;
                runOnUiThread(() -> {
                    adapter.submitList(finalResults);
                    binding.emptyTextView.setVisibility(View.GONE);
                    binding.foodRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.searchButton.setEnabled(true);
                });
            } catch (Exception e) {
                List<FoodRecommendation> mockData = AmapApiHelper.getMockFoodRecommendations();
                runOnUiThread(() -> {
                    Toast.makeText(FoodRecommendationActivity.this, "加载美食推荐失败，使用本地数据", Toast.LENGTH_SHORT).show();
                    adapter.submitList(mockData);
                    binding.emptyTextView.setVisibility(View.GONE);
                    binding.foodRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.searchButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void setupNavigation() {
        // 设置顶部导航按钮
        // 首页按钮
        View homeBtn = findViewById(R.id.view_section_1);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent intent = new Intent(FoodRecommendationActivity.this, SectionOneActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // 休息按钮
        View restBtn = findViewById(R.id.view_section_2);
        if (restBtn != null) {
            restBtn.setOnClickListener(v -> {
                Intent intent = new Intent(FoodRecommendationActivity.this, SectionTwoActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // 美食按钮（当前页面）
        View foodBtn = findViewById(R.id.view_section_3);
        if (foodBtn != null) {
            foodBtn.setOnClickListener(v -> {
                // 已经在美食页面，不需要导航
            });
        }

        // 返回主菜单
        View backBtn = findViewById(R.id.view_back_to_main);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                Intent intent = new Intent(FoodRecommendationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(FoodRecommendationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
