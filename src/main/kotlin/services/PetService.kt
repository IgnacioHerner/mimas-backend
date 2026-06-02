package com.ignaherner.services

import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.PetResponse
import com.ignaherner.repositories.PetRepository
import com.ignaherner.validators.CommonValidators

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
        CommonValidators.validateNotBlank(name, "El nombre de la mascota")
        if (name.length < 2) throw ValidationException("El nombre de la mascota debe tener al menos 2 caracteres")
        CommonValidators.validateNotBlank(species, "La especie")
        CommonValidators.validateNotBlank(ownerName, "El nombre del dueño")
    }

    private fun validateUpdate(name: String?, species: String?) {
        if (name != null) {
            CommonValidators.validateNotBlankIfPresent(name, "El nombre")
            if (name.length < 2) throw ValidationException("El nombre debe tener al menos 2 caracteres")
        }
        CommonValidators.validateNotBlankIfPresent(species, "La especie")
    }

    private fun generateUniqueCode(name: String): String {
        var code = CodeGenerator.generatePetCode(name)
        while (repository.findByCode(code) != null) {
            code = CodeGenerator.generatePetCode(name)
        }
        return code
    }
}