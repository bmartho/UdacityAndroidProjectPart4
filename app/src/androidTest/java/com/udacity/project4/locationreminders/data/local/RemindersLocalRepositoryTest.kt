package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun createRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(), Dispatchers.Main
        )
    }

    @Test
    fun saveAndGetRemindersWithSuccess() = runBlockingTest {
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

        // WHEN
        remindersLocalRepository.saveReminder(reminderDTO)
        val result = remindersLocalRepository.getReminders() as Result.Success

        // THEN
        assertThat(result, `is`(Result.Success(listOf(reminderDTO))))
    }

    @Test
    fun saveAndGetReminderByIdWithSuccess() = runBlockingTest {
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

        // WHEN
        remindersLocalRepository.saveReminder(reminderDTO)
        val result = remindersLocalRepository.getReminder(reminderDTO.id) as Result.Success

        // THEN
        assertThat(result, `is`(Result.Success(reminderDTO)))
    }

    @Test
    fun saveAndGetReminderByIdWithError() = runBlockingTest {
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

        // WHEN
        remindersLocalRepository.saveReminder(reminderDTO)
        val result = remindersLocalRepository.getReminder("123") as Result.Error

        // THEN
        assertThat(result, `is`(Result.Error("Reminder not found!")))
    }

    @Test
    fun deleteAllRemindersTest() = runBlockingTest {
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

        // WHEN
        remindersLocalRepository.saveReminder(reminderDTO)
        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminders() as Result.Success

        // THEN
        assertThat(result.data.size, `is`(0))
    }
}