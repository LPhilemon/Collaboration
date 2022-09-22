package com.example.makeitso.screens.edit_course

import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.makeitso.COURSE_DEFAULT_ID
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.model.Course
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditCourseViewModel @Inject constructor(
        logService:LogService,
        private val storageService:StorageService,
        private val accountService:AccountService
) : MakeItSoViewModel(logService) {
        var course = mutableStateOf(Course())
        private set

        fun initialize(courseId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
        if (courseId != COURSE_DEFAULT_ID) {
        storageService.getCourse(courseId.idFromParameter(), ::onError) {
        course.value = it
        }
        }
        }
        }

        fun onTitleChange(newValue: String) {
        course.value = course.value.copy(title = newValue)
        }

        fun onDescriptionChange(newValue: String) {
        course.value = course.value.copy(description = newValue)
        }

        fun onUrlChange(newValue: String) {
        course.value = course.value.copy(url = newValue)
        }

        fun onDateChange(newValue: Long) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
        calendar.timeInMillis = newValue
        val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
        course.value = course.value.copy(dueDate = newDueDate)
        }

        fun onTimeChange(hour: Int, minute: Int) {
        val newDueTime = "${hour.toClockPattern()}:${minute.toClockPattern()}"
        course.value = course.value.copy(dueTime = newDueTime)
        }

        fun onFlagToggle(newValue: String)  {
        val newFlagOption = EditFlagOption.getBooleanValue(newValue)
        course.value = course.value.copy(flag = newFlagOption)
        }

        fun onPriorityChange(newValue: String) {
        course.value = course.value.copy(priority = newValue)
        }

        fun onDoneClick(popUpScreen: () -> Unit) {
        viewModelScope.launch(showErrorExceptionHandler) {
        val editedCourse = course.value.copy(userId = accountService.getUserId())

        if (editedCourse.id.isBlank())
                saveCourse(editedCourse, popUpScreen)
        else updateCourse(editedCourse, popUpScreen)
        }
        }

private fun saveCourse(course: Course, popUpScreen: () -> Unit) {
        storageService.saveCourse(course) { error ->
        if (error == null) popUpScreen() else onError(error)
        }
        }

private fun updateCourse(course: Course, popUpScreen: () -> Unit) {
        storageService.updateCourse(course ) { error ->
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
