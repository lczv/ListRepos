package com.android.study.lczv.listrepos.ui.repositoryList

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.data.model.Repository
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.repository_list_item.view.*

class RepositoryListAdapter(val listener: LoadMoreRepositoriesListener) : RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {

    val repositories = mutableListOf<Repository>()
    var currentPage = 1
    private val itemsPerPage = 30
    lateinit var sorting: String
//    var listEnded = false

    companion object {
        const val EXTRA_OWNER = "EXTRA_OWNER"
        const val EXTRA_REPOSITORY = "EXTRA_REPOSITORY"
    }

    /**
     *Should be number of repositories + 1, in order to be able to insert the
     *ProgressBar, without overriding the last element
     * */
    override fun getItemCount() = repositories.size + 1


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        lateinit var view: View

        if (viewType == 0) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.repository_list_item, parent, false)
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.repository_list_item_load, parent, false)
        }/*else if(viewType == 3){
            view = LayoutInflater.from(parent?.context).inflate(R.layout.repository_list_item_load, parent, false)
        }*/ else if (viewType == 2) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.repository_list_item_load_empty, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        if (position == (itemsPerPage * currentPage)) {
            currentPage++
            listener.loadMoreRepositories(currentPage, sorting)
        }

        if (getItemViewType(position) == 0) {
            holder?.bind(repositories[position])
        }

    }

    override fun getItemViewType(position: Int): Int {

        // Default layout to inflate when the list is empty
        if (repositories.isEmpty()) {
            return 2
        }
        // Layout to be inflated after the last item of the list
        else if (repositories.size > 0 && position == repositories.size) {
            return 1
        }
        // Default repository item layout
        return 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        lateinit var repositoryTemp: Repository

        fun bind(repository: Repository) {

            repositoryTemp = repository

            itemView.textview_repository_name.text = repository.name ?: ""
            itemView.textview_repository_description.text = repository.description ?: ""
            itemView.textview_fork_count.text = repository.forksCount.toString()
            itemView.textview_star_count.text = repository.stargazersCount.toString()
            itemView.textview_user_name.text = repository.owner?.login ?: ""

            Picasso.with(itemView.context).load(repository.owner?.avatarUrl).into(itemView.imageview_user_avatar)

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val intent = Intent(itemView.context, PullRequestListActivity::class.java)
            intent.putExtra(EXTRA_OWNER, repositoryTemp.owner?.login)
            intent.putExtra(EXTRA_REPOSITORY, repositoryTemp.name)
            itemView.context.startActivity(intent)
        }

    }

    fun clearList() {
        repositories.clear()
        notifyDataSetChanged()
        currentPage = 1
    }

    interface LoadMoreRepositoriesListener {
        fun loadMoreRepositories(currentPage: Int, sorting: String)
    }

}
