package com.android.study.lczv.listrepos.data.repository

import com.android.study.lczv.listrepos.data.model.ApiResponse
import com.android.study.lczv.listrepos.data.model.PullRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubAPI {

    @GET("/search/repositories?q=language:Java")
    fun loadRepositories(@Query("page") page: Int = 1, @Query("sort") sorting: String): Call<ApiResponse>

    @GET("/repos/{owner}/{repo}/pulls")
    fun loadPullRequests(@Path("owner") owner: String, @Path("repo") repo: String, @Query(value = "state") state: String, @Query("page") page: Int): Call<List<PullRequest>>

    @GET("https://api.github.com/users/{user}")
    fun loadUser(@Path("user") user: String)
}
