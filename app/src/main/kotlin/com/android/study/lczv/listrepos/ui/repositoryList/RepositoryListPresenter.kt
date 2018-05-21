package com.android.study.lczv.listrepos.ui.repositoryList

import com.android.study.lczv.listrepos.data.model.ApiResponse
import com.android.study.lczv.listrepos.data.repository.GitHubAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class RepositoryListPresenter(val api: GitHubAPI) : RepositoryListContract.Presenter {

    var view: RepositoryListContract.View? = null

    override fun onAttach(view: RepositoryListContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun loadRepositories(page: Int, sorting: String) {
        api.loadRepositories(page, sorting).enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>?, response: Response<ApiResponse>?) {

                if (response?.isSuccessful == true) {
                    view?.displayRepositories(response.body()?.items!!)
                } else {
                    view?.displayError()
                }

            }

            override fun onFailure(call: Call<ApiResponse>?, t: Throwable?) {
                view?.displayError()
            }

        })
    }

}
