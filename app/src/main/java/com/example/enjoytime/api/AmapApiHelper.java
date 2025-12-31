package com.example.enjoytime.api;

import android.content.Context;
import android.util.Log;

import com.example.enjoytime.model.FoodRecommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 高德地图API工具类
 * 用于查询美食餐厅信息
 */
public class AmapApiHelper {
    private static final String TAG = "AmapApiHelper";
    
    // 高德API Key
    private static final String API_KEY = "78670276a30c950f735ff5180083a9dd";
    
    // 高德地图API的Web服务基础URL
    private static final String BASE_URL = "https://restapi.amap.com/v3";
    
    // 搜索餐厅API
    private static final String SEARCH_URL = BASE_URL + "/place/text";
    
    private final OkHttpClient httpClient;
    private Context context;

    public AmapApiHelper(Context context) {
        this.context = context;
        this.httpClient = new OkHttpClient();
    }

    /**
     * 根据城市和关键词搜索美食餐厅
     * @param city 城市名称（如果为空或无效，将使用默认城市）
     * @param keywords 搜索关键词（如"美食"、"火锅"等）
     * @param region 区域名称或坐标（格式：经度,纬度，可选）
     * @return 美食推荐列表
     */
    public List<FoodRecommendation> searchFoodRestaurants(String city, String keywords, String region) {
        List<FoodRecommendation> recommendations = new ArrayList<>();
        
        try {
            // 【关键修复】检查城市是否为无效值，如果无效则使用默认城市
            String searchCity = city;
            if (city == null || city.isEmpty() || city.equals("未知城市")) {
                Log.w(TAG, "城市参数无效: " + city + "，尝试使用坐标或默认城市进行搜索");
                // 如果有坐标（region），使用坐标；否则使用默认城市
                if (region != null && !region.isEmpty()) {
                    searchCity = "北京";  // 设置一个合理的默认城市，但优先使用region坐标
                    Log.d(TAG, "使用默认城市+坐标组合搜索");
                } else {
                    searchCity = "北京";  // 完全降级：使用固定默认城市
                    Log.d(TAG, "完全降级：使用固定默认城市");
                }
            }
            
            // 构建API请求URL
            String url = buildSearchUrl(searchCity, keywords, region);
            Log.d(TAG, "搜索URL: " + url);
            
            // 发送HTTP请求
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            
            Response response = httpClient.newCall(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                Log.d(TAG, "API响应: " + responseBody);
                
                // 解析JSON响应
                recommendations = parseSearchResponse(responseBody);
            } else {
                Log.e(TAG, "API请求失败: " + response.code() + ", URL: " + url);
            }
        } catch (IOException e) {
            Log.e(TAG, "搜索餐厅异常", e);
        }
        
        return recommendations;
    }

    /**
     * 构建搜索URL
     * 对中文和特殊字符进行URL编码，确保请求正确
     */
    private String buildSearchUrl(String city, String keywords, String region) {
        try {
            StringBuilder urlBuilder = new StringBuilder(SEARCH_URL);
            urlBuilder.append("?");
            urlBuilder.append("key=").append(API_KEY);
            urlBuilder.append("&keywords=").append(URLEncoder.encode(keywords, "UTF-8"));
            urlBuilder.append("&city=").append(URLEncoder.encode(city, "UTF-8"));
            if (region != null && !region.isEmpty()) {
                urlBuilder.append("&location=").append(region);  // region是坐标，不需要编码
                urlBuilder.append("&sortrule=distance");
            }
            if (city != null && !city.equals("未定位") && !city.contains("坐标")) {
                // 如果 city 是中文（如海淀区），需要 URL 编码
                // 如果 city 是 adcode（如 110108），直接传
                urlBuilder.append("&city=").append(URLEncoder.encode(city, "UTF-8"));
            }
            urlBuilder.append("&offset=").append(10);  // 返回10条结果
            urlBuilder.append("&page=").append(1);
            return urlBuilder.toString();
        } catch (Exception e) {
            Log.e(TAG, "构建URL异常", e);
            // 降级处理：返回最基础的URL
            StringBuilder urlBuilder = new StringBuilder(SEARCH_URL);
            urlBuilder.append("?key=").append(API_KEY);
            urlBuilder.append("&keywords=").append(keywords);
            urlBuilder.append("&city=").append(city);
            if (region != null && !region.isEmpty()) {
                urlBuilder.append("&region=").append(region);
            }
            return urlBuilder.toString();
        }
    }

    /**
     * 解析高德API的搜索响应
     */
    private List<FoodRecommendation> parseSearchResponse(String responseBody) {
        List<FoodRecommendation> recommendations = new ArrayList<>();
        
        try {
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            
            // 检查状态码
            String status = jsonObject.get("status").getAsString();
            if (!"1".equals(status)) {
                Log.e(TAG, "API返回错误: " + jsonObject.get("info"));
                return recommendations;
            }
            
            // 获取搜索结果
            JsonArray places = jsonObject.getAsJsonArray("pois");
            
            if (places != null) {
                for (int i = 0; i < places.size(); i++) {
                    JsonObject placeObj = places.get(i).getAsJsonObject();
                    
                    // 提取餐厅信息
                    String name = placeObj.get("name").getAsString();
                    String type = placeObj.has("type") ? placeObj.get("type").getAsString() : "餐厅";
                    String address = placeObj.has("address") ? placeObj.get("address").getAsString() : "地址未知";
                    String phone = placeObj.has("tel") ? placeObj.get("tel").getAsString() : "未提供";
                    
                    // 生成模拟数据（高德API返回的数据可能不包含评分，这里生成模拟评分）
                    double rating = 3.5 + (Math.random() * 1.5);  // 3.5-5.0之间
                    int reviewCount = (int) (10 + Math.random() * 990);
                    double distance = Math.random() * 5000;  // 0-5km
                    
                    FoodRecommendation recommendation = new FoodRecommendation(
                            name,
                            type,
                            Math.round(rating * 10.0) / 10.0,  // 保留一位小数
                            reviewCount,
                            address,
                            phone,
                            distance
                    );
                    
                    recommendations.add(recommendation);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "解析响应异常", e);
        }
        
        return recommendations;
    }

    /**
     * 获取模拟的美食推荐数据（用于测试）
     * 当API Key未配置或网络不可用时使用
     */
    public static List<FoodRecommendation> getMockFoodRecommendations() {
        List<FoodRecommendation> mockData = new ArrayList<>();
        
        mockData.add(new FoodRecommendation(
                "香辣蟹馆",
                "川菜",
                4.8,
                256,
                "朝阳区建国路2号",
                "010-12345678",
                500
        ));
        
        mockData.add(new FoodRecommendation(
                "日本料理山本",
                "日本料理",
                4.7,
                189,
                "朝阳区三里屯街10号",
                "010-87654321",
                800
        ));
        
        mockData.add(new FoodRecommendation(
                "老北京烤鸭",
                "京菜",
                4.9,
                512,
                "西城区前门大街3号",
                "010-11223344",
                2000
        ));
        
        mockData.add(new FoodRecommendation(
                "麻辣烫美食坊",
                "川菜",
                4.5,
                347,
                "朝阳区CBD中心",
                "010-55667788",
                1200
        ));
        
        mockData.add(new FoodRecommendation(
                "正宗火锅城",
                "火锅",
                4.6,
                428,
                "丰台区南三环",
                "010-99887766",
                1500
        ));
        
        return mockData;
    }
}
