package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository

import retrofit2.HttpException

/**
 * To schedule this we override the Application and also update it in the manifest
 */
class RefreshDataWorker(appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    /**
     * A coroutine-friendly method to do your work.
     * Note: In recent work version upgrade, 1.0.0-alpha12 and onwards have a breaking change.
     * The doWork() function now returns Result instead of Payload because they have combined Payload into Result.
     * Read more here - https://developer.android.com/jetpack/androidx/releases/work#1.0.0-alpha12
     */
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids()
            repository.refreshPictureOfDay()
            repository.clearOldAsteroids()
            repository.clearOldPictureOfDay()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}