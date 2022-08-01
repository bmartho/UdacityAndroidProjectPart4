package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    lateinit var remindersListViewModel: RemindersListViewModel

    private val fakeDataSource = FakeDataSource()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        remindersListViewModel = RemindersListViewModel(getApplicationContext(), fakeDataSource)
    }

    @Test
    fun getErrorResultFromLoadReminders() = runBlockingTest {
        // GIVEN
        fakeDataSource.deleteAllReminders()

        // WHEN
        remindersListViewModel.loadReminders()

        // THEN
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("error")
        )

        assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            `is`(true)
        )
    }

    @Test
    fun getSuccessResultFromLoadReminders() = runBlockingTest {
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

        fakeDataSource.saveReminder(reminderDTO)

        // WHEN
        remindersListViewModel.loadReminders()

        // THEN
        assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue()[0].title,
            `is`(title)
        )
        assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue()[0].description,
            `is`(description)
        )
        assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue()[0].location,
            `is`(location)
        )
        assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            `is`(false)
        )
    }

    @Test
    fun loadingTest() {
        // GIVEN
        mainCoroutineRule.pauseDispatcher()

        // WHEN
        remindersListViewModel.loadReminders()

        // THEN
        assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            `is`(true)
        )

        mainCoroutineRule.resumeDispatcher()

        assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            `is`(false)
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}