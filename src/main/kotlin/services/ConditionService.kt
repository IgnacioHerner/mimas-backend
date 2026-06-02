package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.models.dto.ConditionResponse
import com.ignaherner.repositories.ConditionRepository
import com.ignaherner.repositories.PetRepository
import com.ignaherner.validators.CommonValidators

class ConditionService {

    private val conditionRepository = ConditionRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, nombre: String, fechaDiagnostico: String, severidad: String, estado: String, notas: String?): ConditionResponse {
        validateCreate(nombre, fechaDiagnostico, severidad, estado)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        val conditionId = conditionRepository.create(
            petId = pet.id,
            veterinarianId = veterinarianId,
            nombre = nombre,
            fechaDiagnostico = fechaDiagnostico,
            severidad = severidad,
            estado = estado,
            notas = notas
        )

        return conditionRepository.findById(conditionId)!!
    }

    fun getByPetCode(petCode: String): List<ConditionResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        return conditionRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, nombre: String?, fechaDiagnostico: String?, severidad: String?, estado: String?, notas: String?): ConditionResponse {
        validateUpdate(nombre, fechaDiagnostico, severidad, estado)

        val ownerVetId = conditionRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Condición con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la condición puede modificarla")
        }

        val updated = conditionRepository.update(id, nombre, fechaDiagnostico, severidad, estado, notas)
        if (!updated) {
            throw NotFoundException("Condición con id '$id'")
        }

        return conditionRepository.findById(id)!!
    }

    fun delete(id: Int, requestingVetId: Int) {
        val ownerVetId = conditionRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Condición con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la condición puede eliminarla")
        }

        val deleted = conditionRepository.softDelete(id)
        if (!deleted) {
            throw NotFoundException("Condición con id '$id'")
        }
    }

    private fun validateCreate(nombre: String, fechaDiagnostico: String, severidad: String, estado: String) {
        CommonValidators.validateNotBlank(nombre, "El nombre de la condición")
        CommonValidators.validateNotBlank(fechaDiagnostico, "La fecha de diagnóstico")
        CommonValidators.validateDateFormat(fechaDiagnostico, "fecha de diagnóstico")
        CommonValidators.validateNotBlank(severidad, "La severidad")
        CommonValidators.validateNotBlank(estado, "El estado")
    }

    private fun validateUpdate(nombre: String?, fechaDiagnostico: String?, severidad: String?, estado: String?) {
        CommonValidators.validateNotBlankIfPresent(nombre, "El nombre")
        if (fechaDiagnostico != null) {
            CommonValidators.validateNotBlankIfPresent(fechaDiagnostico, "La fecha de diagnóstico")
            CommonValidators.validateDateFormat(fechaDiagnostico, "fecha de diagnóstico")
        }
        CommonValidators.validateNotBlankIfPresent(severidad, "La severidad")
        CommonValidators.validateNotBlankIfPresent(estado, "El estado")
    }
}