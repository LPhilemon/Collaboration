package com.example.makeitso.screens.courses

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
import com.example.makeitso.common.ext.smallSpacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.R.string as AppText


@Composable
@ExperimentalMaterialApi
fun CourseScreen(

    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel = hiltViewModel()

) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick(openScreen) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                modifier = modifier.padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add")
            }
        }
    ) {
        val courses = viewModel.courses

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            ActionToolbar(
                title = AppText.courses,
                modifier = Modifier.toolbarActions(),
                endActionIcon = AppIcon.ic_settings,
                endAction = { viewModel.onSettingsClick(openScreen) }
            )

            Spacer(modifier = Modifier.smallSpacer())

            LazyColumn {
                items(
                    courses
                        .values
                        .toList(), key = { it.id }) {
                        courseItem ->
                   CourseItem(
                        course = courseItem,
                        onCheckChange = { viewModel.onCourseCheckChange(courseItem) },
                        onActionClick = {//how does CourseItem deal with the onActionCLick --- check its class deftn
                                        //1. using a click
                                action -> viewModel.onCourseActionClick(
                            openScreen  //2. show in navigation graph
                            , courseItem
                            , action)
                          // viewModel.onSelectCourse(openScreen, courseItem)//left here for a while//removed two days later
                        }
                       //perhaps add an onCLick to the chain of functions this one uses(like CourseItem.kt) and see if it works
                        //not forgetting that we are transitioning to a new screen called taskssreen that should be
                       // poped onto the stack for eadh instance of a course if selected.
                    )
                }
            }
        }
    }

    DisposableEffect(viewModel) {
        viewModel.addCourseListener()
        onDispose {
            viewModel.removeCourseListener()
        }
    }
}