package com.ravi.kutukidemo.data

import com.ravi.kutukidemo.model.VideoCategoriesModel
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitApi {

    @GET("v2/5e2bebd23100007a00267e51")
    suspend fun getVideoCategory(): Response<VideoCategoriesModel>

    @GET("v2/5e2beb5a3100006600267e4e")
    suspend fun getVideos(): Response<VideoCategoriesModel>
}