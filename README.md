# JoyfulTime - 车机系统停车休闲Demo

![License](https://img.shields.io/badge/license-MIT-blue.svg)

## 📱 项目介绍

**JoyfulTime** 是一个为车机系统设计的停车休闲娱乐应用。当驾驶员停车时，可以通过此应用享受多样化的娱乐内容，包括电影推荐、电视剧推荐、音乐控制、美食推荐等功能，提升停车时的用户体验。

## ✨ 主要功能

### 1. **电影推荐** 🎬
- 浏览热门电影列表
- 查看电影详情和评分
- 视频播放功能
- 个性化推荐算法

### 2. **电视剧推荐** 📺
- 电视剧内容展示
- 按类型分类浏览
- 视频播放支持

### 3. **音乐控制** 🎵
- 通过系统通知监听器集成音乐播放
- 音乐播放控制功能
- 蓝牙设备支持

### 4. **美食推荐** 🍔
- 基于位置的美食推荐
- 显示周边餐饮信息
- 地图集成（高德地图）

### 5. **用户系统** 👤
- 用户注册和登录
- 用户个人资料管理
- 用户偏好设置

## 🏗️ 项目架构

```
JoyfulTime/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/enjoytime/
│   │   │   ├── MainActivity.java              # 主入口Activity
│   │   │   ├── LoginActivity.java             # 登录界面
│   │   │   ├── RegisterActivity.java          # 注册界面
│   │   │   ├── ProfileActivity.java           # 个人资料界面
│   │   │   ├── MoviesActivity.java            # 电影列表
│   │   │   ├── SeriesActivity.java            # 电视剧列表
│   │   │   ├── VideoPlayerActivity.java       # 视频播放器
│   │   │   ├── FoodRecommendationActivity.java # 美食推荐
│   │   │   ├── SectionOneActivity.java        # 功能区块1
│   │   │   ├── SectionTwoActivity.java        # 功能区块2
│   │   │   ├── SectionThreeActivity.java      # 功能区块3
│   │   │   ├── MovieRecommendationFragment.java
│   │   │   ├── SeriesRecommendationFragment.java
│   │   │   ├── MusicNotificationListenerService.java # 音乐监听服务
│   │   │   ├── adapter/                       # 适配器
│   │   │   ├── api/                           # API接口层
│   │   │   ├── location/                      # 位置服务
│   │   │   └── model/                         # 数据模型
│   │   ├── res/                               # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/                                    # Gradle配置
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🛠️ 技术栈

### 开发环境
- **编程语言**: Java

### 核心依赖
- **AndroidX**: AppCompat, ConstraintLayout, RecyclerView
- **地图服务**: 高德地图SDK (本地JAR包)
- **音乐控制**: NotificationListenerService

### 开发工具
- Build System: Gradle (Kotlin DSL)
- Version Control: Git
- IDE: Android Studio

## 📦 依赖管理

项目使用 `gradle/libs.versions.toml` 进行依赖版本管理，统一管理所有库的版本号。

### 主要依赖说明

| 依赖库 | 用途 |
|------|------|
| AppCompat | Android兼容性支持库 |
| Material | Material Design UI组件 |
| OkHttp | HTTP客户端 |
| Gson | JSON序列化/反序列化 |
| Play Services Location | 获取设备位置信息 |
| 高德地图SDK | 地图展示和位置服务 |
| RecyclerView | 列表展示控件 |

## 🚀 快速开始

### 前置条件
- Android Studio (最新版本推荐)
- JDK 11 或更高版本
- Android SDK 36
- 联网环境（用于Gradle下载依赖）

### 构建步骤

1. **克隆或打开项目**
   ```bash
   cd JoyfulTime
   ```

2. **构建项目**
   ```bash
   ./gradlew build
   ```

3. **运行应用**
   ```bash
   ./gradlew installDebug
   ```
   或直接在Android Studio中点击运行

## 📋 系统权限

应用需要以下权限：

| 权限 | 用途 |
|-----|------|
| INTERNET | 网络通信 |
| ACCESS_FINE_LOCATION | GPS定位 |
| ACCESS_COARSE_LOCATION | 粗定位 |
| ACCESS_NETWORK_STATE | 网络状态检查 |
| READ_EXTERNAL_STORAGE | 读取外部存储 |
| BLUETOOTH | 蓝牙连接 |
| BLUETOOTH_CONNECT | 蓝牙连接（Android 12+） |
| MEDIA_CONTENT_CONTROL | 媒体控制 |

## 🎨 应用特色

### 用户界面
- 车机系统优化的UI设计
- 大按钮、清晰的视觉层级，适合在行驶中停车时使用
- 深色主题支持（详见 `values-night/themes.xml`）

### 数据绑定
- 使用View Binding提高开发效率
- 类型安全的视图访问

### 可扩展性
- 模块化架构设计
- 清晰的API层分离
- 易于添加新功能模块

## 📱 支持设备

- **最低版本**: Android 9 (API 28)
- **目标版本**: Android 15 (API 36)
- **设备类型**: 车载设备、平板、手机

## 🔌 集成的外部服务

### 高德地图
- 用于美食推荐功能的地理位置显示
- 周边美食搜索和展示

### Google Play Services
- 位置定位服务
- 精确定位和粗定位

## 🐛 已知问题与改进方向

- 目前使用明文流量通信（cleartext），生产环境应使用HTTPS
- 可扩展美食推荐API集成（如大众点评、高德餐饮API等）
- 可添加更多媒体内容源
- 用户偏好学习算法可进一步优化

## 📄 项目配置文件

### 主要配置文件
- `build.gradle.kts` - 项目级构建配置
- `app/build.gradle.kts` - 应用级构建配置
- `gradle/libs.versions.toml` - 依赖版本管理
- `local.properties` - 本地开发环境配置
- `proguard-rules.pro` - 代码混淆规则

## 🔐 安全性考虑

- 应用支持数据备份和恢复规则 (`data_extraction_rules.xml`, `backup_rules.xml`)
- 支持RTL语言布局
- 数据提取规则已配置

## 📝 版本信息

- **当前版本**: 1.0
- **版本代码**: 1
- **应用ID**: com.example.enjoytime
- **应用名称**: EnjoyTime

## 📄 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。

## 📧 联系方式

如有问题或建议，欢迎通过以下方式联系：

- **提交Issue**: 在项目仓库中提交Issue
- **讨论**: 在Discussions中展开讨论

## 🎯 未来规划

- [ ] 添加本地缓存机制，离线支持
- [ ] 优化视频播放器功能
- [ ] 集成更多娱乐内容源
- [ ] 增强推荐算法
- [ ] 支持多用户配置
- [ ] 添加应用内购买
- [ ] 语音控制功能

---

**开发者**: EnjoyTime Team
