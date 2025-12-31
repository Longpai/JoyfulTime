package com.example.enjoytime.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.location.AMapLocation; // 如果你还在用高德的对象，保留这个引用；如果想彻底脱离SDK，可以用原生Location

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 位置管理器 - Web API (IP定位) 版
 * 逃课专用：无需 SHA1，无需 SDK，通过 HTTP 请求获取大概位置
 */
public class LocationManager {
    private static final String TAG = "LocationManager";
    
    // 【重要】这里填你申请的 “Web 服务” 类型的 Key
    private static final String WEB_API_KEY = "78670276a30c950f735ff5180083a9dd"; 
    
    private Context context;

    public LocationManager(Context context) {
        this.context = context;
    }

    /**
     * 获取当前位置（通过 IP 定位 API）
     */
    public void getCurrentLocation(LocationCallback callback) {
        new Thread(() -> {
            try {
                // 1. 构建请求 URL (高德 IP 定位 API)
                String urlStr = "https://restapi.amap.com/v3/ip?key=" + WEB_API_KEY;
                Log.d(TAG, "正在请求IP定位: " + urlStr);

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // 2. 发送请求并读取响应
                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String jsonStr = response.toString();
                    Log.d(TAG, "IP定位响应: " + jsonStr);

                    // 3. 解析 JSON
                    JSONObject json = new JSONObject(jsonStr);
                    String status = json.optString("status");

                    if ("1".equals(status)) {
                        String province = json.optString("province");
                        String city = json.optString("city");
                        String adcode = json.optString("adcode");
                        String rectangle = json.optString("rectangle"); // 格式如: "116.0,39.0;116.1,39.1"

                        // 处理城市名称（如果是直辖市，city可能是空的或者是数组，这里简单处理）
                        String displayCity = city;
                        if (displayCity == null || displayCity.isEmpty() || displayCity.equals("[]")) {
                            displayCity = province;
                        }

                        // 4. 计算中心坐标
                        // IP定位返回的是一个矩形范围，我们取中心点作为模拟坐标
                        double lat = 0, lon = 0;
                        if (rectangle != null && !rectangle.isEmpty() && !rectangle.equals("[]")) {
                            String[] points = rectangle.split(";")[0].split(",");
                            if (points.length == 2) {
                                lon = Double.parseDouble(points[0]); // 经度
                                lat = Double.parseDouble(points[1]); // 纬度
                            }
                        }

                        // 5. 构造结果返回 (伪装成 AMapLocation 以兼容你的 Activity)
                        AMapLocation location = new AMapLocation("WebIP");
                        location.setProvince(province);
                        location.setCity(city.equals("[]") ? province : city);
                        location.setAdCode(adcode);
                        location.setLatitude(lat);
                        location.setLongitude(lon);
                        location.setAddress(province + displayCity + " (IP定位)");

                        // 回调成功
                        callback.onSuccess(location);
                    } else {
                        callback.onError("IP定位失败: " + json.optString("info"));
                    }
                } else {
                    callback.onError("网络请求失败: " + connection.getResponseCode());
                }
                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "IP定位异常", e);
                callback.onError("定位异常: " + e.getMessage());
            }
        }).start();
    }

    // 此时不需要销毁什么资源了
    public void onDestroy() { }

    public interface LocationCallback {
        void onSuccess(AMapLocation amapLocation);
        void onError(String error);
    }
}