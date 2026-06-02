package com.ignaherner.services

object CodeGenerator {

    fun generatePetCode(petName: String): String {
        val prefix = petName
            .take(3)
            .uppercase()
            .padEnd(3, 'X')
        val number = (1000..9999).random()
        return "$prefix-$number"
    }
}