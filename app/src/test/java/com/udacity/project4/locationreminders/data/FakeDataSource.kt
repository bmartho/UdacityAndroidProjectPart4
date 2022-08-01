package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private val remindersList = mutableListOf<ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (remindersList.isEmpty()) {
            Result.Error("error")
        } else {
            Result.Success(remindersList)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
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