package com.poetry.shijian.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poems")
data class PoemEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,
    val type: String,
    val notes: String? = null,
    val appreciation: String? = null,
    val hashGradient: String = "",
    val cachedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val poemId: Long,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "read_history")
data class ReadHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val poemId: Long,
    val readAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "daily_recommendations")
data class DailyRecEntity(
    @PrimaryKey val date: String,         // "2026-07-26"
    val poemId: Long,
    val slot: String,                      // "morning" / "afternoon" / "night"
    val recommendedAt: Long = System.currentTimeMillis(),
)
