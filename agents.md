# AGENTS.md

本文件为 AI 编程助手（如 Grok、Claude、Cursor 等）提供项目上下文与开发约定。

## 项目概述

**仿欧陆词典阅读器**（EudicReader）  
一个模仿欧陆词典阅读器风格的安卓双语阅读原型应用。

- 包名：`com.eudic.reader`
- 语言：Kotlin
- 最低 SDK：24
- 目标 SDK：34

## 核心功能

1. **书架页**（`MainActivity`）
   - 显示示例书籍
   - 导入按钮弹出选项列表（TXT / EPUB）

2. **阅读页**（`ReaderActivity`）
   - 深色主题
   - 右上角蓝色「中A」按钮：模拟 AI 翻译，显示中文译文
   - 支持传入 `imported_content` 与 `book_title` Intent extras

3. **EPUB 解析**
   - 使用 `ZipInputStream` 提取 HTML/XHTML
   - 识别 `<h1>`–`<h3>` 作为章节标题，格式化为 `【标题】`
   - 章节之间用 `---` 分隔
   - 自动过滤 toc / nav / cover

## 目录结构

```
EudicReader/
├── app/
│   ├── src/main/
│   │   ├── java/com/eudic/reader/
│   │   │   ├── MainActivity.kt      # 书架 + 导入逻辑
│   │   │   └── ReaderActivity.kt    # 阅读 + 翻译按钮
│   │   ├── res/layout/
│   │   │   ├── activity_main.xml
│   │   │   └── activity_reader.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## 开发约定

- UI 保持深色主题（`#000000` 背景）
- 新增功能优先在现有 Activity 中扩展，避免过度拆分
- EPUB 解析保持轻量，不引入重型第三方库
- 修改后需重新编译 APK 并测试导入流程

## 后续可扩展方向

- 真实在线 AI 翻译 API 接入
- 笔记高亮与书签持久化
- 目录导航（基于 EPUB spine / toc.ncx）
- 字体大小、行距调节
- 阅读进度记录

## 构建

```bash
export ANDROID_HOME=~/android-sdk
./gradlew assembleDebug
```

输出：`app/build/outputs/apk/debug/app-debug.apk`
