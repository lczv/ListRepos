package com.android.study.lczv.listrepos.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ApiResponse(

    @SerializedName("total_count")
    @Expose
    val totalCount: Int = 0,
    @SerializedName("incomplete_results")
    @Expose
    val isIncompleteResults: Boolean = false,
    @SerializedName("items")
    @Expose
    val items: List<Repository>? = null

)