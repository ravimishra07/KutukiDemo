package com.ravi.kutukidemo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.ravi.kutukidemo.data.Repository
import com.ravi.kutukidemo.model.VideoCategories
import com.ravi.kutukidemo.model.VideoCategoriesModel
import com.ravi.kutukidemo.model.Videos
import com.ravi.kutukidemo.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var videoResponse: MutableLiveData<NetworkResult<VideoCategoriesModel>> = MutableLiveData()
    var videoCategoryList: MutableLiveData<NetworkResult<MutableList<VideoCategories>>> =
        MutableLiveData()
    var videoList: MutableLiveData<NetworkResult<MutableList<Videos>>> = MutableLiveData()

    fun getVideoCategories() = viewModelScope.launch {
        getVideoCategoriesCall()
    }
    fun getVideos() = viewModelScope.launch {
        getVideoApiCall()
    }

    private suspend fun getVideoCategoriesCall() {
        videoCategoryList.value = NetworkResult.Loading()
        try {
            val response = repository.remote.getCategories()
            videoCategoryList.value = handleVideoCategoriesResponse(response)
        } catch (e: Exception) {
            videoCategoryList.value = NetworkResult.Error("categories not found.")
        }
    }

    private suspend fun getVideoApiCall() {
        videoList.value = NetworkResult.Loading()
        try {
            val response = repository.remote.getVideos()
            videoList.value = handleVideoResponse(response)
        } catch (e: Exception) {
            videoList.value = NetworkResult.Error("videos not found.")
        }
    }

    private fun handleVideoCategoriesResponse(response: Response<VideoCategoriesModel>): NetworkResult<MutableList<VideoCategories>> {

        return when {
            response.isSuccessful -> {

                val vidCatList: MutableList<VideoCategories> = mutableListOf()
                val jsonResponse = response.body()?.response

                jsonResponse?.let {
                    for (data in it.entrySet()) {
                        try {
                            val vidData: JsonObject = data.value as JsonObject
                            Log.v("mainViewModel", vidData.toString())

                            for (vidCategories in vidData.entrySet()) {
                                val obj: JsonObject = vidCategories.value as JsonObject
                                val name = obj["name"].toString().replace("\"", "")
                                val image = obj["image"].toString().replace("\"", "")
                                val vidModel = VideoCategories(name, image)
                                vidCatList.add(vidModel)
                            }

                        } catch (e: JSONException) {
                            // Something went wrong!
                            Log.v("mainViewModel", e.localizedMessage)
                        }
                    }
                }
                NetworkResult.Success(vidCatList)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleVideoResponse(response: Response<VideoCategoriesModel>): NetworkResult<MutableList<Videos>> {

        return when {
            response.isSuccessful -> {

                val videoList: MutableList<Videos> = mutableListOf()
                val jsonResponse = response.body()?.response//.keySet()

                jsonResponse?.let {
                    for (data in it.entrySet()) {
                        try {
                            val vidData: JsonObject = data.value as JsonObject
                            Log.v("mainViewModel", vidData.toString())

                            for (vidCategories in vidData.entrySet()) {
                                val obj: JsonObject = vidCategories.value as JsonObject
                                val title = obj["title"].toString().replace("\"", "")
                                val description = obj["description"].toString().replace("\"", "")
                                val thumbnailURL = obj["thumbnailURL"].toString().replace("\"", "")
                                val videoURL = obj["videoURL"].toString().replace("\"", "")
                                val categories = obj["categories"].toString().replace("\"", "")
          
                                val vidModel = Videos(title, description,videoURL,thumbnailURL,categories)
                                videoList.add(vidModel)
                            }

                        } catch (e: JSONException) {
                            // Something went wrong!
                            Log.v("mainViewModel", e.localizedMessage)
                        }
                    }
                }
                NetworkResult.Success(videoList)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

}