package com.ignaherner.validators

import com.ignaherner.exceptions.ValidationException

object CommonValidators {

    private val dateRegex = Regex("\\d{4}-\\d{2}-\\d{2}")
    private val timeRegex = Regex("\\d{2}:\\d{2}")

    fun validateDateFormat(date: String, fieldName: String) {
        if (!date.matches(dateRegex)){
            throw ValidationException("La $fieldName debe tener formato YYYY-MM-DD")
        }
    }

    fun validateTimeFormat(time: String, fieldName: String = "hora") {
        if (!time.matches(timeRegex)){
            throw ValidationException("La $fieldName debe tener formato HH:MM")
        }
    }

    fun validateNotBlank(value: String, fieldName: String) {
        if (value.isBlank()) {
            throw ValidationException("$fieldName no puede estar vacío/a")
        }
    }

    fun validateNotBlankIfPresent(value: String?, fieldName: String) {
        if (value != null && value.isBlank()) {
            throw ValidationException("$fieldName no puede estar vacío")
        }
    }
}