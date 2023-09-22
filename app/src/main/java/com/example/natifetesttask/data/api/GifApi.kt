package com.example.natifetesttask.data.api

import com.example.natifetesttask.data.ApiResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface GifApi {

    @GET("search?")
    suspend fun getGifs(
        @Query("api_key") apiKey: String,
        @Query("q") searchQuery: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiResponseData

    @GET("trending?")
    suspend fun getDefaultGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiResponseData

    companion object {
        const val BASE_URL = "https://api.giphy.com/v1/gifs/"
    }
}