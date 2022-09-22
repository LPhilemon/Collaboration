package com.example.makeitso.screens.tasks

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.makeitso.*
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.model.Course
import com.example.makeitso.model.Task
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService
) : MakeItSoViewModel(logService) {
    var tasks = mutableStateMapOf<String, Task>()
        private set
    //just like editcourse view model
    var course = mutableStateOf(Course())//used to get course id for select course
        private set
    var task = mutableStateOf(Task())//this line supports our addClick in TasksScreen just for a filler value to navigate screens

    //also from editcourse when initialixing a value and passing it to this screen
    fun initialize(courseId: String) {
        //we have this id, now in conjunction with a task id, we can update an element
        //using those two values and passing them to our storage service
        viewModelScope.launch(showErrorExceptionHandler) {
            if (courseId != COURSE_DEFAULT_ID) {
                storageService.getCourse(courseId.idFromParameter(), ::onError) {
                    course.value = it
                }
            }
        }
    }


    fun addListener(courseId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {

            storageService.addListener(accountService.getUserId(), courseId.idFromParameter(),::onDocumentEvent, ::onError)

        }
    }


    fun getCourse(courseId: String){
        viewModelScope.launch(showErrorExceptionHandler) {
            if (courseId != COURSE_DEFAULT_ID) {
                storageService.getCourse(courseId.idFromParameter(), ::onError) {
                    course.value = it
                }
            }
        }
    }

    fun removeListener() {
        viewModelScope.launch(showErrorExceptionHandler) { storageService.removeListener() }
    }

    fun onTaskCheckChange(courseId: String, task: Task) {
        viewModelScope.launch(showErrorExceptionHandler) {
            val updatedTask = task.copy(completed = !task.completed)

            storageService.updateTask(courseId.idFromParameter(), updatedTask) { error ->
                if (error != null) onError(error)
            }
        }
    }

    //fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_TASK_SCREEN) //course argument needed
    //fun onAddClick(openAndPopUp: (String, String) -> Unit) = openScreen(EDIT_TASK_SCREEN)
    fun onAddTaskClick(openScreen: (String) -> Unit, courseId: String) {
        openScreen("$EDIT_TASK_SCREEN?$COURSE_ID={${courseId}}") //course argument needed
    }

    //then
    //fun onCourseClick(openScreen : (String) -> Unit) = openScreen(TASKS_SCREEN))
    //NO COURSES HERE
    fun onSettingsClick(openScreen: (String) -> Unit) = openScreen(SETTINGS_SCREEN)

    fun onTaskActionClick(openScreen: (String) -> Unit, task: Task, courseId: String, action: String) {//whole objects used
        when (TaskActionOption.getByTitle(action)) {
            TaskActionOption.EditTask -> openScreen("$EDIT_TASK_SCREEN?$COURSE_ID={${courseId}}$TASK_ID={${task.id}}")
            TaskActionOption.ToggleFlag -> onFlagTaskClick(courseId, task)
            TaskActionOption.DeleteTask -> onDeleteTaskClick(courseId, task)//added course parameter
        }
    }

    //look at course first
    private fun onFlagTaskClick(courseId: String, task: Task) {
        viewModelScope.launch(showErrorExceptionHandler) {
            val updatedTask = task.copy(flag = !task.flag)

            storageService.updateTask(courseId.idFromParameter(), updatedTask) { error ->//normally use courseId
                if (error != null) onError(error)
            }
        }
    }

    private fun onDeleteTaskClick(courseId: String, task: Task) {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.deleteTask(courseId.idFromParameter(), task.id) { error ->
                if (error != null) onError(error)
            }
        }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, task: Task) {
        if (wasDocumentDeleted) tasks.remove(task.id) else tasks[task.id] = task
    }
}