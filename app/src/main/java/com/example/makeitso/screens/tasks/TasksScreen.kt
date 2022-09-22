package com.example.makeitso.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.common.composable.ActionToolbar
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.common.ext.smallSpacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.screens.courses.CoursesViewModel
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText


@Composable
@ExperimentalMaterialApi
fun TasksScreen(

    openScreen: (String) -> Unit, //changed back
    courseId: String,
    //openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = hiltViewModel()

) {
    //val tasky by viewModel.task
    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    viewModel.onAddTaskClick(openScreen, courseId)
                          },//originally openScreen UPDATE this stays openscreen.....only moving to the task screen requires a popup, so it's coursesviewmodel to have one
                //NOTE: SEEMS THAT THIS ONCLICK NEEDS THE courseId within it for us to add a new task.....
                //same way we give courseId to TaskScreen before screen loads or onTaskClick to edit a task
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                modifier = modifier.padding(16.dp)
            ) { Icon(Icons.Filled.Add, "Add") }
        }

    ) {
        val viewModelled : CoursesViewModel = hiltViewModel()
        val course by viewModelled.course

        LaunchedEffect(Unit) {                        //THIS FUNCTION IS IMPORTANT IN SAVING EDITED DATA
            viewModelled.initialize(courseId)
        }
        val tasks = viewModel.tasks

        Column(

            modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()

        ) {

            ActionToolbar(
                title = AppText.tasks,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_settings,
                endAction = { viewModel.onSettingsClick(openScreen) } //change openAndPopUp
            )

            Spacer(modifier = Modifier.smallSpacer())

            LazyColumn {


                items(
                    tasks
                        .values
                        .toList(),
                    key = { it.id }) {
                        taskItem ->
                    TaskItem(
                        task = taskItem,
                        onCheckChange = { viewModel.onTaskCheckChange(courseId, taskItem) },
                        onActionClick = { action ->
                            viewModel.onTaskActionClick(openScreen, taskItem, courseId, action) //change openAndPopUp
                        }
                    )
                }


            }
        }
    }

    DisposableEffect(viewModel) {
//so if viewmodel can get the storage service, then so can we. In order to access data
        //but viewmodel is the middleman to provide data to us
        //so how do we get viewmodel to give us what we want
        //we want to index a courseid in the addlistener function that indexes a stored item
   // viewModel.initialize(courseId)
        //viewModel.getCourse(courseId)
        //viewModel.addListener(course.idFromParameter())
        viewModel.addListener(courseId)
        onDispose {
            viewModel.removeListener()
        }
    }
}

