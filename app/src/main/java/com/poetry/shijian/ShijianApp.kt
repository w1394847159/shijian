package com.poetry.shijian

import android.app.Application
import com.poetry.shijian.data.local.AppDatabase
import com.poetry.shijian.data.remote.PoetryApiClient
import com.poetry.shijian.data.repository.PoetryRepository

class ShijianApp : Application() {

    lateinit var repository: PoetryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        val dao = AppDatabase.getInstance(this).poemDao()
        repository = PoetryRepository(
            api = PoetryApiClient.api,
            dao = dao,
        )
    }

    companion object {
        lateinit var instance: ShijianApp
            private set
    }
}
