package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.BaseTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ReminderListFragmentTest : BaseTest() {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun clickTask_navigateToSaveReminderFragment() = runBlockingTest {
        // GIVEN
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.addReminderFAB))
            .perform(click())

        // THEN
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun clickTask_navigateToLogout() = runBlockingTest {
        // GIVEN
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // WHEN
        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext);
        onView(
            withText(
                getApplicationContext<Context>().getString(
                    R.string.logout
                )
            )
        ).perform(click())

        // THEN
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(
            withText(
                getApplicationContext<Context>().getString(
                    R.string.welcome_to_the_location_reminder_app
                )
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun reminderList_DisplayedInUi() {
        //GIVEN
        val title = "title"
        val description = "description"
        val location = "location"
        val latitude = 1.0
        val longitude = 2.0

        val reminderDTO = ReminderDTO(
            title = title,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude
        )
        runBlocking {
            repository.saveReminder(reminderDTO)
        }

        // WHEN
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // THEN
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
        onView(withText(reminderDTO.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.location)).check(matches(isDisplayed()))
    }

    @Test
    fun reminderList_noData_DisplayedInUi() {
        //GIVEN
        runBlocking {
            repository.deleteAllReminders()
        }

        // WHEN
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // THEN
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}