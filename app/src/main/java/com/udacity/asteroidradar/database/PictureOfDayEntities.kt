package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.PictureOfDay


@Entity
data class DatabasePictureOfDay constructor(
        @PrimaryKey
        val url: String,

        @ColumnInfo(name = "created_at")
        val createdAt: Long,

        val mediaType: String,
        val title: String)


/**
 * Kotlin extension function to get domain models from the database models
 */
fun DatabasePictureOfDay.asDomainModel(): PictureOfDay {
    return PictureOfDay(
            url = this.url,
            mediaType = this.mediaType,
            title = this.title)
}
