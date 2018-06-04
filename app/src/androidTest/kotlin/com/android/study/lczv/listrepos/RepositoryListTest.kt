package com.android.study.lczv.listrepos

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import br.com.concrete.desafioandroid.ui.pullRequestList.PullRequestListActivity
import com.android.study.lczv.listrepos.data.repository.ApiCaller
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListActivity
import com.android.study.lczv.listrepos.ui.repositoryList.RepositoryListAdapter
import com.google.common.io.CharStreams
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.InputStreamReader

@RunWith(JUnit4::class)
class RepositoryListTest {

    @JvmField
    @Rule
    val activityTestRule = IntentsTestRule<RepositoryListActivity>(RepositoryListActivity::class.java, true, false)

    @JvmField
    @Rule
    val mockWebServer = MockWebServer()

    @Test
    fun shouldDisplayUserListFromLocalServer() {
        val assets = InstrumentationRegistry.getContext().assets
        val body = CharStreams.toString(InputStreamReader(assets.open("json/repositories.json")))
        mockWebServer.enqueue(MockResponse().setBody(body))
        ApiCaller.addTestInterceptor(mockWebServer.url("/"))
        activityTestRule.launchActivity(Intent())
        onView(ViewMatchers.withText("android-advancedrecyclerview")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldLoadDetailsWhenClicked() {
        val assets = InstrumentationRegistry.getContext().assets
        val body = CharStreams.toString(InputStreamReader(assets.open("json/repositories.json")))
        mockWebServer.enqueue(MockResponse().setBody(body))
        ApiCaller.addTestInterceptor(mockWebServer.url("/"))
        activityTestRule.launchActivity(Intent())
        onView(withId(R.id.recyclerview_repository_list)).perform(RecyclerViewActions.actionOnItemAtPosition<RepositoryListAdapter.ViewHolder>(10, click()))
        intended(hasComponent(PullRequestListActivity::class.java.name))
    }


}
