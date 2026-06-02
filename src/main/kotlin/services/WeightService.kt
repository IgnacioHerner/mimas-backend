package com.ignaherner.services

import com.ignaherner.exceptions.NotFoundException
import com.ignaherner.exceptions.UnauthorizedException
import com.ignaherner.exceptions.ValidationException
import com.ignaherner.models.dto.WeightResponse
import com.ignaherner.repositories.PetRepository
import com.ignaherner.repositories.WeightRepository
import java.math.BigDecimal

class WeightService {

    private val weightRepository = WeightRepository()
    private val petRepository = PetRepository()

    fun create(petCode: String, veterinarianId: Int, peso: String, fecha: String, notas: String?): WeightResponse {
        validateCreate(peso, fecha)

        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        val weightId = weightRepository.create(
            petId = pet.id,
            veterinarianId = veterinarianId,
            peso = peso,
            fecha = fecha,
            notas = notas
        )

        return weightRepository.findById(weightId)!!
    }

    fun getByPetCode(petCode: String): List<WeightResponse> {
        val pet = petRepository.findByCode(petCode)
            ?: throw NotFoundException("Mascota con código '$petCode'")

        return weightRepository.findByPetId(pet.id)
    }

    fun update(id: Int, requestingVetId: Int, peso: String?, fecha: String?, notas: String?): WeightResponse {
        validateUpdate(peso, fecha)

        val ownerVetId = weightRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Registro de peso con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que registró el peso puede modificarlo")
        }

        val updated = weightRepository.update(id, peso, fecha, notas)
        if (!updated) {
            throw NotFoundException("Registro de peso con id '$id'")
        }

        return weightRepository.findById(id)!!
    }

    fun delete(id: Int, requestingVetId: Int) {
        val ownerVetId = weightRepository.findOwnerVetId(id)
            ?: throw NotFoundException("Registro de peso con id '$id'")

        if (ownerVetId != requestingVetId) {
            throw UnauthorizedException("Solo el veterinario que registró el peso puede eliminarlo")
        }

        val deleted = weightRepository.softDelete(id)
        if (!deleted) {
            throw NotFoundException("Registro de peso con id '$id'")
        }
    }

    private fun validateCreate(peso: String, fecha: String) {
        if (peso.isBlank()) throw ValidationException("El peso es obligatorio")
        validateWeight(peso)
        if (fecha.isBlank()) throw ValidationException("La fecha es obligatoria")
        validateDateFormat(fecha)
    }

    private fun validateUpdate(peso: String?, fecha: String?) {
        if (peso != null) {
            if (peso.isBlank()) throw ValidationException("El peso no puede estar vacío")
            validateWeight(peso)
        }
        if (fecha != null) {
            if (fecha.isBlank()) throw ValidationException("La fecha no puede estar vacía")
            validateDateFormat(fecha)
        }
    }

    private fun validateWeight(peso: String) {
        val pesoDecimal = try {
            BigDecimal(peso)
        } catch (e: NumberFormatException) {
            throw ValidationException("El peso debe ser un número válido (ej. 5.4)")
        }
        if (pesoDecimal <= BigDecimal.ZERO) {
            throw ValidationException("El peso debe ser mayor a cero")
        }
        if (pesoDecimal > BigDecimal("999.99")) {
            throw ValidationException("El peso no puede superar 999.99 kg")
        }
    }

    private fun validateDateFormat(date: String) {
        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            throw ValidationException("La fecha debe tener formato YYYY-MM-DD")
        }
    }
}