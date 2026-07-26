package com.poetry.shijian.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poetry.shijian.data.local.entities.DailyRecEntity
import com.poetry.shijian.data.local.entities.FavoriteEntity
import com.poetry.shijian.data.local.entities.PoemEntity
import com.poetry.shijian.data.local.entities.ReadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PoemDao {

    // ── 诗词缓存 ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoem(poem: PoemEntity)

    @Query("SELECT * FROM poems WHERE id = :id")
    suspend fun getPoem(id: Long): PoemEntity?

    @Query("SELECT * FROM poems WHERE id = :id")
    fun getPoemFlow(id: Long): Flow<PoemEntity?>

    @Query("DELETE FROM poems WHERE cachedAt < :before")
    suspend fun deleteOldCache(before: Long)

    // ── 收藏 ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE poemId = :poemId")
    suspend fun removeFavorite(poemId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE poemId = :poemId)")
    suspend fun isFavorite(poemId: Long): Boolean

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT p.* FROM poems p JOIN favorites f ON p.id = f.poemId ORDER BY f.createdAt DESC")
    fun getFavoritePoems(): Flow<List<PoemEntity>>

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getFavoriteCount(): Int

    // ── 浏览历史 ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReadHistory(history: ReadHistoryEntity)

    @Query("SELECT p.* FROM poems p JOIN read_history h ON p.id = h.poemId ORDER BY h.readAt DESC LIMIT :limit")
    fun getRecentPoems(limit: Int = 50): Flow<List<PoemEntity>>

    @Query("SELECT COUNT(DISTINCT poemId) FROM read_history")
    suspend fun getReadCount(): Int

    // ── 每日推荐 ──

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyRec(rec: DailyRecEntity)

    @Query("SELECT * FROM daily_recommendations WHERE date = :date AND slot = :slot LIMIT 1")
    suspend fun getDailyRec(date: String, slot: String): DailyRecEntity?

    @Query("SELECT * FROM daily_recommendations WHERE date >= :startDate ORDER BY date DESC")
    suspend fun getRecentDailyRecs(startDate: String): List<DailyRecEntity>

    // ── 统计 ──

    @Query("SELECT COUNT(*) FROM poems")
    suspend fun getCachedPoemCount(): Int
}
