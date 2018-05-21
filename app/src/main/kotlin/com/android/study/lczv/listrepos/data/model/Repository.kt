package com.android.study.lczv.listrepos.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Repository(

        @SerializedName("id")
        @Expose
        val id: Int = 0,
        @SerializedName("name")
        @Expose
        val name: String? = null,
        @SerializedName("full_name")
        @Expose
        val fullName: String? = null,
        @SerializedName("owner")
        @Expose
        val owner: Owner? = null,
        @SerializedName("private")
        @Expose
        val _private: Boolean = false,
        @SerializedName("html_url")
        @Expose
        val htmlUrl: String? = null,
        @SerializedName("description")
        @Expose
        val description: String? = null,
        @SerializedName("stargazers_count")
        @Expose
        val stargazersCount: Int = 0,
        @SerializedName("forks_count")
        @Expose
        val forksCount: Int = 0

)