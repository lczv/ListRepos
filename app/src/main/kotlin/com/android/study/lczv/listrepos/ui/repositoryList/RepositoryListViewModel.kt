package com.android.study.lczv.listrepos.ui.repositoryList

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.ApiResponse
import com.android.study.lczv.listrepos.data.model.Repository
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryListViewModel : ViewModel() {

    open val status = MutableLiveData<ApiRequestStatus>()
    open val repositories = MutableLiveData<List<Repository>>()
    open val sort = MutableLiveData<String>().apply { postValue("stars") }


    open fun loadRepositoryList(page: Int) {

        ApiCaller.api.loadRepositories(page, sort.value
                ?: "stars").enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>?, response: Response<ApiResponse>?) {

                if (response?.isSuccessful == true) {
                    repositories.postValue(response?.body()?.items)
                    status.postValue(ApiRequestStatus.SUCCESS)
                } else {
                    status.postValue(ApiRequestStatus.ERROR)
                }

            }

            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                status.postValue(ApiRequestStatus.ERROR)
            }

        })

    }

}
