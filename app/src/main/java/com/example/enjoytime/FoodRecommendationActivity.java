package com.example.enjoytime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.enjoytime.adapter.FoodRecommendationAdapter;
import com.example.enjoytime.api.AmapApiHelper;
import com.example.enjoytime.databinding.ActivityFoodRecommendationBinding;
import com.example.enjoytime.location.LocationManager;
import com.example.enjoytime.model.FoodRecommendation;

import java.util.List;

/**
 * 美食推荐页面
 * 自动定位当前位置，用户只需输入美食类型进行搜索
 */
public class FoodRecommendationActivity extends AppCompatActivity {

    private ActivityFoodRecommendationBinding binding;
    private FoodRecommendationAdapter adapter;
    private AmapApiHelper apiHelper;
    private LocationManager locationManager;
    private String currentCity = "未定位";
    
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化
        apiHelper = new AmapApiHelper(this);
        locationManager = new LocationManager(this);

        // 设置RecyclerView
        adapter = new FoodRecommendationAdapter();
        binding.foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.foodRecyclerView.setAdapter(adapter);

        // 修改UI提示
        binding.cityEditText.setHint("当前定位位置");
        binding.cityEditText.setEnabled(false);
        binding.keywordsEditText.setHint("输入美食类型（如：火锅、川菜）");

        // 设置搜索按钮点击事件
        binding.searchButton.setOnClickListener(v -> {
            String keywords = binding.keywordsEditText.getText().toString().trim();
            if (keywords.isEmpty()) {
                keywords = "美食";
            }
            if (!currentCity.equals("未定位")) {
                loadFoodRecommendations(currentCity, keywords);
            } else {
                Toast.makeText(this, "正在定位中，请稍候...", Toast.LENGTH_SHORT).show();
            }
        });

        // 请求权限并获取位置
        requestLocationAndLoadFood();

        // 设置返回导航
        setupNavigation();
        setupBackNavigation();
    }

    /**
     * 请求位置权限并获取当前位置
     */
    private void requestLocationAndLoadFood() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                // 权限已授予，获取位置
                getCurrentLocationAndSearch();
            }
        } else {
            // Android 5.0以下，直接获取位置
            getCurrentLocationAndSearch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndSearch();
            } else {
                Toast.makeText(this, "需要定位权限才能使用此功能", Toast.LENGTH_SHORT).show();
                // 使用默认城市
                currentCity = "北京";
                binding.cityEditText.setText(currentCity);
                loadFoodRecommendations(currentCity, "美食");
            }
        }
    }

    /**
     * 获取当前位置并搜索美食
     */
    private void getCurrentLocationAndSearch() {
        binding.progressBar.setVisibility(View.VISIBLE);
        locationManager.getCurrentLocation(new LocationManager.LocationCallback() {
            @Override
            public void onSuccess(String city, Location location) {
                currentCity = city;
                binding.cityEditText.setText(currentCity);
                // 自动搜索美食
                loadFoodRecommendations(city, "美食");
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(FoodRecommendationActivity.this, "定位失败: " + error, Toast.LENGTH_SHORT).show();
                    // 使用默认城市
                    currentCity = "北京";
                    binding.cityEditText.setText(currentCity);
                    loadFoodRecommendations(currentCity, "美食");
                });
            }
        });
    }

    /**
     * 加载美食推荐
     */
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
                // 已经在美食页面
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
