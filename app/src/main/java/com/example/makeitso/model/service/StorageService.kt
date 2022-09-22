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

package com.example.makeitso.model.service

import com.example.makeitso.model.Course
import com.example.makeitso.model.Task

interface StorageService { //this provides methods that can be used to provide functionality to the viewmodel when called.
    fun addListener(
        userId: String,
        courseId: String ,
        onDocumentEvent: (Boolean, Task) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    fun getTask(courseId: String, taskId: String, onError: (Throwable) -> Unit, onSuccess: (Task) -> Unit)
    fun saveTask(courseId: String, task: Task, onResult: (Throwable?) -> Unit)//need courseid and task
    fun updateTask(courseId: String, task: Task, onResult: (Throwable?) -> Unit)
    fun deleteTask(courseId: String, taskId: String, onResult: (Throwable?) -> Unit)
    fun deleteAllForUser(userId: String, onResult: (Throwable?) -> Unit)//almost done. Doesn't delete course collection for user. Only task collection. If course collection is deleted, the individual tasks will not be deleted instead. Maybe deleting both
    fun updateUserId(oldUserId: String, newUserId: String, onResult: (Throwable?) -> Unit)//done

    fun addCourseListener(//done
        userId: String,
        onDocumentEvent: (Boolean, Course) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeCourseListener()//done
    fun getCourse(courseId: String, onError: (Throwable) -> Unit, onSuccess: (Course) -> Unit)//done here we only need an id to index the course
    fun saveCourse(course: Course, onResult: (Throwable?) -> Unit)//done here we take the entire course in order to save it
    fun updateCourse(course: Course, onResult: (Throwable?) -> Unit)//done
    fun deleteCourse(courseId: String, onResult: (Throwable?) -> Unit)//done
}
