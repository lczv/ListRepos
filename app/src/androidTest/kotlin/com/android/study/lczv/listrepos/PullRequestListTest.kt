package com.android.study.lczv.listrepos

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import com.android.study.lczv.listrepos.ui.pullRequestList.PullRequestListActivity
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListAdapter
import com.google.common.io.CharStreams
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStreamReader

@RunWith(AndroidJUnit4::class)
class PullRequestListTest {

    @JvmField
    @Rule
    val intentTestRule = IntentsTestRule<PullRequestListActivity>(PullRequestListActivity::class.java, false, false)

    @Test
    fun shouldDisplayPullRequestListFromLocalServer() {

        val mockWebServer = MockWebServer()

        val assets = InstrumentationRegistry.getContext().assets
        val body = CharStreams.toString(InputStreamReader(assets.open("json/pullRequests.json")))

        mockWebServer.enqueue(MockResponse().setBody(body))
        ApiCaller.addTestInterceptor(mockWebServer.url("/"))

        val intent = Intent()
        intent.putExtra(RepositoryListAdapter.EXTRA_OWNER, "")
        intent.putExtra(RepositoryListAdapter.EXTRA_REPOSITORY, "")

        intentTestRule.launchActivity(intent)
        onView(withText("[SPORTS-12018] Fix issue where recyclerview animations crash with support library 26")).check(matches(isDisplayed()))

        mockWebServer.shutdown()
    }

    @Test
    fun shouldDisplayMessageWhenNoPullRequestsExist() {

        val mockWebServer = MockWebServer()

        val assets = InstrumentationRegistry.getContext().assets
        val body = CharStreams.toString(InputStreamReader(assets.open("json/pullRequests.json")))
        mockWebServer.enqueue(MockResponse().setBody("{[]}"))
        ApiCaller.addTestInterceptor(mockWebServer.url("/"))


        val intent = Intent()
        intent.putExtra(RepositoryListAdapter.EXTRA_OWNER, "")
        intent.putExtra(RepositoryListAdapter.EXTRA_REPOSITORY, "")

        intentTestRule.launchActivity(intent)
        onView(withText("No pull requests found")).check(matches(isDisplayed()))

        mockWebServer.shutdown()
    }

}
