package com.example.enjoytime.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * 位置管理器 - 获取当前位置和城市名称
 */
public class LocationManager {
    private static final String TAG = "LocationManager";
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    public LocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.geocoder = new Geocoder(context, Locale.CHINA);
    }

    /**
     * 获取当前位置（经纬度）
     */
    public void getCurrentLocation(LocationCallback callback) {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Log.d(TAG, "当前位置: " + location.getLatitude() + ", " + location.getLongitude());
                            getAddressFromLocation(location, callback);
                        } else {
                            Log.w(TAG, "无法获取位置");
                            callback.onError("无法获取当前位置，请检查定位权限");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "获取位置失败", e);
                        callback.onError("获取位置失败: " + e.getMessage());
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "缺少位置权限", e);
            callback.onError("缺少位置权限");
        }
    }

    /**
     * 从位置获取城市名称
     */
    private void getAddressFromLocation(Location location, LocationCallback callback) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getAdminArea();  // 获取省份
                    String district = address.getLocality(); // 获取城市
                    
                    if (district != null && !district.isEmpty()) {
                        city = district;
                    }
                    
                    Log.d(TAG, "获取到的城市: " + city);
                    callback.onSuccess(city != null ? city : "未知城市", location);
                } else {
                    Log.w(TAG, "无法获取地址信息");
                    callback.onError("无法获取城市信息");
                }
            } catch (IOException e) {
                Log.e(TAG, "地理编码异常", e);
                callback.onError("地理编码异常: " + e.getMessage());
            }
        }).start();
    }

    /**
     * 位置获取回调
     */
    public interface LocationCallback {
        void onSuccess(String city, Location location);
        void onError(String error);
    }
}
