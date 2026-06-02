package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.DewormingResponse
import com.ignaherner.repositories.DewormingRepository
import com.ignaherner.repositories.PetRepository

class DewormingService {

    private val dewormingRepository = DewormingRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, producto: String, tipo: String, fechaAplicacion: String, frecuencia: String?, proximaDosis: String?, notas: String?): DewormingResponse {
        validateCreate(producto, tipo, fechaAplicacion, proximaDosis)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        val dewormingId = dewormingRepository.create(
            petId = pet.id,
            veterinarianId = veterinarianId,
            producto = producto,
            tipo = tipo,
            fechaAplicacion = fechaAplicacion,
            frecuencia = frecuencia,
            proximaDosis = proximaDosis,
            notas = notas
        )

        return dewormingRepository.findById(dewormingId)!!
    }

    fun getByPetCode(petCode: String): List<DewormingResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        return dewormingRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, producto: String?, tipo: String?, fechaAplicacion: String?, frecuencia: String?, proximaDosis: String?, notas: String?): DewormingResponse {
        validateUpdate(producto, tipo, fechaAplicacion, proximaDosis)

        val ownerVetId = dewormingRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Desparasitación con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la desparasitación puede modificarla")
        }

        val updated = dewormingRepository.update(id, producto, tipo, fechaAplicacion, frecuencia, proximaDosis, notas)
        if (!updated) {
            throw NotFoundException("Desparasitación con id '$id'")
        }

        return dewormingRepository.findById(id)!!
    }

    fun delete(id: Int, requestingVetId: Int) {
        val ownerVetId = dewormingRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Desparasitación con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que creó la desparasitación puede eliminarla")
        }

        val deleted = dewormingRepository.softDelete(id)
        if (!deleted) {
            throw NotFoundException("Desparasitación con id '$id'")
        }
    }

    private fun validateCreate(producto: String, tipo: String, fechaAplicacion: String, proximaDosis: String?) {
        if (producto.isBlank()) throw ValidationException("El producto es obligatorio")
        if (tipo.isBlank()) throw ValidationException("El tipo es obligatorio")
        if (fechaAplicacion.isBlank()) throw ValidationException("La fecha de aplicación es obligatoria")
        validateDateFormat(fechaAplicacion, "fecha de aplicación")
        if (proximaDosis != null && proximaDosis.isNotBlank()) {
            validateDateFormat(proximaDosis, "próxima dosis")
        }
    }

    private fun validateUpdate(producto: String?, tipo: String?, fechaAplicacion: String?, proximaDosis: String?) {
        if (producto != null && producto.isBlank()) throw ValidationException("El producto no puede estar vacío")
        if (tipo != null && tipo.isBlank()) throw ValidationException("El tipo no puede estar vacío")
        if (fechaAplicacion != null) {
            if (fechaAplicacion.isBlank()) throw ValidationException("La fecha de aplicación no puede estar vacía")
            validateDateFormat(fechaAplicacion, "fecha de aplicación")
        }
        if (proximaDosis != null && proximaDosis.isNotBlank()) {
            validateDateFormat(proximaDosis, "próxima dosis")
        }
    }

    private fun validateDateFormat(date: String, fieldName: String) {
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La $fieldName debe tener formato YYYY-MM-DD")
        }
    }
}