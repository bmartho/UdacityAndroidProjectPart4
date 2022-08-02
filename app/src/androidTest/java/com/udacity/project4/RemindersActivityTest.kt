package com.udacity.project4

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.BaseTest
import com.udacity.project4.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest : BaseTest() {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun reminderActivityWithOneReminder() {
        // GIVEN
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
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        // THEN
        onView(withText(reminderDTO.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.location)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun reminderActivityWithTwoReminder() {
        // GIVEN
        val reminderDTO = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 2.0,
            longitude = 2.0
        )

        val reminderDTO2 = ReminderDTO(
            title = "title 2",
            description = "description 2",
            location = "location 2",
            latitude = 1.0,
            longitude = 1.0
        )

        runBlocking {
            repository.saveReminder(reminderDTO)
            repository.saveReminder(reminderDTO2)
        }

        // WHEN
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        // THEN
        onView(withText(reminderDTO.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO.location)).check(matches(isDisplayed()))

        onView(withText(reminderDTO2.title)).check(matches(isDisplayed()))
        onView(withText(reminderDTO2.description)).check(matches(isDisplayed()))
        onView(withText(reminderDTO2.location)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun reminderActivitySaveReminder() {
        // GIVEN
        val location = "location"
        val title = "Title"
        val description = "Description"

        saveReminderViewModel.reminderSelectedLocationStr.value = location
        saveReminderViewModel.latitude.value = 1.0
        saveReminderViewModel.longitude.value = 1.0

        // WHEN
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB))
            .perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText(title))
        closeSoftKeyboard()
        onView(withId(R.id.reminderDescription)).perform(typeText(description))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(description)).check(matches(isDisplayed()))
        onView(withText(location)).check(matches(isDisplayed()))
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.reminder_saved)
            )
        )
        assertThat(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            instanceOf(NavigationCommand.Back.javaClass)
        )

        activityScenario.close()
    }

    @Test
    fun reminderActivitySaveReminderWithNoTitle() {
        // GIVEN
        val location = "location"

        saveReminderViewModel.reminderSelectedLocationStr.value = location
        saveReminderViewModel.latitude.value = 1.0
        saveReminderViewModel.longitude.value = 1.0

        // WHEN
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB))
            .perform(click())
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )

        onView(
            allOf(
                withId(R.id.snackbar_text), withText(
                    ApplicationProvider.getApplicationContext<Context>()
                        .getString(R.string.err_enter_title)
                )
            )
        ).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun reminderActivitySaveReminderWithNoLocation() {
        // GIVEN
        val title = "Title"

        // WHEN
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB))
            .perform(click())
        onView(withId(R.id.reminderTitle)).perform(typeText(title))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )
        onView(
            allOf(
                withId(R.id.snackbar_text), withText(
                    ApplicationProvider.getApplicationContext<Context>()
                        .getString(R.string.err_select_location)
                )
            )
        ).check(matches(isDisplayed()))

        activityScenario.close()
    }
}