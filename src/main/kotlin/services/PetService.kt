package com.ignaherner.services

import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.PetResponse
import com.ignaherner.repositories.PetRepository

class PetService {

    private val repository = PetRepository()

    fun create(name: String, species: String, breed: String?, birthDate: String?, ownerName: String, ownerPhone: String?, ownerEmail: String?, vetId: Int?): PetResponse {
        validateCreate(name, species, ownerName)

        val uniqueCode = generateUniqueCode(name)

        val petId = repository.create(uniqueCode, name, species, breed, birthDate, vetId)
        repository.addOwner(petId, ownerName, ownerPhone, ownerEmail)

        return repository.findByCode(uniqueCode)!!
    }

    fun findByCode(uniqueCode: String): PetResponse? {
        return repository.findByCode(uniqueCode)
    }

    fun update(uniqueCode: String, name: String?, species: String?, breed: String?, birthDate: String?): PetResponse {
        validateUpdate(name, species)

        val updated = repository.update(uniqueCode, name, species, breed, birthDate)
        if (!updated) {
            throw com.ignaherner.exceptions.NotFoundException("Mascota con código '$uniqueCode'")
        }

        return repository.findByCode(uniqueCode)!!
    }

    fun delete(uniqueCode: String) {
        val deleted = repository.softDelete(uniqueCode)
        if (!deleted) {
            throw com.ignaherner.exceptions.NotFoundException("Mascota con código '$uniqueCode'")
        }
    }

    private fun validateCreate(name: String, species: String, ownerName: String) {
        if (name.isBlank()) throw ValidationException("El nombre de la mascota es obligatorio")
        if (name.length < 2) throw ValidationException("El nombre de la mascota debe tener al menos 2 caracteres")
        if (species.isBlank()) throw ValidationException("La especie es obligatoria")
        if (ownerName.isBlank()) throw ValidationException("El nombre del dueño es obligatorio")
    }

    private fun generateUniqueCode(name: String): String {
        var code = CodeGenerator.generatePetCode(name)
        while (repository.findByCode(code) != null) {
            code = CodeGenerator.generatePetCode(name)
        }
        return code
    }

    private fun validateUpdate(name: String?, species: String?) {
        if (name != null && name.isBlank()) throw ValidationException("El nombre no puede estar vacío")
        if (name != null && name.length < 2) throw ValidationException("El nombre debe tener al menos 2 caracteres")
        if (species != null && species.isBlank()) throw ValidationException("La especie no puede estar vacía")
    }

}