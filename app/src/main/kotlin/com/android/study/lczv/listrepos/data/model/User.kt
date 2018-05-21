package com.android.study.lczv.listrepos.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    @Expose
    private val name: String? = null
)
