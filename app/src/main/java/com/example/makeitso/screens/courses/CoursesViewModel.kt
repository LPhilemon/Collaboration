package com.example.makeitso.screens.courses

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.makeitso.*
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.model.Course
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : MakeItSoViewModel(logService) {
    var courses = mutableStateMapOf<String, Course>()
        private set
    //just like editcourse view model
    var course = mutableStateOf(Course())//used to get course id for select course
        private set

    //also from editcourse when initializing a value and passing it to this screen
    fun initialize(courseId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            if (courseId != COURSE_DEFAULT_ID) {
                storageService.getCourse(courseId.idFromParameter(), ::onError) {
                    course.value = it
                }
            }
        }
    }
    //COPY THE edittask view model
    //copy oncourseaction click in this file
    //start small probably. Just moving to another screen(without arguments) shouuld be enough for now.
    //onSelectCourse needs a course argument from the selected course.
    //fun onSelectCourse(openScreen: (String) -> Unit, course: Course) = //supposed to take us to task list of selected course
     //   openScreen("$TASKS_SCREEN?$COURSE_ID={${course.id}}") - NOTE THIS FUNCTION CAN BE USED FOR AN ONCLICK METHOD FOR THE CARD ITSELF INSTEAD OF A COURSE ACTION OPTION
    //unlike onCourseClick, here we need just one option hence the action argument is not really required.
    //   fun onCourseClick(openScreen: (String) -> Unit = openScreen(TASKS_SCREEN))

    fun addCourseListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.addCourseListener(accountService.getUserId(), ::onDocumentEvent, ::onError)
//            getUserId() - particular user of task being checked for changes in disposab;e effect on course screen #CourseScreen.kt
            //::onDocumentEvent - the course or doc being changed
        }
    }

    fun removeCourseListener() {
        viewModelScope.launch(showErrorExceptionHandler) { storageService.removeCourseListener() }
    }

    fun onCourseCheckChange(course: Course) {
        viewModelScope.launch(showErrorExceptionHandler) {
            val updatedCourse = course.copy(completed = !course.completed)

            storageService.updateCourse(updatedCourse) { error ->
                if (error != null) onError(error)
            }
        }
    }
    //THESE open screen paths are found in viewmodels of a screen that leads to another screen....all these variable paths have to be defined in the main app file(MakeItSoApp of Collaborationg app in this case)
    fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_COURSE_SCREEN)

    fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)


    fun onCourseActionClick(openScreen: (String) -> Unit, course: Course, action: String) {
        when (CoursesActionOption.getByTitle(action)) {
//            CoursesActionOption.OpenCoursesScreen -> openScreen("$TASKS_SCREEN")
            CoursesActionOption.OpenTasksScreen -> openScreen("$TASKS_SCREEN?$COURSE_ID={${course.id}}") //viewModel.onSelectCourse(openScreen, courseItem)
           CoursesActionOption.EditCourse -> openScreen("$EDIT_COURSE_SCREEN?$COURSE_ID={${course.id}}") //$COURSE_ID={${course.id}} is defined as COURSE_ID_ARG in MakeItSoRoutes....this can be seen in MakeItSoApp.kt, we have used that instead of the full thing. We use this to cater for EDIT_COURSE_SCREEN which is used in creating a new task that has it's original id as -1
//            navigating to tasks screen using a given course id    IMPORTANT
            CoursesActionOption.ToggleFlag -> onFlagCourseClick(course)
            CoursesActionOption.DeleteCourse -> onDeleteCourseClick(course)
            //we could have used one option if we had only one button i.e no need for action argument to tell us which
            //option is being used. In otherwards we have open screen and the course to give course id----applied to selectcourse
            //a function used for one option with an argument
        }
    }

    private fun onFlagCourseClick(course: Course) {
        viewModelScope.launch(showErrorExceptionHandler) {
            val updatedCourse = course.copy(flag = !course.flag)

            storageService.updateCourse(updatedCourse) { error ->
                if (error != null) onError(error)
            }
        }
    }

    private fun onDeleteCourseClick(course: Course) {//doesn't delete tasks in nosql databasee under it
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.deleteCourse(course.id) { error ->
                if (error != null) onError(error)
            }
        }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, course: Course) {
        if (wasDocumentDeleted) courses.remove(course.id)
        else courses[course.id] = course
    }
}