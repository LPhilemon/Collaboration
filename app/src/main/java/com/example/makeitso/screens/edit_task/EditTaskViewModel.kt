/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.edit_task

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.makeitso.TASK_DEFAULT_ID
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.model.Course
import com.example.makeitso.model.Task
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : MakeItSoViewModel(logService) {
    var task = mutableStateOf(Task()) //private set
    var course = mutableStateOf(Course())
        private set

    fun initialize(courseId: String, taskId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            if (taskId != TASK_DEFAULT_ID) {
                storageService.getTask(courseId.idFromParameter(), taskId.idFromParameter(), ::onError) { //getting task
                    task.value = it //what if we just use course.task.value? We would need and argument for which course exactly.
                }
                storageService.getCourse(courseId.idFromParameter(), ::onError){
                    course.value = it
                }
            }
        }
    }

    fun onTitleChange(newValue: String) {
        task.value = task.value.copy(title = newValue)
    }

    fun onDescriptionChange(newValue: String) {
        task.value = task.value.copy(description = newValue)
    }

    fun onUrlChange(newValue: String) {
        task.value = task.value.copy(url = newValue)
    }

    fun onDateChange(newValue: Long) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
        calendar.timeInMillis = newValue
        val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
        task.value = task.value.copy(dueDate = newDueDate)
    }

    fun onTimeChange(hour: Int, minute: Int) {
        val newDueTime = "${hour.toClockPattern()}:${minute.toClockPattern()}"
        task.value = task.value.copy(dueTime = newDueTime)
    }

    fun onFlagToggle(newValue: String)  {
        val newFlagOption = EditFlagOption.getBooleanValue(newValue)
        task.value = task.value.copy(flag = newFlagOption)
    }

    fun onPriorityChange(newValue: String) {
        task.value = task.value.copy(priority = newValue)
    }

    fun onDoneClick(popUpScreen: () -> Unit, courseId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            //val courseUnderWhichClassIs = course.value.copy(userId = accountService.getUserId())
            val editedTask = task.value.copy(userId = accountService.getUserId())

//            if (editedTask.id.isBlank())
//                saveTask(courseUnderWhichClassIs.id, editedTask, popUpScreen)
//            else updateTask(courseUnderWhichClassIs.id, editedTask, popUpScreen)
            if (editedTask.id.isBlank())
                saveTask(courseId.idFromParameter(), editedTask, popUpScreen)
            else updateTask(courseId.idFromParameter(), editedTask, popUpScreen)
        }
    }

    private fun saveTask(courseId: String, task: Task, popUpScreen: () -> Unit) {
        storageService.saveTask(courseId.idFromParameter(), task) { error ->//hustling to use course.id....if course is passed, even saveTask in storage service interface and implementation changes
            if (error == null) popUpScreen() else onError(error)
        }
    }

    private fun updateTask(courseId: String, task: Task, popUpScreen: () -> Unit) {
        storageService.updateTask(courseId.idFromParameter(), task) { error ->
            if (error == null) popUpScreen() else onError(error)
        }
    }

    private fun Int.toClockPattern(): String {
        return if (this < 10) "0$this" else "$this"
    }

    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}
