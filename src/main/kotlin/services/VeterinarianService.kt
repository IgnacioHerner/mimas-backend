package com.ignaherner.services

import com.ignaherner.exceptions.EmailAlreadyExistsException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.VetWithPassword
import com.ignaherner.models.dto.VeterinarianResponse
import com.ignaherner.repositories.VeterinarianRepository

class VeterinarianService {

    private val repository = VeterinarianRepository()

    fun register(email: String, password: String, firstName: String, lastName: String, licenseNumber: String): Int {
        validateRegistration(email, password, firstName, lastName, licenseNumber)

        if (repository.findByEmail(email) != null) {
            throw EmailAlreadyExistsException(email)
        }

        val passwordHash = PasswordService.hash(password)
        return repository.create(email, passwordHash, firstName, lastName, licenseNumber)
    }

    fun getAll(): List<VeterinarianResponse> {
        return repository.findAll()
    }

    fun findByEmail(email: String): VetWithPassword? {
        return repository.findByEmail(email)
    }

    private fun validateRegistration(email: String, password: String, firstName: String, lastName: String, licenseNumber: String) {
        if (email.isBlank()) throw ValidationException("El email es obligatorio")
        if (!email.contains("@") || !email.contains(".")) throw ValidationException("El email no tiene formato válido")
        if (password.length < 6) throw ValidationException("La contraseña debe tener al menos 6 caracteres")
        if (firstName.isBlank()) throw ValidationException("El nombre es obligatorio")
        if (lastName.isBlank()) throw ValidationException("El apellido es obligatorio")
        if (licenseNumber.isBlank()) throw ValidationException("La matrícula es obligatoria")
    }
}