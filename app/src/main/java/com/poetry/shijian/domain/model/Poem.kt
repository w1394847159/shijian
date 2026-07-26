package com.poetry.shijian.domain.model

/** 诗词领域模型 */
data class Poem(
    val id: Long,
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,          // 全文，含换行符分隔
    val type: String,             // 五言绝句 / 七言律诗 / 词 / 曲 ...
    val notes: String? = null,    // 注释
    val appreciation: String? = null, // 赏析
    val hashGradient: String = "",    // "H,S,L" 格式的HSL色值
    val isFavorite: Boolean = false,
    val isRead: Boolean = false,
)

/** 每日推荐的时段 */
enum class TimeSlot(val label: String) {
    MORNING("晨诗"),
    AFTERNOON("闲适"),
    NIGHT("夜诗"),
}

/** 诗词简略信息（列表用） */
data class PoemBrief(
    val id: Long,
    val title: String,
    val author: String,
    val dynasty: String,
    val firstLine: String,
    val type: String,
)
