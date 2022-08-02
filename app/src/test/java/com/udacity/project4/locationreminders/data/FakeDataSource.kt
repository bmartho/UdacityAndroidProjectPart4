package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private val remindersList = mutableListOf<ReminderDTO>()
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error(Exception("Test exception").localizedMessage)
        } else {
            Result.Success(remindersList)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception").localizedMessage)
        }

        val reminder = remindersList.firstOrNull { it.id == id }
        return if (reminder != null)
            Result.Success(reminder)
        else
            Result.Error("error")
    }

    override suspend fun deleteAllReminders() {
        remindersList.clear()
    }
}