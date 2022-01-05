package com.ravi.kutukidemo

import android.app.Application
import androidx.lifecycle.*
import com.ravi.kutukidemo.data.Repository
import com.ravi.kutukidemo.model.VideoCategoriesModel
import com.ravi.kutukidemo.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var videoResponse: MutableLiveData<NetworkResult<VideoCategoriesModel>> = MutableLiveData()

    fun getQuestions() = viewModelScope.launch {
        getQuestionsSafeCall()
    }

     private suspend fun getQuestionsSafeCall() {
        videoResponse.value = NetworkResult.Loading()
            try {
                val response = repository.remote.getCategories()
                videoResponse.value = handleQuestionsResponse(response)

                val ques = videoResponse.value!!.data

            } catch (e: Exception) {
                videoResponse.value = NetworkResult.Error("Questions not found.")
            }
    }

    private fun handleQuestionsResponse(response: Response<VideoCategoriesModel>): NetworkResult<VideoCategoriesModel> {
        return when {
            response.isSuccessful -> {
                val questions = response.body()
                NetworkResult.Success(questions!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

}