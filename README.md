# 诗笺 · Shijian

> 月光白 · 仿宋 · 每日一首
> 一款纯粹的个人诗词阅读 Android App。

---

## 设计理念

诗笺不是大而全的诗词字典，而是一个**有温度、有美感的日常陪伴工具**。打开就有一首今日之诗，上下滑动随手翻，像翻开一页宣纸。

- 🌙 **月光白** — 暖白基调，像月光洒在宣纸上
- ✒️ **仿宋默认** — 支持楷/宋/隶/自定义字体
- 🎴 **每首诗有自己的颜色** — SHA256 哈希生成唯一渐变色
- 📖 **每日一首 · 时令匹配** — 早上晨诗、中午闲适、晚上夜诗

## 技术栈

| 领域 | 选型 |
|:-----|:-----|
| 语言 | Kotlin 2.0+ |
| UI | Jetpack Compose + Material3 |
| 架构 | MVVM + Repository |
| 网络 | Retrofit + OkHttp |
| 本地存储 | Room |
| 图片加载 | Coil |
| 数据源 | 诗泉 Chinese Poetry API |

## 页面结构

```
底部导航：每日 · 文库 · 发现 · 我的

├─ 每日    — ONE一个风格，上下滑动切换，哈希渐变背景
├─ 文库    — 列表/网格双模，朝代筛选，全文搜索
├─ 发现    — 热门排行 / 随机翻牌 / 本周回顾
└─ 我的    — 收藏/历史/字体/深色模式/配图设置
```

## 数据来源

- [诗泉 Chinese Poetry API](https://github.com/palemoky/chinese-poetry-api) — 基于 Go 的高性能诗词 API
- [chinese-poetry](https://github.com/chinese-poetry/chinese-poetry) — 40 万首诗词数据集

## 构建

```bash
# 用 Android Studio 打开项目根目录
# 等待 Gradle 同步完成
# 点击 Run 或执行:
./gradlew assembleDebug
```

> 最低支持 Android 8.0 (API 26)，目标 SDK 35

## License

MIT
