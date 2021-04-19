package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PictureDao {

    @Query("delete from databasepictureofday where created_at < :today")
    fun clearOldPictureOfDay(today: String)

    // Picture of the day
    @Query("select * from databasepictureofday order by created_at desc limit 1")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)
}
