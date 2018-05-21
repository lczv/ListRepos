package com.android.study.lczv.listrepos.ui.pullRequestList

import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.PullRequest
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import com.android.study.lczv.listrepos.data.repository.GitHubAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PullRequestListPresenter(val api: GitHubAPI) : PullRequestListContract.Presenter {

    var view: PullRequestListContract.View? = null

    val itemsPerPage = 30

    override fun onAttach(view: PullRequestListContract.View) {
        this.view = view
    }

    override fun onDetach() {
        this.view = null
    }

    override fun loadPullRequests(owner: String, repository: String, state: String, page: Int) {

        api.loadPullRequests(owner, repository, state, page).enqueue(object : Callback<List<PullRequest>> {

            override fun onResponse(call: Call<List<PullRequest>>?, response: Response<List<PullRequest>>?) {

                if (response?.code() == 403) {
                    view?.displayError(owner, repository, ApiRequestStatus.API_LIMIT_EXCEEDED)
                    return
                }

                if (response?.isSuccessful == true) {

                    view?.displayPullRequests(response.body()!!)

                    if(page==0){
                        countPullRequest(owner, repository, "open")
                        countPullRequest(owner, repository, "closed")
                    }

                } else {
                    view?.displayError(owner, repository, ApiRequestStatus.ERROR)
                }
            }

            override fun onFailure(call: Call<List<PullRequest>>?, t: Throwable?) {
                view?.displayError(owner, repository, ApiRequestStatus.ERROR)
            }

        })

    }

    override fun countPullRequest(owner: String, repository: String, state: String) {

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
                                            view?.displayOpenPullRequestsCount((response?.body()?.size!!.toInt() + (itemsPerPage * lastPage)))
                                        } else {
                                            view?.displayClosedPullRequestsCount((response?.body()?.size!!.toInt() + (itemsPerPage * lastPage)))
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
