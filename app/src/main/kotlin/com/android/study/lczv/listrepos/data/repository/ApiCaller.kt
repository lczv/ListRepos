package com.android.study.lczv.listrepos.data.repository

import android.support.annotation.VisibleForTesting
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiCaller {

    var api: GitHubAPI

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(GitHubAPI::class.java)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun addTestInterceptor(serverUrl: HttpUrl) {
        val client = OkHttpClient.Builder()
                .addInterceptor {
                    val request = it.request().newBuilder()
                            .url(serverUrl)
                            .build()
                    it.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(GitHubAPI::class.java)
    }
}
