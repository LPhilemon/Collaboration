package com.example.makeitso.screens.edit_course

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.common.composable.*
import com.example.makeitso.common.ext.card
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.spacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.Course
import com.example.makeitso.model.Priority
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText

@Composable
@ExperimentalMaterialApi
fun EditCourseScreen(
    popUpScreen: () -> Unit,
    courseId: String,
    modifier: Modifier = Modifier,
    viewModel: EditCourseViewModel = hiltViewModel()
) {
    val course by viewModel.course

    LaunchedEffect(Unit) {
        viewModel.initialize(courseId)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.edit_course,
            modifier = Modifier.toolbarActions(),
            endActionIcon = AppIcon.ic_check,
            endAction = { viewModel.onDoneClick(popUpScreen) }
        )

        Spacer(modifier = Modifier.spacer())

        val fieldModifier = Modifier.fieldModifier()
        BasicField(AppText.title, course.title, viewModel::onTitleChange, fieldModifier)
        BasicField(AppText.description, course.description, viewModel::onDescriptionChange, fieldModifier)
        //BasicField(AppText.url, course.url, viewModel::onUrlChange, fieldModifier)

        Spacer(modifier = Modifier.spacer())
        //CardEditors(course, viewModel::onDateChange, viewModel::onTimeChange)
        CardSelectors(
            course,
            viewModel::onPriorityChange,
            viewModel::onFlagToggle)

        Spacer(modifier = Modifier.spacer())
    }
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(course: Course, onDateChange: (Long) -> Unit, onTimeChange: (Int, Int) -> Unit) {
    val activity = LocalContext.current as AppCompatActivity

    RegularCardEditor(AppText.date, AppIcon.ic_calendar, course.dueDate, Modifier.card()) {
        showDatePicker(activity, onDateChange)
    }

    RegularCardEditor(AppText.time, AppIcon.ic_clock, course.dueTime, Modifier.card()) {
        showTimePicker(activity, onTimeChange)
    }
}

@Composable
@ExperimentalMaterialApi
private fun CardSelectors(
    course: Course,
    onPriorityChange: (String) -> Unit,
    onFlagToggle: (String) -> Unit
) {
    val prioritySelection = Priority.getByName(course.priority).name
    CardSelector(AppText.priority, Priority.getOptions(), prioritySelection, Modifier.card()) { newValue ->
        onPriorityChange(newValue)
    }

    val flagSelection = EditFlagOption.getByCheckedState(course.flag).name
    CardSelector(AppText.flag, EditFlagOption.getOptions(), flagSelection, Modifier.card()) { newValue ->
        onFlagToggle(newValue)
    }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker().build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { timeInMillis ->
            onDateChange(timeInMillis)
        }
    }
}

private fun showTimePicker(activity: AppCompatActivity?, onTimeChange: (Int, Int) -> Unit) {
    val picker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            onTimeChange(picker.hour, picker.minute)
        }
    }
}