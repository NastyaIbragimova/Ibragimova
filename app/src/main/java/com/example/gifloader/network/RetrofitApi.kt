package com.example.gifloader.network

import com.example.gifloader.data.GifArray
import com.example.gifloader.data.GifInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitApi {
    @GET("random?json=true")
    fun getRandom(): Call<GifInfo>

    @GET("hot/{page}?json=true")
    fun getHot(@Path("page") page: Int): Call<GifArray>

    @GET("latest/{page}?json=true")
    fun getLatest(@Path("page") page: Int): Call<GifArray>

    @GET("top/{page}?json=true")
    fun getTop(@Path("page") page: Int): Call<GifArray>
}