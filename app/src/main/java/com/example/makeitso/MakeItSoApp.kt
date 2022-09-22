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

package com.example.makeitso

import android.content.res.Resources
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.makeitso.common.snackbar.SnackbarManager
import com.example.makeitso.screens.courses.CourseScreen
import com.example.makeitso.screens.edit_course.EditCourseScreen
import com.example.makeitso.screens.edit_task.EditTaskScreen
import com.example.makeitso.screens.login.LoginScreen
import com.example.makeitso.screens.settings.SettingsScreen
import com.example.makeitso.screens.sign_up.SignUpScreen
import com.example.makeitso.screens.splash.SplashScreen
import com.example.makeitso.screens.tasks.TasksScreen
import com.example.makeitso.theme.MakeItSoTheme
import kotlinx.coroutines.CoroutineScope

@Composable
@ExperimentalMaterialApi
fun MakeItSoApp() {
    MakeItSoTheme {
        Surface(color = MaterialTheme.colors.background) {

            val appState = rememberAppState()

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colors.onPrimary)
                        }
                    )
                },
                scaffoldState = appState.scaffoldState
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) { makeItSoGraph(appState) }
            }
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
    MakeItSoAppState(scaffoldState, navController, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@ExperimentalMaterialApi
fun NavGraphBuilder.makeItSoGraph(appState: MakeItSoAppState) {
    composable(SPLASH_SCREEN) {
        SplashScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(SETTINGS_SCREEN) {
        SettingsScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(LOGIN_SCREEN) {
        LoginScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    //the pop up signifies how after entering details, once user clicks the enter button, they are taken to another screen.
    // As compared to open screen that just takes you to another screen
    // An example of popup with arguments from an item, not a user(it can use arguments i.e the case of editscreen
    // but they come from the clicked object itself. In actual sense the object being edited is passed onto the edit screen
    // and it's id indexed to allow for changes to be made.
        //the rest of the fields are referenced off that id
    }


    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(COURSES_SCREEN) {
        CourseScreen(openScreen = { route -> appState.navigate(route) })
    }
    //should pop back to course screen
//    composable(TASKS_SCREEN) {
//        TasksScreen(openScreen = { route -> appState.navigate(route) })
//    }

    //for this composable to work, we need to pass the course id to guide us to the screen with it's particular tasks
    //this probably needs a popup
    composable(route = "$TASKS_SCREEN$COURSE_ID_ARG",
                arguments = listOf(navArgument(COURSE_ID){defaultValue = COURSE_ID})) {
                //THE AIM here is to go to the tasks screen which shows tasks in a particular course
        // to do this we need the course id, this gives us connection to the tasks which all have their different ids
        //to show the different tasks under the course, we replicate what is done for editscreen,
        //once a task was chosen, the task itself contains an id and so we use that to start indexing everything to display in appropriate places on the screen
        TasksScreen(
            openScreen = { route -> appState.navigate(route) }, //questioning whether this doesn't actually need a popup
            courseId = it.arguments?.getString(COURSE_ID) ?: COURSE_ID)
    }

//    composable(route = "$EDIT_TASK_SCREEN$COURSE_ID_ARG",
//        arguments = listOf(navArgument(COURSE_ID){defaultValue = COURSE_ID})) {
//        //THE AIM here is to go to the tasks screen which shows tasks in a particular course
//        // to do this we need the course id, this gives us connection to the tasks which all have their different ids
//        //to show the different tasks under the course, we replicate what is done for editscreen,
//        //once a task was chosen, the task itself contains an id and so we use that to start indexing everything to display in appropriate places on the screen
//        EditTaskScreen(
//            openScreen = { route -> appState.navigate(route) }, //questioning whether this doesn't actually need a popup
//            courseId = it.arguments?.getString(COURSE_ID) ?: COURSE_ID)
//    }

    composable(
        route = "$EDIT_TASK_SCREEN$COURSE_ID_ARG",
        arguments = listOf(navArgument(COURSE_ID){ defaultValue = COURSE_DEFAULT_ID }) //REMOVED  { defaultValue = COURSE_DEFAULT_ID } Because a course id must exist under which we may have a new task or edited task
    ) {
        EditTaskScreen(
            popUpScreen = {
                appState.popUp()
            },
            courseId = it.arguments?.getString(COURSE_ID) ?: COURSE_ID, //default COURSE_DEFAULT_ID removed to give COURSE_ID
            taskId =  TASK_DEFAULT_ID
        )
    }

    composable(
        route = "$EDIT_TASK_SCREEN$COURSE_ID_ARG$TASK_ID_ARG",
        arguments = listOf(navArgument(COURSE_ID){ defaultValue = COURSE_DEFAULT_ID },navArgument(TASK_ID) { defaultValue = TASK_DEFAULT_ID }) //REMOVED  { defaultValue = COURSE_DEFAULT_ID } Because a course id must exist under which we may have a new task or edited task
    ) {
        EditTaskScreen(
            popUpScreen = {
                appState.popUp()
                          },
            courseId = it.arguments?.getString(COURSE_ID) ?: COURSE_ID, //default COURSE_DEFAULT_ID removed to give COURSE_ID
            taskId = it.arguments?.getString(TASK_ID) ?: TASK_DEFAULT_ID
        )
    }

    composable(//this works for two routes....EDIT_COURSE_SCREEN AND "$EDIT_COURSE_SCREEN$COURSE_ID_ARG as seen in courseviewmodel
        route = "$EDIT_COURSE_SCREEN$COURSE_ID_ARG",//$COURSE_ID={${course.id}} is defined as COURSE_ID_ARG in MakeItSoRoutes....this can be seen in MakeItSoApp.kt, we have used that instead of the full thing. We use this to cater for EDIT_COURSE_SCREEN
        // which is used in creating a new task that has it's original id as -1
        arguments = listOf(navArgument(COURSE_ID) { defaultValue = COURSE_DEFAULT_ID })
    ) {
        EditCourseScreen(
            popUpScreen = {
                appState.popUp()
                          },
            courseId = it.arguments?.getString(COURSE_ID) ?: COURSE_DEFAULT_ID
        )
    }
}



