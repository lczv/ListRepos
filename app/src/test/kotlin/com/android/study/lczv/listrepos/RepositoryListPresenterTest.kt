package com.android.study.lczv.listrepos

import com.android.study.lczv.listrepos.data.model.ApiResponse
import com.android.study.lczv.listrepos.data.repository.GitHubAPI
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListActivity
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListContract
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListPresenter
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Captor
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class RepositoryListPresenterTest {

    val view: RepositoryListActivity = mock()
    val api: GitHubAPI = mock()
    val apiResponseCall: Call<ApiResponse> = mock()

    @Captor
    var captor: ArgumentCaptor<Callback<ApiResponse>>? = null

    lateinit var presenter: RepositoryListContract.Presenter

    @Before
    fun setUp() {
        presenter = RepositoryListPresenter(api)
        presenter.onAttach(view)
    }

    //
    @Test
    fun Should_DisplayRepositoriesList_When_LoadRepositoriesFromAPI() {

        val apiResponse = ApiResponse(30, false, mutableListOf())

        whenever(api.loadRepositories(0, "asc")).thenReturn(apiResponseCall)

        val response = Response.success(apiResponse)

        presenter.loadRepositories(0, "asc")

        verify(apiResponseCall).enqueue(captor?.capture())

        captor?.value?.onResponse(apiResponseCall, response)

        verify(view).displayRepositories(anyList())

    }

    @Test
    fun Should_DisplayErrorMessage_When_NoRepositoryCouldBeLoaded() {

        whenever(api.loadRepositories(0, "asc")).thenReturn(apiResponseCall)

        val failure = Throwable()

        presenter.loadRepositories(0, "asc")

        verify(apiResponseCall).enqueue(captor?.capture())

        captor?.value?.onFailure(apiResponseCall, failure)

        verify(view).displayError()

    }

}