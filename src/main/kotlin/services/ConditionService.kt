package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.ConditionResponse
import com.ignaherner.repositories.ConditionRepository
import com.ignaherner.repositories.PetRepository

class ConditionService {

    private val conditionRepository = ConditionRepository()
    private val petRepository = PetRepository()


    fun create(petCode: String, veterinarianId: Int, nombre: String, fechaDiagnostico: String, severidad: String, estado: String, notas: String?): ConditionResponse {
        validateCreate(nombre, fechaDiagnostico, severidad, estado)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con codigo '$petCode'")

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

    fun getByPetId(petCode: String) : List<ConditionResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con codigo '$petCode'")
        return conditionRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, nombre: String?, fechaDiagnostico: String?, severidad: String?, estado: String?, notas: String?): ConditionResponse {
        validateUpdate(nombre, fechaDiagnostico, severidad, estado)

        val ownerVetId = conditionRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Condicion con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la condición puede modificarla")
        }

        val updated = conditionRepository.update(id, nombre, fechaDiagnostico, severidad, estado, null)
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
        if (nombre.isBlank()) throw ValidationException("El nombre de la condición es obligatorio")
        if (fechaDiagnostico.isBlank()) throw ValidationException("La fecha de diagnóstico es obligatoria")
        validateDateFormat(fechaDiagnostico, "fecha de diagnóstico")
        if (severidad.isBlank()) throw ValidationException("La severidad es obligatoria")
        if (estado.isBlank()) throw ValidationException("El estado es obligatorio")
    }

    private fun validateUpdate(nombre: String?, fechaDiagnostico: String?, severidad: String?, estado: String?) {
        if (nombre != null && nombre.isBlank()) throw ValidationException("El nombre no puede estar vacío")
        if (fechaDiagnostico != null) {
            if (fechaDiagnostico.isBlank()) throw ValidationException("La fecha de diagnóstico no puede estar vacía")
            validateDateFormat(fechaDiagnostico, "fecha de diagnóstico")
        }
        if (severidad != null && severidad.isBlank()) throw ValidationException("La severidad no puede estar vacía")
        if (estado != null && estado.isBlank()) throw ValidationException("El estado no puede estar vacío")
    }


    private fun validateDateFormat(date: String, fieldName: String) {
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La $fieldName debe tener formato YYYY-MM-DD")
        }
    }
}