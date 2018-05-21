package com.android.study.lczv.listrepos.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PullRequest(

    @SerializedName("html_url")
    @Expose
    var htmlUrl: String? = null,
    @SerializedName("state")
    @Expose
    var state: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("user")
    @Expose
    var user: Owner? = null,
    @SerializedName("body")
    @Expose
    var body: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null,
    @SerializedName("closed_at")
    @Expose
    var closedAt: Any? = null,
    @SerializedName("merged_at")
    @Expose
    var mergedAt: Any? = null,
    @SerializedName("merge_commit_sha")
    @Expose
    var mergeCommitSha: String? = null

)
