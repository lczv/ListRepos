package com.android.study.lczv.listrepos

import com.android.study.lczv.listrepos.data.ApiRequestStatus
import com.android.study.lczv.listrepos.data.model.PullRequest
import com.android.study.lczv.listrepos.data.repository.GitHubAPI
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListActivity
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListContract
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListPresenter
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class PullRequestListPresenterTest {

    val view: PullRequestListActivity = mock()
    val api: GitHubAPI = mock()
    val apiResponseCall: Call<List<PullRequest>> = mock()

    @Captor
    var captor: ArgumentCaptor<Callback<List<PullRequest>>>? = null

    lateinit var presenter: PullRequestListContract.Presenter

    @Before
    fun setUp() {
        presenter = PullRequestListPresenter(api)
        presenter.onAttach(view)
    }

    @Test
    fun Should_DisplayPullRequestsList_When_LoadPullRequestsFromAPI() {

        val apiResponse = listOf<PullRequest>()

        whenever(api.loadPullRequests("", "", "", 1)).thenReturn(apiResponseCall)

        val response = Response.success(apiResponse)

        presenter.loadPullRequests("", "", "", 1)

        verify(apiResponseCall).enqueue(captor?.capture())

        captor?.value?.onResponse(apiResponseCall, response)

        verify(view).displayPullRequests(anyList())
    }


    @Test
    fun Should_DisplayErrorMessage_When_NoPullRequestCouldBeLoaded() {

        whenever(api.loadPullRequests("", "", "", 1)).thenReturn(apiResponseCall)

        presenter.loadPullRequests("", "", "", 1)

        verify(apiResponseCall).enqueue(captor?.capture())

        captor?.value?.onFailure(apiResponseCall, Throwable())

        verify(view).displayError(anyString(), anyString(), eq(ApiRequestStatus.ERROR))

    }

    @Test
    fun Should_DisplayApiLimitErrorMessage_When_ApiLimitExceeded() {

        whenever(api.loadPullRequests("", "", "", 1)).thenReturn(apiResponseCall)

        val response = Response.error<List<PullRequest>>(403, ResponseBody.create(
                MediaType.parse(""), ""
        ))

        presenter.loadPullRequests("", "", "", 1)

        verify(apiResponseCall).enqueue(captor?.capture())

        captor?.value?.onResponse(apiResponseCall, response)

        verify(view).displayError(anyString(), anyString(), eq(ApiRequestStatus.API_LIMIT_EXCEEDED))

    }

}