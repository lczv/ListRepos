package com.android.study.lczv.listrepos.ui.repositoryList

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.data.model.Repository
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import kotlinx.android.synthetic.main.activity_repository_list.*


open class RepositoryListActivity : AppCompatActivity(), RepositoryListContract.View,RepositoryListAdapter.LoadMoreRepositoriesListener {

    lateinit var presenter: RepositoryListContract.Presenter

    lateinit var adapter: RepositoryListAdapter
    lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        presenter = RepositoryListPresenter(ApiCaller.api)
        presenter.onAttach(this)

        val layoutManager = LinearLayoutManager(this)

        adapter = RepositoryListAdapter(this)
        adapter.sorting = "stars"

        recyclerview_repository_list.layoutManager = layoutManager
        recyclerview_repository_list.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(recyclerview_repository_list.context, layoutManager.getOrientation())
        recyclerview_repository_list.addItemDecoration(dividerItemDecoration)

        presenter.loadRepositories(0, adapter.sorting)

    }


    override fun displayRepositories(repositories: List<Repository>) {

        adapter.repositories.addAll(repositories)
        adapter.notifyDataSetChanged()

    }


    override open fun displayError() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.connection_error), Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(getString(R.string.reload_list), object : View.OnClickListener {
            override fun onClick(v: View?) {
                presenter.loadRepositories(adapter.currentPage, adapter.sorting)
            }
        }).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.getItemId()) {
            R.id.item_sort_by_stars -> {

                adapter.clearList()
                adapter.sorting = "stars"
                presenter.loadRepositories(1, adapter.sorting)
                item.setChecked(!item.isChecked)

                return true
            }
            R.id.item_sort_by_forks -> {

                adapter.clearList()
                adapter.sorting = "forks"
                presenter.loadRepositories(1, adapter.sorting)

                item.setChecked(!item.isChecked)
                return true
            }
            R.id.item_sort_by_update -> {
                adapter.clearList()
                adapter.sorting = "updated"
                presenter.loadRepositories(1, adapter.sorting)
                item.setChecked(!item.isChecked)

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }

    override fun loadMoreRepositories(currentPage: Int, sorting: String) {
        presenter.loadRepositories(currentPage, sorting)
    }
}
