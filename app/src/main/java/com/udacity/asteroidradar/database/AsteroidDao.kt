package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AsteroidDao {

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = date('now') order by closeApproachDate asc")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN date('now') AND date('now', '+7 day') order by closeApproachDate asc")
    fun getWeeklyAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid  order by closeApproachDate asc")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("delete from databaseasteroid where closeApproachDate < :today")
    fun clearOldAsteroids(today: String)
}