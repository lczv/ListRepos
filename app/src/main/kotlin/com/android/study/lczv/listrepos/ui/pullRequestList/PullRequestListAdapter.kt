package com.android.study.lczv.listrepos.ui.pullRequestList

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.study.lczv.listrepos.R
import com.android.study.lczv.listrepos.data.model.PullRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pull_request_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class PullRequestListAdapter(val listener: LoadMorePullRequestsListener) : RecyclerView.Adapter<PullRequestListAdapter.ViewHolder>() {

    val pullRequests = mutableListOf<PullRequest>()
    var currentPage = 1
    private val itemsPerPage = 30
    lateinit var owner: String
    lateinit var repository: String
    lateinit var state: String
    var listEnded = false

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        lateinit var view: View

        if (viewType == 0) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.pull_request_list_item, parent, false)
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.repository_list_item_load, parent, false)
        } else if (viewType == 3) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.pull_request_last_item, parent, false)
        } else {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.pull_request_no_item, parent, false)
        }
        return ViewHolder(view)

    }

    override fun getItemCount() = pullRequests.size + 1

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        if (position == (itemsPerPage * currentPage)) {
            currentPage++
            listener.loadMorePullRequests(owner, repository, state, currentPage)
        }

        if (getItemViewType(position) == 0) {
            holder?.bind(pullRequests[position])
        }
    }

    override fun getItemViewType(position: Int): Int {

        // Layout to be inflated when there are no more pull requests
        if (listEnded && position == pullRequests.size) {
            return 3
        }
        // Default layout to inflate when the list is empty
        else if (pullRequests.isEmpty()) {
            return 2
        }
        // Layout to be inflated after the last item of the list
        else if (pullRequests.size > 0 && position == pullRequests.size) {
            return 1
        }
        // Default repository item layout
        return 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        lateinit var pullRequest: PullRequest

        fun bind(pullRequest: PullRequest) {

            this.pullRequest = pullRequest

            itemView.textview_pull_request_title.text = pullRequest.title ?: ""
            itemView.textview_pull_request_description.text = pullRequest.body ?: ""
            itemView.textview_user_name.text = pullRequest.user?.login ?: ""

            itemView.textview_pull_request_opened_date.text = itemView.context.getString(R.string.pull_requests_opened_date, formatDate(pullRequest.createdAt))
            itemView.textview_pull_request_closed_date.text = itemView.context.getString(R.string.pull_requests_closed_date, formatDate((pullRequest.closedAt
                    ?: "") as String))

            Picasso.with(itemView.context).load(pullRequest.user?.avatarUrl).into(itemView.imageview_user_avatar)

            if (pullRequest.closedAt == null) {
                itemView.imageview_pull_request_status.setImageResource(R.drawable.ic_issue_opened)
            } else {
                itemView.imageview_pull_request_status.setImageResource(R.drawable.ic_issue_closed)
            }

            itemView.setOnClickListener(this)
        }


        fun formatDate(originalDate: String?): String {

            var dateFormat: SimpleDateFormat
            val formattedDate: Date
            val newDate: String

            try {
                dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                formattedDate = dateFormat.parse(originalDate);
                newDate = SimpleDateFormat("dd-MM-yyyy").format(formattedDate)
            } catch (exception: Exception) {
                return " - "
            }



            if (formattedDate.toString().isNullOrEmpty()) {
                return " - "
            }

            return newDate.toString()

        }

        override fun onClick(v: View?) {

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pullRequest.htmlUrl))
            itemView.context.startActivity(intent)

        }
    }

    interface LoadMorePullRequestsListener {
        fun loadMorePullRequests(owner: String, repository: String, state: String, lastPage: Int)
    }
}