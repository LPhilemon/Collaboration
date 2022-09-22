package com.example.makeitso.screens.courses

enum class CoursesActionOption(val title: String) {
    OpenTasksScreen("Open Tasks"),
    EditCourse("Edit Course"),
    ToggleFlag("Toggle flag"),
    DeleteCourse("Delete Course");

    companion object {
        fun getByTitle(title: String): CoursesActionOption {
            values().forEach { action ->
                if (title == action.title) return action
            }

            return EditCourse
        }

        fun getOptions(): List<String> {
            val options = mutableListOf<String>()
            values().forEach { courseAction -> options.add(courseAction.title)
            }
            return options
        }
    }
}