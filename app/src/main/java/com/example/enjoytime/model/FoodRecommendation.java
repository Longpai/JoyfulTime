package com.example.enjoytime.model;

/**
 * 美食推荐数据模型
 */
public class FoodRecommendation {
    private String name;           // 餐厅名称
    private String cuisine;         // 菜系
    private double rating;          // 评分
    private int reviewCount;        // 评论数
    private String address;         // 地址
    private String phone;           // 电话
    private double distance;        // 距离（单位：米）

    public FoodRecommendation(String name, String cuisine, double rating, 
                             int reviewCount, String address, String phone, double distance) {
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.address = address;
        this.phone = phone;
        this.distance = distance;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getDistance() {
        return distance;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "FoodRecommendation{" +
                "name='" + name + '\'' +
                ", cuisine='" + cuisine + '\'' +
                ", rating=" + rating +
                ", reviewCount=" + reviewCount +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", distance=" + distance +
                '}';
    }
}
