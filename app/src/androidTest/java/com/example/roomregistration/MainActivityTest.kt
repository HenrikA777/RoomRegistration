package com.example.roomregistration

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule @JvmField
    var activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    lateinit var mIdlingResource: CountingIdlingResource;

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mIdlingResource = activityRule.getActivity().idlingResourceInTest;
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    fun login() {
        if (activityRule.activity.mAuth.currentUser != null) {

            onView(withId(R.id.btnSignOut)).perform(closeSoftKeyboard()).perform(click())
            check(activityRule.activity.mAuth.currentUser == null)
        }
        onView(withId(R.id.etEmailAddr)).perform(typeText("test@test.dk")).check(matches(withText("test@test.dk")));
        onView(withId(R.id.etPassword)).perform(typeText("123456"));
        onView(withId(R.id.btnSignIn)).perform(closeSoftKeyboard()).perform(click())
        check(activityRule.activity.mAuth.currentUser != null)

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}