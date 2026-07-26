package com.poetry.shijian.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/** 诗泉 Chinese Poetry API */
interface PoetryApi {

    /** 随机一首诗词 */
    @GET("api/v1/poems/random")
    suspend fun getRandomPoem(
        @Query("author") author: String? = null,
        @Query("type") type: String? = null,
        @Query("dynasty") dynasty: String? = null,
        @Query("char") char: String? = null,
        @Query("lang") lang: String = "zh-Hans",
    ): PoemResponse

    /** 搜索诗词 */
    @GET("api/v1/poems/search")
    suspend fun searchPoems(
        @Query("q") query: String,
        @Query("type") type: String = "all",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("lang") lang: String = "zh-Hans",
    ): PoemSearchResponse

    /** 获取作者列表 */
    @GET("api/v1/authors")
    suspend fun getAuthors(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
    ): AuthorListResponse

    /** 获取朝代列表 */
    @GET("api/v1/dynasties")
    suspend fun getDynasties(): DynastyListResponse

    /** 获取诗词体裁列表 */
    @GET("api/v1/types")
    suspend fun getTypes(): TypeListResponse

    /** 统计信息 */
    @GET("api/v1/stats")
    suspend fun getStats(): StatsResponse
}

// ── Response DTO ──

data class PoemResponse(
    val id: Long,
    val title: String,
    val author: String,
    val dynasty: String,
    val content: String,
    val type: String,
    val notes: String? = null,
    val appreciation: String? = null,
)

data class PoemSearchResponse(
    val poems: List<PoemResponse>,
    val total: Int,
    val page: Int,
)

data class AuthorListResponse(
    val authors: List<AuthorResponse>,
    val total: Int,
)

data class AuthorResponse(
    val id: Long,
    val name: String,
    val dynasty: String,
    val count: Int,
)

data class DynastyListResponse(
    val dynasties: List<DynastyResponse>,
)

data class DynastyResponse(
    val id: Long,
    val name: String,
    val count: Int,
)

data class TypeListResponse(
    val types: List<TypeResponse>,
)

data class TypeResponse(
    val id: Long,
    val name: String,
    val count: Int,
)

data class StatsResponse(
    val totalPoems: Int,
    val totalAuthors: Int,
    val poemsByDynasty: List<DynastyCount>,
)

data class DynastyCount(
    val dynasty: String,
    val count: Int,
)
