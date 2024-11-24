package com.telda.movieApp.data.model

enum class DepartmentTypes(val value: String) {
    ACTING("Acting"),
    DIRECTING("Director");

    companion object {
        fun fromValue(value: String?): DepartmentTypes? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}