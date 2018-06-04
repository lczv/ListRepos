package com.android.study.lczv.listrepos.ui.repositoryList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.data.ApiRequestStatus
import kotlinx.android.synthetic.main.activity_repository_list.*


class RepositoryListActivity : AppCompatActivity(), RepositoryListAdapter.LoadMoreRepositoriesListener {

    lateinit var adapter: RepositoryListAdapter
    lateinit var snackbar: Snackbar

    val repositoryListViewModel by lazy {
        ViewModelProviders.of(this).get(RepositoryListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        val layoutManager = LinearLayoutManager(this)

        adapter = RepositoryListAdapter(this)

        recyclerview_repository_list.layoutManager = layoutManager
        recyclerview_repository_list.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(recyclerview_repository_list.context, layoutManager.getOrientation())
        recyclerview_repository_list.addItemDecoration(dividerItemDecoration)

        repositoryListViewModel.status.observe(this, Observer {
            if (it == ApiRequestStatus.ERROR) {
                showErrorSnackbar()
            } else if (it == ApiRequestStatus.SUCCESS) {
            }
        })

        repositoryListViewModel.repositories.observe(this, Observer {
            adapter.repositories.addAll(it!!)

            adapter.notifyDataSetChanged()
        })

        repositoryListViewModel.sort.observe(this, Observer {
            adapter.repositories.clear()
            adapter.notifyDataSetChanged()
            adapter.currentPage = 1
            repositoryListViewModel.loadRepositoryList(1)
            Log.d("debug", "ATUALIZADO")
        })

    }


    open fun showErrorSnackbar() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.connection_error), Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(getString(R.string.reload_list), object : View.OnClickListener {
            override fun onClick(v: View?) {
                repositoryListViewModel.loadRepositoryList(adapter.currentPage)
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
                repositoryListViewModel.sort.postValue("stars")
                item.setChecked(!item.isChecked)
                return true
            }
            R.id.item_sort_by_forks -> {
                repositoryListViewModel.sort.postValue("forks")
                item.setChecked(!item.isChecked)
                return true
            }
            R.id.item_sort_by_update -> {
                repositoryListViewModel.sort.postValue("updated")
                item.setChecked(!item.isChecked)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun loadMoreRepositories(currentPage: Int) {
        repositoryListViewModel.loadRepositoryList(currentPage)
    }
}
