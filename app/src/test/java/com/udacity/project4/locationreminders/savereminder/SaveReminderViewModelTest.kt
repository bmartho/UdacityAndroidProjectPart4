package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    lateinit var saveReminderViewModel: SaveReminderViewModel

    private val fakeDataSource = FakeDataSource()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        saveReminderViewModel = SaveReminderViewModel(getApplicationContext(), fakeDataSource)
    }

    @Test
    fun onClearTest() {
        // WHEN
        saveReminderViewModel.onClear()

        // THEN
        assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.selectedPOI.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            Matchers.nullValue()
        )
        assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            Matchers.nullValue()
        )
    }

    @Test
    fun validateAndSaveReminderWithoutTitleTest() {
        //GIVEN
        val invalidReminderDataItem = ReminderDataItem(
            title = null,
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0
        )

        // WHEN
        val result = saveReminderViewModel.validateAndSaveReminder(invalidReminderDataItem)

        //THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )

        assertThat(result, `is`(false))
    }

    @Test
    fun validateAndSaveReminderWithoutLocationTest() {
        //GIVEN
        val invalidReminderDataItem = ReminderDataItem(
            title = "title",
            description = "description",
            location = null,
            latitude = 0.0,
            longitude = 0.0
        )

        // WHEN
        val result = saveReminderViewModel.validateAndSaveReminder(invalidReminderDataItem)

        //THEN
        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_select_location)
        )

        assertThat(result, `is`(false))
    }

    @Test
    fun validateAndSaveValidReminderTest() {
        //GIVEN
        val validReminderDataItem = ReminderDataItem(
            title = "title",
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0
        )

        // WHEN
        val result = saveReminderViewModel.validateAndSaveReminder(validReminderDataItem)

        //THEN
        assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            `is`(getApplicationContext<Context>().getString(R.string.reminder_saved))
        )

        assertThat(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            instanceOf(NavigationCommand.Back.javaClass)
        )

        assertThat(result, `is`(true))
    }

    @Test
    fun loadingTest() {
        // GIVEN
        mainCoroutineRule.pauseDispatcher()
        val validReminderDataItem = ReminderDataItem(
            title = "title",
            description = "description",
            location = "location",
            latitude = 0.0,
            longitude = 0.0
        )

        // WHEN
        saveReminderViewModel.validateAndSaveReminder(validReminderDataItem)

        // THEN
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            `is`(true)
        )

        mainCoroutineRule.resumeDispatcher()

        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}