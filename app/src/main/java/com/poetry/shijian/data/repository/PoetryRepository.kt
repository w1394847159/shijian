package com.poetry.shijian.data.repository

import com.poetry.shijian.data.local.PoemDao
import com.poetry.shijian.data.local.entities.DailyRecEntity
import com.poetry.shijian.data.local.entities.FavoriteEntity
import com.poetry.shijian.data.local.entities.PoemEntity
import com.poetry.shijian.data.local.entities.ReadHistoryEntity
import com.poetry.shijian.data.remote.PoetryApi
import com.poetry.shijian.data.remote.PoemResponse
import com.poetry.shijian.domain.model.Poem
import com.poetry.shijian.domain.model.PoemBrief
import com.poetry.shijian.domain.model.TimeSlot
import com.poetry.shijian.util.HashGradientGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PoetryRepository(
    private val api: PoetryApi,
    private val dao: PoemDao,
) {
    // ── 随机一首 ──

    suspend fun getRandomPoem(author: String? = null, type: String? = null): Poem {
        val response = api.getRandomPoem(author = author, type = type)
        return response.toPoem()
    }

    // ── 每日推荐 ──

    suspend fun getDailyPoem(slot: TimeSlot): Poem {
        val date = today()
        val slotName = slot.name.lowercase()

        // 检查今日该时段是否已有推荐
        val existing = dao.getDailyRec(date, slotName)
        if (existing != null) {
            val cached = dao.getPoem(existing.poemId)
            if (cached != null) return cached.toDomainPoem()
        }

        // 按时段风格请求
        val tag = when (slot) {
            TimeSlot.MORNING -> null   // 清晨诗，用默认随机
            TimeSlot.AFTERNOON -> null // 闲适诗
            TimeSlot.NIGHT -> null     // 夜诗
        }
        // 诗泉API暂不支持按时间过滤，先用纯随机
        val response = api.getRandomPoem()
        val poem = response.toPoem()

        // 缓存
        dao.insertPoem(poem.toEntity())
        dao.saveDailyRec(DailyRecEntity(date = date, slot = slotName, poemId = poem.id))

        return poem
    }

    // ── 换一首（智能随机） ──

    suspend fun shufflePoem(currentPoemId: Long, preferSimilar: Boolean = false): Poem {
        if (preferSimilar) {
            // 获取当前诗信息
            val current = dao.getPoem(currentPoemId)
            if (current != null) {
                try {
                    // 60%概率同作者
                    val response = api.getRandomPoem(author = current.author)
                    if (response.id != currentPoemId) return response.toPoem()
                } catch (_: Exception) { /* 降级到纯随机 */ }
            }
        }
        return getRandomPoem()
    }

    // ── 搜索 ──

    suspend fun searchPoems(query: String, type: String = "all", page: Int = 1): List<PoemBrief> {
        val response = api.searchPoems(query = query, type = type, page = page)
        return response.poems.map { it.toBrief() }
    }

    // ── 收藏 ──

    fun getAllFavorites(): Flow<List<Poem>> {
        return dao.getFavoritePoems().map { list -> list.map { it.toDomainPoem() } }
    }

    suspend fun isFavorite(poemId: Long): Boolean = dao.isFavorite(poemId)

    suspend fun toggleFavorite(poemId: Long) {
        if (dao.isFavorite(poemId)) {
            dao.removeFavorite(poemId)
        } else {
            dao.addFavorite(FavoriteEntity(poemId = poemId))
        }
    }

    suspend fun getFavoriteCount(): Int = dao.getFavoriteCount()

    // ── 浏览历史 ──

    fun getRecentPoems(limit: Int = 50): Flow<List<Poem>> {
        return dao.getRecentPoems(limit).map { list -> list.map { it.toDomainPoem() } }
    }

    suspend fun getReadCount(): Int = dao.getReadCount()

    suspend fun recordRead(poemId: Long) {
        dao.addReadHistory(ReadHistoryEntity(poemId = poemId))
    }

    // ── 诗词详情 ──

    suspend fun getPoem(id: Long): Poem? {
        val entity = dao.getPoem(id)
        return entity?.toDomainPoem()
    }

    // ── 本周回顾 ──

    suspend fun getWeekReview(): List<DailyRecEntity> {
        val start = java.time.LocalDate.now().minusDays(6).toString()
        return dao.getRecentDailyRecs(start)
    }

    // ── 数据迁移 ──

    private fun PoemResponse.toPoem(): Poem {
        val gradient = HashGradientGenerator.generateHslString(content)
        return Poem(
            id = id,
            title = title,
            author = author,
            dynasty = dynasty,
            content = content,
            type = type,
            notes = notes,
            appreciation = appreciation,
            hashGradient = gradient,
        )
    }

    private fun PoemResponse.toBrief(): PoemBrief {
        val firstLine = content.lines().firstOrNull()?.take(20) ?: ""
        return PoemBrief(
            id = id,
            title = title,
            author = author,
            dynasty = dynasty,
            firstLine = firstLine + "…",
            type = type,
        )
    }

    private fun PoemEntity.toDomainPoem(): Poem {
        return Poem(
            id = id,
            title = title,
            author = author,
            dynasty = dynasty,
            content = content,
            type = type,
            notes = notes,
            appreciation = appreciation,
            hashGradient = hashGradient,
        )
    }

    private fun Poem.toEntity(): PoemEntity {
        return PoemEntity(
            id = id,
            title = title,
            author = author,
            dynasty = dynasty,
            content = content,
            type = type,
            notes = notes,
            appreciation = appreciation,
            hashGradient = hashGradient,
        )
    }

    private fun today(): String = java.time.LocalDate.now().toString()
}
