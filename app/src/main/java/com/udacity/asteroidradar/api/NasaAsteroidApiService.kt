package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


enum class NasaAsteroidApiFilter(val value: String) {

    SHOW_WEEK("week"), SHOW_TODAY("today"), SHOW_SAVED("saved")

}

/**
 * this retrofit object use scalar converter factory
 */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()


// Moshi object for retrofit

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// retrofit object that use moshi converter factory
private val retrofitMoshi = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(Constants.BASE_URL)
        .build()

interface NasaAsteroidsApiService {


    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
            @Query("start_date") startDate: String?,
            @Query("end_date") endDate: String?,
            @Query("api_key") apiKey: String?): String

    @GET("planetary/apod")
    suspend fun getPicOfDay(@Query("api_key") apiKey: String): NetworkPictureOfDay


    object NasaApi {
        val retrofitScalarService: NasaAsteroidsApiService by lazy {
            retrofit.create(NasaAsteroidsApiService::class.java)
        }
        val retrofitMoshiService: NasaAsteroidsApiService by lazy {
            retrofitMoshi.create(NasaAsteroidsApiService::class.java)
        }
    }
}