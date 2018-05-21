package com.android.study.lczv.listrepos.ui.pullRequestList

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.R.id.*
import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.PullRequest
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListAdapter
import kotlinx.android.synthetic.main.activity_pull_request_list.*


open class PullRequestListActivity : AppCompatActivity(), PullRequestListContract.View, PullRequestListAdapter.LoadMorePullRequestsListener {

    lateinit var presenter: PullRequestListContract.Presenter

    private lateinit var adapter: PullRequestListAdapter
    private lateinit var snackbar: Snackbar

    lateinit var owner: String
    lateinit var repository: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_request_list)

        presenter = PullRequestListPresenter(ApiCaller.api)
        presenter.onAttach(this)

        owner = intent.getStringExtra(RepositoryListAdapter.EXTRA_OWNER)
        repository = intent.getStringExtra(RepositoryListAdapter.EXTRA_REPOSITORY)
        val layoutManager = LinearLayoutManager(this)

        title = owner + " / " + repository

        textview_opened_pull_requests.text = getString(R.string.opened_pull_requests, "-")
        textview_closed_pull_requests.text = getString(R.string.closed_pull_requests, "-")

        linearLayout_pull_request_status.visibility = View.INVISIBLE

        adapter = PullRequestListAdapter(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerview_pull_request_list.layoutManager = layoutManager
        recyclerview_pull_request_list.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(recyclerview_pull_request_list.context, layoutManager.getOrientation())
        recyclerview_pull_request_list.addItemDecoration(dividerItemDecoration)

        presenter.loadPullRequests(owner, repository, "all", 0)

    }

    override fun displayPullRequests(pullRequests: List<PullRequest>) {

        adapter.pullRequests.addAll(pullRequests)
        adapter.owner = owner
        adapter.repository = repository
        adapter.state = "all"

        if (pullRequests.size < 30) {
            adapter.listEnded = true
        }

        adapter.notifyDataSetChanged()

    }

    override fun displayError(owner: String, repository: String, apiRequestStatus: ApiRequestStatus) {
        when (apiRequestStatus) {
            ApiRequestStatus.API_LIMIT_EXCEEDED ->
                snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.api_limit_exceeded), Snackbar.LENGTH_INDEFINITE)
            ApiRequestStatus.ERROR ->
                snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.connection_error), Snackbar.LENGTH_INDEFINITE)
        }
        snackbar.setAction(getString(R.string.reload_list), object : View.OnClickListener {
            override fun onClick(v: View?) {
                presenter.loadPullRequests(owner, repository, "all", adapter.currentPage)
                presenter.countPullRequest(owner, repository, "open")
                presenter.countPullRequest(owner, repository, "closed")
            }
        }).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }

    override fun displayOpenPullRequestsCount(openPullRequestsCount: Int) {
        textview_opened_pull_requests.text = getString(R.string.opened_pull_requests, openPullRequestsCount.toString())
        linearLayout_pull_request_status.visibility = View.VISIBLE
    }

    override fun displayClosedPullRequestsCount(closedPullRequestsCount: Int) {
        textview_closed_pull_requests.text = getString(R.string.closed_pull_requests, closedPullRequestsCount.toString())
        linearLayout_pull_request_status.visibility = View.VISIBLE
    }

    override fun loadMorePullRequests(owner: String, repository: String, state: String, lastPage: Int) {
        presenter.loadPullRequests(owner, repository, "all", 0)
    }
}
