package com.example.enjoytime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.amap.api.location.AMapLocation;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Arrays;
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
    private AMapLocation currentLocation; // 使用高德对象
    
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
                // 使用当前存储的位置信息进行搜索
                loadFoodRecommendations(currentCity, keywords, currentLocation);
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
            // Android 6.0+ 需要运行时权限
            boolean hasCoarseLocation = ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean hasFineLocation = ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            
            Log.d("FoodRecommendation", "权限检查 - FINE_LOCATION: " + hasFineLocation + 
                    ", COARSE_LOCATION: " + hasCoarseLocation);
            
            // 如果两个权限都已授予，直接获取位置
            if (hasFineLocation && hasCoarseLocation) {
                Log.d("FoodRecommendation", "权限已授予，开始获取位置");
                getCurrentLocationAndSearch();
            } else {
                // 权限未全部授予，请求权限
                Log.d("FoodRecommendation", "权限未授予，请求权限: " + 
                        Arrays.toString(PERMISSIONS));
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android 5.0以下，直接获取位置
            Log.d("FoodRecommendation", "Android < 6.0，直接获取位置");
            getCurrentLocationAndSearch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("FoodRecommendation", "权限请求回调: requestCode=" + requestCode + 
                ", 结果数=" + grantResults.length);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 检查是否所有权限都被授予
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            
            if (allPermissionsGranted && grantResults.length > 0) {
                Log.d("FoodRecommendation", "所有权限已授予，开始获取位置");
                getCurrentLocationAndSearch();
            } else {
                Log.w("FoodRecommendation", "权限被拒绝，使用默认城市");
                Toast.makeText(this, "需要定位权限才能使用此功能，已使用默认城市", Toast.LENGTH_SHORT).show();
                // 使用默认城市
                currentCity = "北京";
                binding.cityEditText.setText(currentCity);
                loadFoodRecommendations(currentCity, "美食", null);
            }
        }
    }

    /**
     * 获取当前位置并搜索美食
     */
    private void getCurrentLocationAndSearch() {
        binding.progressBar.setVisibility(View.VISIBLE);
        locationManager.getCurrentLocation(new LocationManager.LocationCallback(){
            @Override
            public void onSuccess(AMapLocation amapLocation) {
                runOnUiThread(() -> {
                    currentLocation = amapLocation; // 保存高德对象

                    // 3. 智能获取城市名称
                    // 优先显示 区/县 (如：朝阳区)，如果没有再显示 市
                    String displayCity = amapLocation.getDistrict();
                    if (displayCity == null || displayCity.isEmpty()) {
                        displayCity = amapLocation.getCity();
                    }
                    currentCity = displayCity;

                    binding.cityEditText.setText(currentCity);
                    binding.progressBar.setVisibility(View.GONE);


                    // 4. 调用搜索
                    loadFoodRecommendations(currentCity, "美食", currentLocation);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // 根据错误信息显示更具体的提示
                    String userMessage = "定位失败";
                    if (error != null) {
                        if (error.contains("权限")) {
                            userMessage = "缺少定位权限，已使用默认位置";
                        } else if (error.contains("GPS") || error.contains("启用")) {
                            userMessage = "请开启GPS或网络定位";
                        } else if (error.contains("超时")) {
                            userMessage = "定位超时，已使用默认位置";
                        } else {
                            userMessage = error;
                        }
                    }

                    Log.d("FoodRecommendation", "定位错误详情: " + error);
                    
                    // 【关键修复】完全降级处理
                    currentCity = "重庆";  // 使用固定默认城市
                    currentLocation = null;  // 无法定位时清空位置
                    binding.cityEditText.setText(currentCity);
                    binding.progressBar.setVisibility(View.GONE);
                    // 使用默认城市和关键词搜索
                    loadFoodRecommendations(currentCity, "美食", null);
                });
            }
        });
    }

    /**
     * 加载美食推荐
     * @param city 城市名称
     * @param keywords 搜索关键词
     * @param location 当前位置，用于搜索周边美食，可为null
     */
    private void loadFoodRecommendations(String city, String keywords, AMapLocation location) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.searchButton.setEnabled(false);

        // 使用线程执行网络请求
        new Thread(() -> {
            try {
                // 【关键修复】如果有位置信息，将经纬度作为region参数传递给API
                // 6. 提取坐标字符串 (格式：经度,纬度)
                String locationStr = null;
                String adCode = null; // 城市编码

                if (location != null) {
                    // 经度在前，纬度在后
                    locationStr = String.format("%.6f,%.6f", location.getLongitude(), location.getLatitude());
                    // 获取精确的行政区划代码 (如 110105 代表朝阳区)
                    adCode = location.getAdCode();
                }

                // 调用 API Helper (建议把 city 传 adCode，这样搜得准)
                // 如果你的 ApiHelper 还没改好，这里暂时传 city 名字也可以
                List<FoodRecommendation> results = apiHelper.searchFoodRestaurants(
                        adCode != null ? adCode : city, // 优先传 AdCode 给 API
                        keywords,
                        locationStr // 传坐标字符串
                );

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁定位客户端，释放资源
        try {
            if (locationManager != null) {
                locationManager.onDestroy();
            }
        } catch (Exception e) {
            Log.e("FoodRecommendation", "销毁LocationManager异常", e);
        }
    }
}
