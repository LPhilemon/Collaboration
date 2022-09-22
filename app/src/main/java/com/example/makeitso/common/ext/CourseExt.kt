package com.example.makeitso.common.ext

import com.example.makeitso.model.Course

fun Course?.hasDueDate(): Boolean {
    return this?.dueDate.orEmpty().isNotBlank()
}

fun Course?.hasDueTime(): Boolean {
    return this?.dueTime.orEmpty().isNotBlank()
}