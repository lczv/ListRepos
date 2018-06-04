package com.android.study.lczv.listrepos.ui.pullRequestList

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.PullRequest
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PullRequestListViewModel : ViewModel() {

    open val status = MutableLiveData<ApiRequestStatus>()
    open val pullRequests = MutableLiveData<List<PullRequest>>()
    open val openPullRequestCount = MutableLiveData<Int>()
    open val closedPullRequestCount = MutableLiveData<Int>()

    val itemsPerPage = 30

    fun loadPullRequestList(owner: String, repository: String, state: String, page: Int) {
        ApiCaller.api.loadPullRequests(owner, repository, state, page).enqueue(object : Callback<List<PullRequest>> {


            override fun onResponse(call: Call<List<PullRequest>>?, response: Response<List<PullRequest>>?) {

                if (response?.code() == 403) {
                    status.postValue(ApiRequestStatus.API_LIMIT_EXCEEDED)
                    return
                }

                if (response?.isSuccessful == true) {

                    pullRequests.postValue(response?.body())
                    status.postValue(ApiRequestStatus.SUCCESS)

                    if(page==0){
                        countPullRequest(owner, repository, "open")
                        countPullRequest(owner, repository, "closed")
                    }

                } else {
                    status.postValue(ApiRequestStatus.ERROR)

                }
            }

            override fun onFailure(call: Call<List<PullRequest>>?, t: Throwable?) {
                status.postValue(ApiRequestStatus.ERROR)
            }


        })
    }

    /**
     * In order to obtain the number of open / closed PR's,
     * We need to request the last page from both the open and closed PR's list.
     * e.g. since the pagination returns 30 items by default, the number of open PR's,
     * will be (perPageItems * pages) + lastPageItems
     * */

    fun countPullRequest(owner: String, repository: String, state: String) {

        var lastPage = 0
        var pullRequestCount: Int?

        if (state == "open" || state == "closed") {

            ApiCaller.api.loadPullRequests(owner, repository, state, 1).enqueue(object : Callback<List<PullRequest>> {

                override fun onResponse(call: Call<List<PullRequest>>?, response: Response<List<PullRequest>>?) {

                    if (response?.isSuccessful == true) {

                        // Gets the last page number
                        val headers = response.headers()
                        lastPage = (headers.get("Link")?.split(";")?.get(1)?.split("page=")?.get(1)?.replace(">", ""))?.toInt() ?: 1

                        if (lastPage != null) {
                            // Calls the API again in order to obtain the last page items
                            ApiCaller.api.loadPullRequests(owner, repository, state, lastPage).enqueue(object : Callback<List<PullRequest>> {
                                override fun onFailure(call: Call<List<PullRequest>>?, t: Throwable?) {}

                                override fun onResponse(call: Call<List<PullRequest>>?, response: Response<List<PullRequest>>?) {

                                    pullRequestCount = response?.body()?.size

                                    if (pullRequestCount != null) {

                                        if (lastPage == 1) {
                                            lastPage = 0
                                        } else {
                                            lastPage--
                                        }

                                        if (state.equals("open")) {
                                            openPullRequestCount.postValue((response?.body()?.size!!.toInt() + (itemsPerPage * lastPage)))
                                        } else {
                                            closedPullRequestCount.postValue((response?.body()?.size!!.toInt() + (itemsPerPage * lastPage)))
                                        }

                                    }

                                }
                            })
                        }

                    }
                }

                override fun onFailure(call: Call<List<PullRequest>>?, t: Throwable?) {
                }

            })
        }
    }

}
