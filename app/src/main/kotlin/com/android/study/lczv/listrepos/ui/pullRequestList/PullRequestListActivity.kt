package com.android.study.lczv.listrepos.ui.pullRequestList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.R.id.*
import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListAdapter
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListViewModel
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListAdapter
import kotlinx.android.synthetic.main.activity_pull_request_list.*


class PullRequestListActivity : AppCompatActivity(), PullRequestListAdapter.LoadMorePullRequestsListener {

    private lateinit var adapter: PullRequestListAdapter
    private lateinit var snackbar: Snackbar

    val repositoryDetailViewModel by lazy {
        ViewModelProviders.of(this).get(PullRequestListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_request_list)

        val owner = intent.getStringExtra(RepositoryListAdapter.EXTRA_OWNER)
        val repository = intent.getStringExtra(RepositoryListAdapter.EXTRA_REPOSITORY)
        val layoutManager = LinearLayoutManager(this)

        title = "$owner  /  $repository"

        textview_opened_pull_requests.text = getString(R.string.opened_pull_requests, "-")
        textview_closed_pull_requests.text = getString(R.string.closed_pull_requests, "-")

        linearLayout_pull_request_status.visibility = View.GONE

        adapter = PullRequestListAdapter(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerview_pull_request_list.layoutManager = layoutManager
        recyclerview_pull_request_list.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(recyclerview_pull_request_list.context, layoutManager.getOrientation())
        recyclerview_pull_request_list.addItemDecoration(dividerItemDecoration)

        repositoryDetailViewModel.loadPullRequestList(owner, repository, "all", 0)

        repositoryDetailViewModel.status.observe(this, Observer {
            if (it == ApiRequestStatus.ERROR) {
                showErrorSnackbar(owner, repository, getString(R.string.connection_error))
            } else if (it == ApiRequestStatus.API_LIMIT_EXCEEDED) {
                showErrorSnackbar(owner, repository, getString(R.string.api_limit_exceeded))
            } else {

            }
        })

        repositoryDetailViewModel.openPullRequestCount.observe(this, Observer {
            textview_opened_pull_requests.text = getString(R.string.opened_pull_requests, it.toString())
            linearLayout_pull_request_status.visibility = View.VISIBLE
        })

        repositoryDetailViewModel.closedPullRequestCount.observe(this, Observer {
            textview_closed_pull_requests.text = getString(R.string.closed_pull_requests, it.toString())
            linearLayout_pull_request_status.visibility = View.VISIBLE
        })

        repositoryDetailViewModel.pullRequests.observe(this, Observer {
            adapter.pullRequests.addAll(it!!)
            adapter.owner = owner
            adapter.repository = repository
            adapter.state = "all"

            if (it.size < 30) {
                adapter.listEnded = true
            }

            adapter.notifyDataSetChanged()
        })

    }

    open fun showErrorSnackbar(owner: String, repository: String, message: String) {
        snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(getString(R.string.reload_list), object : View.OnClickListener {
            override fun onClick(v: View?) {
                loadMorePullRequests(owner, repository, "all", adapter.currentPage)
            }
        }).show()
    }

    override fun loadMorePullRequests(owner: String, repository: String, state: String, lastPage: Int) {
        repositoryDetailViewModel.loadPullRequestList(owner, repository, "all", adapter.currentPage)
    }

}
