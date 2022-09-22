package com.example.makeitso.screens.courses

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.makeitso.common.composable.DropdownContextMenu
import com.example.makeitso.common.ext.contextMenu
import com.example.makeitso.common.ext.hasDueDate
import com.example.makeitso.common.ext.hasDueTime
import com.example.makeitso.model.Course
import com.example.makeitso.R.drawable as AppIcon
import com.example.makeitso.theme.DarkOrange
import java.lang.StringBuilder

@Composable
@ExperimentalMaterialApi
fun CourseItem(
    course: Course,
    onCheckChange: () -> Unit,
    onActionClick: (String) -> Unit,
//    onSelectedCourse: (String) -> Unit//added to support click on item itself

) {
    Card(
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier.padding(4.dp, 0.dp, 4.dp, 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp, 9.dp),
                    //Modifier.fillMaxWidth(),

        ) {
//            Checkbox(
//                checked = course.completed,
//                onCheckedChange = { onCheckChange() },
//                modifier = Modifier.padding(8.dp, 0.dp)
//            )

            Column(modifier = Modifier.weight(1f)) {
                //for now we could make the text clickable
                Text( text = course.title, style = MaterialTheme.typography.subtitle2)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = getDueDateAndTime(course), fontSize = 12.sp)
                }
            }

            //i could make the entire textarea clickable or the card itself maybe by using a different
            //type of card
            //for now we could make a details button
//            Column() {
//                BasicButton(text = "", modifier = ) {
//
//                }
//            }

            if (course.flag) {
                Icon(
                    painter = painterResource(AppIcon.ic_flag),
                    tint = DarkOrange,
                    contentDescription = "Flag"
                )
            }
            //dropdown
            DropdownContextMenu(
                CoursesActionOption.getOptions(),//options given to context menu from CoursesActonOption class...i.e three options..edit flag delete
                Modifier.contextMenu(),
                onActionClick//from our courseScreen, here it is now from the course item, it's given to dropdown context menu
                // (remember it contains one argument caled action that also contains three args  i.e)
                //action -> viewModel.onCourseActionClick(
                //                            openScreen  //2. show in navigation graph
                //                            , courseItem
                //                            , action)
            )//actionClick used here
        }
    }
}

private fun getDueDateAndTime(course: Course): String {
    val stringBuilder = StringBuilder("")

    if (course.hasDueDate()) {
        stringBuilder.append(course.dueDate)
        stringBuilder.append(" ")
    }

    if (course.hasDueTime()) {
        stringBuilder.append("at ")
        stringBuilder.append(course.dueTime)
    }

    return stringBuilder.toString()
}
