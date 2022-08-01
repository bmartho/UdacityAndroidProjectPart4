package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
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
        database.reminderDao().saveReminder(reminderDTO)
        val result = database.reminderDao().getReminderById(reminderDTO.id)

        // THEN
        assertThat(result?.title, `is`(reminderDTO.title))
        assertThat(result?.description, `is`(reminderDTO.description))
        assertThat(result?.location, `is`(reminderDTO.location))
        assertThat(result?.latitude, `is`(reminderDTO.latitude))
        assertThat(result?.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun saveTwoRemindersAndCheckListSize() = runBlockingTest {
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

        val reminderDTO2 = ReminderDTO(
            title = title,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude
        )

        // WHEN
        database.reminderDao().saveReminder(reminderDTO)
        database.reminderDao().saveReminder(reminderDTO2)
        val result = database.reminderDao().getReminders()

        // THEN
        assertThat(result.size, `is`(2))
    }

    @Test
    fun saveTwoRemindersDeleteThenAndCheckListSize() = runBlockingTest {
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

        val reminderDTO2 = ReminderDTO(
            title = title,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude
        )

        // WHEN
        database.reminderDao().saveReminder(reminderDTO)
        database.reminderDao().saveReminder(reminderDTO2)
        database.reminderDao().deleteAllReminders()
        val result = database.reminderDao().getReminders()

        // THEN
        assertThat(result.size, `is`(0))
    }
}