package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.util.BaseTest
import com.udacity.project4.util.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SaveReminderFragmentTest : BaseTest() {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun navigateToSelectLocation() {
        // GIVEN
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.selectLocation))
            .perform(click())

        // THEN
        Mockito.verify(navController).navigate(
            SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
        )
    }

    @Test
    fun saveReminderWithNoTitle() {
        // GIVEN
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        // WHEN
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun saveReminderWithNoLocation() {
        // GIVEN
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        // WHEN
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )
    }

    @Test
    fun saveReminderWithSuccess() {
        // GIVEN
        saveReminderViewModel.reminderSelectedLocationStr.value = "location"
        saveReminderViewModel.latitude.value = 1.0
        saveReminderViewModel.longitude.value = 1.0

        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.reminderTitle)).perform(typeText("Title"))
        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))
        closeSoftKeyboard()
        onView(withId(R.id.saveReminder))
            .perform(click())

        // THEN
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.reminder_saved)
            )
        )
        assertThat(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            Matchers.instanceOf(NavigationCommand.Back.javaClass)
        )
    }
}