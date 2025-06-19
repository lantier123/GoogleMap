# MapDemo - 实时导航应用

一个基于 Google Maps 的 Android 导航应用，使用 Jetpack Compose 构建现代化的用户界面。

## 功能特点

- 实时位置追踪
- 目的地选择和导航
- 路线显示和实时更新
- GPS 信号质量监控
- 行程数据统计
- 权限管理系统

## 技术栈

- Kotlin
- Jetpack Compose
- Google Maps SDK
- Coroutines Flow
- ViewModel
- Material Design 3

## 开发步骤

1. **项目初始化**
   - 创建 Android 项目
   - 配置 Gradle 依赖
   - 设置 Google Maps API Key

2. **权限管理**
   - 实现位置权限请求
   - 添加权限状态监控
   - 创建用户友好的权限请求界面

3. **地图集成**
   - 集成 Google Maps Compose
   - 实现地图控件
   - 添加地图交互功能

4. **位置服务**
   - 实现位置更新服务
   - 添加位置数据模型
   - GPS 信号质量监控

5. **导航功能**
   - 实现目的地选择
   - 添加路线显示
   - 计算距离和预计时间

6. **用户界面**
   - 设计导航控件
   - 实现行程信息面板
   - 添加加载状态指示器

7. **数据管理**
   - 实现 ViewModel
   - 使用 StateFlow 管理状态
   - 添加行程数据统计

8. **优化和测试**
   - 性能优化
   - 错误处理
   - 用户体验改进

## 项目结构

```
app/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── mapdemo/
│       │               ├── data/           # 数据模型
│       │               ├── services/       # 位置服务
│       │               ├── ui/
│       │               │   ├── components/ # UI组件
│       │               │   ├── screens/    # 界面
│       │               │   └── theme/      # 主题
│       │               └── viewmodel/      # ViewModel
│       └── res/                           # 资源文件
```

## 如何运行

1. 克隆项目
```bash
git clone https://github.com/lantier123/GoogleMap.git
```

2. 获取 Google Maps API Key
   - 访问 [Google Cloud Console](https://console.cloud.google.com/)
   - 创建新项目或选择现有项目
   - 启用 Maps SDK for Android
   - 创建 API 密钥

3. 配置 API Key
   - 在 `app/src/main/AndroidManifest.xml` 中替换 `YOUR_GOOGLE_MAPS_API_KEY_HERE` 为您的实际 API 密钥
   - 或者创建 `local.properties` 文件并添加：
   ```properties
   MAPS_API_KEY=your_actual_api_key_here
   ```

4. 在 Android Studio 中打开项目并运行

## 注意事项

- 需要 Android API Level 24 或更高版本
- 需要有效的 Google Maps API Key
- 需要启用设备位置服务
- 请确保 API 密钥有适当的限制（如应用包名限制）以确保安全

## 许可证

MIT License 