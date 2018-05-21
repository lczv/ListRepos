package com.android.study.lczv.listrepos.ui.pullRequestList

import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.PullRequest


interface PullRequestListContract {
    interface View {

        fun displayPullRequests(pullRequests: List<PullRequest>)

        fun displayOpenPullRequestsCount(openPullRequestsCount: Int)

        fun displayClosedPullRequestsCount(closedPullRequestsCount: Int)

        fun displayError(owner: String, repository: String, apiRequestStatus: ApiRequestStatus)

    }

    interface Presenter {
        fun onAttach(view: View)

        fun onDetach()

        fun loadPullRequests(owner: String, repository: String, state: String, page: Int)

        fun countPullRequest(owner: String, repository: String, state: String)
    }
}
