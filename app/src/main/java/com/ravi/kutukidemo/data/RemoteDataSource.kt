package com.ravi.kutukidemo.data

import com.ravi.kutukidemo.model.VideoCategoriesModel
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: RetrofitApi
    ) {
    suspend fun getCategories(): Response<VideoCategoriesModel> {
        return api.getVideoCategory()
    }
}