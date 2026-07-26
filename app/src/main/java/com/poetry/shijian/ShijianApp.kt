package com.poetry.shijian

import android.app.Application
import com.poetry.shijian.data.local.AppDatabase
import com.poetry.shijian.data.remote.PoetryApiClient
import com.poetry.shijian.data.repository.PoetryRepository
import com.poetry.shijian.util.CrashLogger

class ShijianApp : Application() {

    lateinit var repository: PoetryRepository
        private set

    override fun onCreate() {
        // 必须在最前面注册崩溃捕获
        CrashLogger.install(this)

        super.onCreate()
        instance = this

        try {
            val dao = AppDatabase.getInstance(this).poemDao()
            repository = PoetryRepository(
                api = PoetryApiClient.api,
                dao = dao,
            )
        } catch (e: Exception) {
            // 初始化失败时会由 CrashLogger 记录
            throw e
        }
    }

    companion object {
        lateinit var instance: ShijianApp
            private set
    }
}
